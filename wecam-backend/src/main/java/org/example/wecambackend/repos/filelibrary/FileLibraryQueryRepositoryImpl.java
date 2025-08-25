package org.example.wecambackend.repos.filelibrary;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.dto.projection.FileItemDto;
import org.example.wecambackend.dto.projection.FileLibraryFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FileLibraryQueryRepositoryImpl implements FileLibraryQueryRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Page<FileItemDto> search(FileLibraryFilter f, Pageable pageable) {
        StringBuilder where = new StringBuilder(" WHERE F.council_id = :councilId ");

        if (f.sourceType() != null && !f.sourceType().isBlank()) {
            where.append(" AND F.source_type = :sourceType ");
        }
        if (Boolean.TRUE.equals(f.finalOnly())) {
            where.append(" AND F.is_final = 1 ");
        }
        if (f.categoryId() != null) {
            where.append("""
                    AND EXISTS (
                      SELECT 1 FROM category_assignment ca
                       WHERE ca.status='ACTIVE'
                         AND ca.entity_type = F.entity_type
                         AND ca.entity_id   = F.entity_id
                         AND ca.category_id = :categoryId
                    )
                    """);
        }
        if (f.query() != null && !f.query().isBlank()) {
            // 파일명 또는 업로더명 like
            where.append(" AND (F.file_name LIKE :q OR U.name LIKE :q) ");
        }

        // 데이터 쿼리
        String dataSql = """
            SELECT
              F.source_type,
              F.entity_type,
              F.entity_id,
              F.file_id,
              F.source_title,
              F.file_name,
              F.council_id,
              F.uploader_id,
              U.name AS uploader_name,
              CA.category_names,
              F.uploaded_at,
              F.is_final
            FROM v_file_library F
            LEFT JOIN v_category_agg CA
                   ON CA.entity_type = F.entity_type AND CA.entity_id = F.entity_id
            LEFT JOIN user U
                   ON U.user_pk_id = F.uploader_id
            """ + where + """
            ORDER BY F.uploaded_at DESC, F.file_id DESC
            LIMIT :limit OFFSET :offset
            """;

        Query dq = em.createNativeQuery(dataSql);
        bindParams(dq, f);
        dq.setParameter("limit", pageable.getPageSize());
        dq.setParameter("offset", (int) pageable.getOffset());

        @SuppressWarnings("unchecked")
        List<Object[]> rows = dq.getResultList();

        List<FileItemDto> items = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            items.add(new FileItemDto(
                    (String) r[0],                         // source_type
                    (String) r[1],                         // entity_type
                    ((Number) r[2]).longValue(),           // entity_id
                    (String) r[3],                         // file_id
                    (String) r[4],                         // source_title
                    (String) r[5],                         // file_name
                    ((Number) r[6]).longValue(),           // council_id
                    r[7] == null ? null : ((Number) r[7]).longValue(), // uploader_id
                    (String) r[8],                         // uploader_name
                    (String) r[9],                         // category_names
                    (java.sql.Timestamp) r[10],                 // uploaded_at
                    ((Number) r[11]).intValue() == 1       // is_final
            ));
        }

        // 카운트 쿼리 (SQL_CALC_FOUND_ROWS 금지)
        String countSql = "SELECT COUNT(*) FROM v_file_library F " +
                " LEFT JOIN user U ON U.user_pk_id=F.uploader_id " + where;

        Query cq = em.createNativeQuery(countSql);
        bindParams(cq, f);

        long total = ((Number) cq.getSingleResult()).longValue();
        return new PageImpl<>(items, pageable, total);
    }

    private void bindParams(Query q, FileLibraryFilter f) {
        q.setParameter("councilId", f.councilId());
        if (f.sourceType() != null && !f.sourceType().isBlank()) {
            q.setParameter("sourceType", f.sourceType());
        }
        if (f.categoryId() != null) {
            q.setParameter("categoryId", f.categoryId());
        }
        if (f.query() != null && !f.query().isBlank()) {
            q.setParameter("q", "%" + f.query().trim() + "%");
        }
    }
}
