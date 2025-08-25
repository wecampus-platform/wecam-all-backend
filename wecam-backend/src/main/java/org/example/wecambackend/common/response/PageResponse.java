package org.example.wecambackend.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
    private List<T> items;   // 실제 데이터
    private int page;        // 현재 페이지 (1부터 시작)
    private int size;        // 페이지 크기
    private long total;      // 전체 데이터 개수
    private int totalPages;  // 전체 페이지 수

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber() + 1, // Page는 0-based, 프론트는 보통 1-based
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
