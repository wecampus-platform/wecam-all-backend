package org.example.wecambackend.dto.request;

import io.micrometer.common.lang.Nullable;


import java.util.List;

public record FileUploadRequest(
        @Nullable List<Long> categoryIds,
        Boolean isFinal, // 최종 권한자
        String description,
        Boolean requestFinal  // 일반 사용자: 최종문서 신청

) {
}
