package org.example.wecambackend.dto.responseDTO;

import lombok.Builder;

@Builder
public class ProfileImageResponse {
    public String imageUrl;
    public String thumbnailUrl;
}