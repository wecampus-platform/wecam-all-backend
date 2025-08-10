package org.example.wecambackend.dto.response;

import lombok.Builder;

@Builder
public class ProfileImageResponse {
    // 프로필 원본 url
    public String imageUrl;

    // 프로필 썸네일 url
    public String thumbnailUrl;
}