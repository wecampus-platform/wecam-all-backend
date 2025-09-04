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
            // ✅ 부모/파일자체 둘 다 인식하는 카테고리 필터
            where.append("""
            AND (
              EXISTS (
                SELECT 1 FROM category_assignment ca
                 WHERE ca.status='ACTIVE'
                   AND ca.entity_type = F.entity_type
                   AND ca.entity_id   = F.entity_id
                   AND ca.category_id = :categoryId
              )
              OR
              EXISTS (
                SELECT 1 FROM category_assignment ca
                 WHERE ca.status='ACTIVE'
                   AND ca.entity_type = 'FILE_ASSET'
                   AND ca.entity_id   = F.file_id
                   AND ca.category_id = :categoryId
              )
            )
            """);
            // 만약 category_assignment.entity_id가 BINARY(16)이면 위 F.file_id 대신
            // AND ca.entity_id = UUID_TO_BIN(F.file_id, 1)
        }
        if (f.query() != null && !f.query().isBlank()) {
            where.append(" AND (F.file_name LIKE :q OR U.name LIKE :q) ");
        }

        // ✅ SELF + PARENT 둘 다 조인해서 합치기
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
          NULLIF(CONCAT_WS(',', CAP.category_names, CAS.category_names), '') AS category_names,
          F.uploaded_at,
          F.is_final
        FROM v_file_library F
        LEFT JOIN user U
               ON U.user_pk_id = F.uploader_id
        -- 부모 카테고리
        LEFT JOIN v_category_agg CAP
               ON CAP.entity_type = F.entity_type
              AND CAP.entity_id   = F.entity_id
        -- 파일 자체 카테고리
        LEFT JOIN v_category_agg CAS
               ON CAS.entity_type = 'FILE_ASSET'
              AND CAS.entity_id   = F.file_id
        """ + where + """
        ORDER BY F.uploaded_at DESC, F.file_id DESC
        LIMIT :limit OFFSET :offset
        """;
        // BINARY(16) UUID 사용 시:
        // AND CAS.entity_id = UUID_TO_BIN(F.file_id, 1)

        Query dq = em.createNativeQuery(dataSql);
        bindParams(dq, f);
        dq.setParameter("limit", pageable.getPageSize());
        dq.setParameter("offset", (int) pageable.getOffset());

        @SuppressWarnings("unchecked")
        List<Object[]> rows = dq.getResultList();

        List<FileItemDto> items = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            // uploaded_at 타입 안전 변환
            LocalDateTime uploadedAt = null;
            Object upObj = r[10];
            if (upObj != null) {
                if (upObj instanceof java.sql.Timestamp ts) uploadedAt = ts.toLocalDateTime();
                else if (upObj instanceof LocalDateTime ldt) uploadedAt = ldt;
                else throw new IllegalStateException("Unexpected type for uploaded_at: " + upObj.getClass());
            }
            boolean isFinal;
            Object isFinalObj = r[11];
            if (isFinalObj instanceof Boolean b) isFinal = b; else isFinal = ((Number) isFinalObj).intValue() != 0;

            items.add(new FileItemDto(
                    (String) r[0],                          // source_type
                    (String) r[1],                          // entity_type
                    // entity_id가 정수라면:
                    ((Number) r[2]).longValue(),            // entity_id
                    // 만약 entity_id가 UUID 문자열로 바뀐다면 DTO/매핑 타입도 String으로 바꿔주세요.
                    (String) r[3],                          // file_id (CHAR(36))
                    (String) r[4],                          // source_title
                    (String) r[5],                          // file_name
                    ((Number) r[6]).longValue(),            // council_id
                    r[7] == null ? null : ((Number) r[7]).longValue(), // uploader_id
                    (String) r[8],                          // uploader_name
                    (String) r[9],                          // category_names (SELF+PARENT 합친 값)
                    uploadedAt,                             // LocalDateTime
                    isFinal
            ));
        }

        String countSql = """
        SELECT COUNT(*)
        FROM v_file_library F
        LEFT JOIN user U
               ON U.user_pk_id = F.uploader_id
        -- 부모
        LEFT JOIN v_category_agg CAP
               ON CAP.entity_type = F.entity_type
              AND CAP.entity_id   = F.entity_id
        -- 파일자체
        LEFT JOIN v_category_agg CAS
               ON CAS.entity_type = 'FILE_ASSET'
              AND CAS.entity_id   = F.file_id
        """ + where;

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
