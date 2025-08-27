package org.example.wecambackend.repos.category;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.example.wecambackend.dto.response.category.CategoryDetailResponse;
import org.example.wecambackend.dto.response.category.CategoryItemFilter;
import org.example.wecambackend.dto.response.category.SortOption;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CategoryDetailNativeRepository {

    @PersistenceContext
    private EntityManager em;

    private static final String CHARSET   = "utf8mb4";
    private static final String COLLATION = "utf8mb4_general_ci"; // MySQL8 기본이면 utf8mb4_0900_ai_ci 권장


    public long countItems(Long councilId, Long categoryId, CategoryItemFilter filter) {
        String base = """
            SELECT COUNT(*) FROM (
              /* MEETING */
              SELECT m.meeting_id FROM category_assignment ca
                JOIN meeting m ON m.meeting_id = ca.entity_id
               WHERE ca.category_id = :categoryId AND ca.entity_type = 'MEETING'
                 AND ca.status='ACTIVE' AND m.status='ACTIVE' AND m.council_id=:councilId
              UNION ALL
              /* FILE_ASSET */
              SELECT f.file_id FROM category_assignment ca
                JOIN file_asset f ON f.file_id = ca.entity_id
               WHERE ca.category_id = :categoryId AND ca.entity_type = 'FILE_ASSET'
                 AND ca.status='ACTIVE' AND f.status='ACTIVE' AND f.council_id=:councilId
              UNION ALL
              /* TODO */
              SELECT t.todo_id FROM category_assignment ca
                JOIN todo t ON t.todo_id = ca.entity_id
               WHERE ca.category_id = :categoryId AND ca.entity_type = 'TODO'
                 AND ca.status='ACTIVE' AND t.status<>'DELETED' AND t.council_id=:councilId
            ) x
            """;

        String sql = switch (filter) {
            case ALL -> base;
            case MEETING -> """
                SELECT COUNT(*)
                  FROM category_assignment ca
                  JOIN meeting m ON m.meeting_id = ca.entity_id
                 WHERE ca.category_id=:categoryId AND ca.entity_type='MEETING'
                   AND ca.status='ACTIVE' AND m.status='ACTIVE' AND m.council_id=:councilId
                """;
            case FILE -> """
                SELECT COUNT(*)
                  FROM category_assignment ca
                  JOIN file_asset f ON f.file_id = ca.entity_id
                 WHERE ca.category_id=:categoryId AND ca.entity_type='FILE_ASSET'
                   AND ca.status='ACTIVE' AND f.status='ACTIVE' AND f.council_id=:councilId
                """;
            case TODO -> """
                SELECT COUNT(*)
                  FROM category_assignment ca
                  JOIN todo t ON t.todo_id = ca.entity_id
                 WHERE ca.category_id=:categoryId AND ca.entity_type='TODO'
                   AND ca.status='ACTIVE' AND t.status<>'DELETED' AND t.council_id=:councilId
                """;
        };

        Object res = em.createNativeQuery(sql)
                .setParameter("categoryId", categoryId)
                .setParameter("councilId", councilId)
                .getSingleResult();

        if (res instanceof BigInteger bi) return bi.longValue();
        if (res instanceof Number n) return n.longValue();
        return Long.parseLong(String.valueOf(res));
    }
    @SuppressWarnings("unchecked")
    public List<CategoryDetailResponse.Item> findItems(
            Long councilId, Long categoryId, CategoryItemFilter filter, SortOption sort, int offset, int limit
    ) {
        String order = sort.toSql(); // "ASC" | "DESC"

        String unionAll = """
        /* MEETING */
        SELECT
            CAST('MEETING' AS CHAR CHARACTER SET %1$s) COLLATE %2$s           AS entity_type,
            m.meeting_id                                                      AS entity_id,
            CAST(m.title AS CHAR CHARACTER SET %1$s) COLLATE %2$s             AS title,
            CAST(NULL AS CHAR CHARACTER SET %1$s) COLLATE %2$s                AS status,
            m.created_at                                                      AS created_at
          FROM category_assignment ca
          JOIN meeting m ON m.meeting_id = ca.entity_id
         WHERE ca.category_id = :categoryId AND ca.entity_type = 'MEETING'
           AND ca.status='ACTIVE' AND m.status='ACTIVE' AND m.council_id=:councilId

        UNION ALL

        /* FILE_ASSET */
        SELECT
            CAST('FILE_ASSET' AS CHAR CHARACTER SET %1$s) COLLATE %2$s        AS entity_type,
            f.file_id                                                         AS entity_id,
            CAST(COALESCE(f.original_file_name, f.stored_file_name)
                 AS CHAR CHARACTER SET %1$s) COLLATE %2$s                     AS title,
            CAST(CASE WHEN COALESCE(f.is_final,0)=1
                      THEN '최종 문서' ELSE NULL END
                 AS CHAR CHARACTER SET %1$s) COLLATE %2$s                     AS status,
            f.created_at                                                      AS created_at
          FROM category_assignment ca
          JOIN file_asset f ON f.file_id = ca.entity_id
         WHERE ca.category_id = :categoryId AND ca.entity_type = 'FILE_ASSET'
           AND ca.status='ACTIVE' AND f.status='ACTIVE' AND f.council_id=:councilId

        UNION ALL

        /* TODO */
        SELECT
            CAST('TODO' AS CHAR CHARACTER SET %1$s) COLLATE %2$s              AS entity_type,
            t.todo_id                                                         AS entity_id,
            CAST(t.title AS CHAR CHARACTER SET %1$s) COLLATE %2$s             AS title,
            CAST(CASE
                   WHEN t.progress_status IN ('COMPLETED','DONE') THEN '진행 완료'
                   WHEN t.progress_status='IN_PROGRESS'         THEN '진행 중'
                   ELSE NULL
                 END AS CHAR CHARACTER SET %1$s) COLLATE %2$s                 AS status,
            t.created_at                                                      AS created_at
          FROM category_assignment ca
          JOIN todo t ON t.todo_id = ca.entity_id
         WHERE ca.category_id = :categoryId AND ca.entity_type = 'TODO'
           AND ca.status='ACTIVE' AND t.status<>'DELETED' AND t.council_id=:councilId
        """.formatted(CHARSET, COLLATION);

        String sql = switch (filter) {
            case ALL -> "SELECT * FROM ( " + unionAll + " ) items ORDER BY created_at " + order + " LIMIT :limit OFFSET :offset";

            case MEETING -> """
            SELECT
                CAST('MEETING' AS CHAR CHARACTER SET %1$s) COLLATE %2$s AS entity_type,
                m.meeting_id AS entity_id,
                CAST(m.title AS CHAR CHARACTER SET %1$s) COLLATE %2$s    AS title,
                CAST(NULL AS CHAR CHARACTER SET %1$s) COLLATE %2$s       AS status,
                m.created_at AS created_at
              FROM category_assignment ca
              JOIN meeting m ON m.meeting_id = ca.entity_id
             WHERE ca.category_id=:categoryId AND ca.entity_type='MEETING'
               AND ca.status='ACTIVE' AND m.status='ACTIVE' AND m.council_id=:councilId
             ORDER BY m.created_at %3$s LIMIT :limit OFFSET :offset
            """.formatted(CHARSET, COLLATION, order);

            case FILE -> """
            SELECT
                CAST('FILE_ASSET' AS CHAR CHARACTER SET %1$s) COLLATE %2$s AS entity_type,
                f.file_id AS entity_id,
                CAST(COALESCE(f.original_file_name, f.stored_file_name)
                     AS CHAR CHARACTER SET %1$s) COLLATE %2$s              AS title,
                CAST(CASE WHEN COALESCE(f.is_final,0)=1
                          THEN '최종 문서' ELSE NULL END
                     AS CHAR CHARACTER SET %1$s) COLLATE %2$s              AS status,
                f.created_at AS created_at
              FROM category_assignment ca
              JOIN file_asset f ON f.file_id = ca.entity_id
             WHERE ca.category_id=:categoryId AND ca.entity_type='FILE_ASSET'
               AND ca.status='ACTIVE' AND f.status='ACTIVE' AND f.council_id=:councilId
             ORDER BY f.created_at %3$s LIMIT :limit OFFSET :offset
            """.formatted(CHARSET, COLLATION, order);

            case TODO -> """
            SELECT
                CAST('TODO' AS CHAR CHARACTER SET %1$s) COLLATE %2$s AS entity_type,
                t.todo_id AS entity_id,
                CAST(t.title AS CHAR CHARACTER SET %1$s) COLLATE %2$s  AS title,
                CAST(CASE
                       WHEN t.progress_status IN ('COMPLETED','DONE') THEN '진행 완료'
                       WHEN t.progress_status='IN_PROGRESS'           THEN '진행 중'
                       ELSE NULL END
                     AS CHAR CHARACTER SET %1$s) COLLATE %2$s         AS status,
                t.created_at AS created_at
              FROM category_assignment ca
              JOIN todo t ON t.todo_id = ca.entity_id
             WHERE ca.category_id=:categoryId AND ca.entity_type='TODO'
               AND ca.status='ACTIVE' AND t.status<>'DELETED' AND t.council_id=:councilId
             ORDER BY t.created_at %3$s LIMIT :limit OFFSET :offset
            """.formatted(CHARSET, COLLATION, order);
        };

        Query q = em.createNativeQuery(sql)
                .setParameter("categoryId", categoryId)
                .setParameter("councilId", councilId)
                .setParameter("limit", limit)
                .setParameter("offset", offset);

        List<Object[]> rows = q.getResultList();
        List<CategoryDetailResponse.Item> out = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            String type = (String) r[0];
            Long id = ((Number) r[1]).longValue();
            String title = (String) r[2];
            String status = (String) r[3];
            LocalDateTime createdAt = ((Timestamp) r[4]).toLocalDateTime();
            out.add(new CategoryDetailResponse.Item(type, id, title, status, createdAt));
        }
        return out;
    }


}
