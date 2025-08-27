package org.example.wecambackend.dto.request;

import io.micrometer.common.lang.Nullable;


import java.util.List;

public record FileUploadRequest(
        @Nullable List<Long> categoryIds,
        @Nullable Boolean isFinal
) {
}
