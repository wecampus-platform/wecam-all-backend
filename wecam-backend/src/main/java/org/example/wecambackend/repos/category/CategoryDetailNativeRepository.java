//package org.example.wecambackend.repos.category;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.persistence.Query;
//import org.example.wecambackend.dto.response.category.CategoryDetailResponse;
//import org.example.wecambackend.dto.response.category.CategoryItemFilter;
//import org.example.wecambackend.dto.response.category.SortOption;
//import org.springframework.stereotype.Repository;
//
//import java.math.BigInteger;
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Repository
//public class CategoryDetailNativeRepository {
//
//    @PersistenceContext
//    private EntityManager em;
//
//    public long countItems(Long categoryId, CategoryItemFilter filter) {
//        String base = """
//            SELECT COUNT(*) FROM (
//              /* MEETING */
//              SELECT m.id FROM category_assignment ca
//                JOIN meeting m ON m.id = ca.entity_id
//               WHERE ca.category_id = :categoryId AND ca.entity_type = 'MEETING'
//              UNION ALL
//              /* FILE */
//              SELECT f.id FROM category_assignment ca
//                JOIN file f ON f.id = ca.entity_id
//               WHERE ca.category_id = :categoryId AND ca.entity_type = 'FILE'
//              UNION ALL
//              /* TODO */
//              SELECT t.id FROM category_assignment ca
//                JOIN todo t ON t.id = ca.entity_id
//               WHERE ca.category_id = :categoryId AND ca.entity_type = 'TODO'
//            ) x
//            """;
//
//        String sql = switch (filter) {
//            case ALL -> base;
//            case MEETING -> "SELECT COUNT(*) FROM category_assignment ca WHERE ca.category_id = :categoryId AND ca.entity_type = 'MEETING'";
//            case FILE -> "SELECT COUNT(*) FROM category_assignment ca WHERE ca.category_id = :categoryId AND ca.entity_type = 'FILE'";
//            case TODO -> "SELECT COUNT(*) FROM category_assignment ca WHERE ca.category_id = :categoryId AND ca.entity_type = 'TODO'";
//        };
//
//        Query q = em.createNativeQuery(sql);
//        q.setParameter("categoryId", categoryId);
//        Object res = q.getSingleResult();
//        if (res instanceof BigInteger bi) return bi.longValue();
//        if (res instanceof Number n) return n.longValue();
//        return Long.parseLong(String.valueOf(res));
//    }
//
//    @SuppressWarnings("unchecked")
//    public List<CategoryDetailResponse.Item> findItems(Long categoryId, CategoryItemFilter filter, SortOption sort, int offset, int limit) {
//        String order = sort.toSql();
//
//        String unionAll = """
//            /* MEETING */
//            SELECT 'MEETING' AS entity_type, m.id AS entity_id, m.title AS title,
//                   NULL AS status, m.created_at AS created_at
//              FROM category_assignment ca
//              JOIN meeting m ON m.id = ca.entity_id
//             WHERE ca.category_id = :categoryId AND ca.entity_type = 'MEETING'
//            UNION ALL
//            /* FILE */
//            SELECT 'FILE' AS entity_type, f.id AS entity_id, f.title AS title,
//                   CASE WHEN COALESCE(f.is_final, 0) = 1 THEN '최종 문서' ELSE NULL END AS status,
//                   f.created_at AS created_at
//              FROM category_assignment ca
//              JOIN file f ON f.id = ca.entity_id
//             WHERE ca.category_id = :categoryId AND ca.entity_type = 'FILE'
//            UNION ALL
//            /* TODO */
//            SELECT 'TODO' AS entity_type, t.id AS entity_id, t.title AS title,
//                   CASE
//                     WHEN t.status = 'DONE' THEN '진행 완료'
//                     WHEN t.status = 'IN_PROGRESS' THEN '진행 중'
//                     ELSE NULL
//                   END AS status,
//                   t.created_at AS created_at
//              FROM category_assignment ca
//              JOIN todo t ON t.id = ca.entity_id
//             WHERE ca.category_id = :categoryId AND ca.entity_type = 'TODO'
//            """;
//
//        String sql = switch (filter) {
//            case ALL -> "SELECT * FROM ( " + unionAll + " ) items ORDER BY created_at " + order + " LIMIT :limit OFFSET :offset";
//            case MEETING -> """
//                SELECT 'MEETING' AS entity_type, m.id, m.title, NULL AS status, m.created_at
//                  FROM category_assignment ca
//                  JOIN meeting m ON m.id = ca.entity_id
//                 WHERE ca.category_id = :categoryId AND ca.entity_type = 'MEETING'
//                 ORDER BY m.created_at %s LIMIT :limit OFFSET :offset
//                """.formatted(order);
//            case FILE -> """
//                SELECT 'FILE' AS entity_type, f.id, f.title,
//                       CASE WHEN COALESCE(f.is_final,0)=1 THEN '최종 문서' ELSE NULL END AS status,
//                       f.created_at
//                  FROM category_assignment ca
//                  JOIN file f ON f.id = ca.entity_id
//                 WHERE ca.category_id = :categoryId AND ca.entity_type = 'FILE'
//                 ORDER BY f.created_at %s LIMIT :limit OFFSET :offset
//                """.formatted(order);
//            case TODO -> """
//                SELECT 'TODO' AS entity_type, t.id, t.title,
//                       CASE WHEN t.status='DONE' THEN '진행 완료'
//                            WHEN t.status='IN_PROGRESS' THEN '진행 중'
//                            ELSE NULL END AS status,
//                       t.created_at
//                  FROM category_assignment ca
//                  JOIN todo t ON t.id = ca.entity_id
//                 WHERE ca.category_id = :categoryId AND ca.entity_type = 'TODO'
//                 ORDER BY t.created_at %s LIMIT :limit OFFSET :offset
//                """.formatted(order);
//        };
//
//        Query q = em.createNativeQuery(sql);
//        q.setParameter("categoryId", categoryId);
//        q.setParameter("limit", limit);
//        q.setParameter("offset", offset);
//
//        List<Object[]> rows = q.getResultList();
//        List<CategoryDetailResponse.Item> out = new ArrayList<>(rows.size());
//        for (Object[] r : rows) {
//            String type = (String) r[0];
//            Long id = ((Number) r[1]).longValue();
//            String title = (String) r[2];
//            String status = (String) r[3];
//            LocalDateTime createdAt = ((Timestamp) r[4]).toLocalDateTime();
//            out.add(new CategoryDetailResponse.Item(type, id, title, status, createdAt));
//        }
//        return out;
//    }
//}
