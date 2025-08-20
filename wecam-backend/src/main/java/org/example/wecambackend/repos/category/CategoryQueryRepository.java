package org.example.wecambackend.repos.category;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.dto.response.category.CategorySummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@RequiredArgsConstructor
/*
특정 학생회(councilId)의 카테고리를 전부 가져오고

각 카테고리별로

TODO 몇 개,

SCHEDULE 몇 개,

MEETING 몇 개인지 집계

결과를 CategorySummary DTO로 매핑해서 반환.
 */
public class CategoryQueryRepository {
    private final EntityManager em;

    public Page<CategorySummary> summaries(Long councilId, String q, Pageable pageable) {
        String like = (q == null || q.isBlank()) ? "%" : "%" + q.trim() + "%";

        Long total = ((Number) em.createNativeQuery("""
            SELECT COUNT(*)
            FROM category c
            WHERE c.council_id = :cid
              AND c.name LIKE :q
        """)
                .setParameter("cid", councilId)
                .setParameter("q", like)
                .getSingleResult()).longValue();

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
              c.id,
              c.name,
              SUM(CASE WHEN a.entity_type = 'TODO'     THEN 1 ELSE 0 END)    AS todo_cnt,
              SUM(CASE WHEN a.entity_type = 'SCHEDULE' THEN 1 ELSE 0 END)    AS schedule_cnt,
              SUM(CASE WHEN a.entity_type = 'MEETING'  THEN 1 ELSE 0 END)    AS meeting_cnt
            FROM category c
            LEFT JOIN category_assignment a
              ON a.category_id = c.id
            WHERE c.council_id = :cid
              AND c.name LIKE :q
            GROUP BY c.id, c.name
            ORDER BY c.id DESC
            LIMIT :limit OFFSET :offset
        """)
                .setParameter("cid", councilId)
                .setParameter("q", like)
                .setParameter("limit", pageable.getPageSize())
                .setParameter("offset", (int) pageable.getOffset())
                .getResultList();

        List<CategorySummary> content = rows.stream().map(r ->
                new CategorySummary(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        ((Number) r[2]).intValue(),
                        ((Number) r[3]).intValue(),
                        ((Number) r[4]).intValue()
                )
        ).toList();

        return new PageImpl<>(content, pageable, total);
    }
}
