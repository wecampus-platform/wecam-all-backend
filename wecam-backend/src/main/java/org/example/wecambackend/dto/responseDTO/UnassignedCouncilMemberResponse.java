package org.example.wecambackend.dto.responseDTO;

public record UnassignedCouncilMemberResponse(
        Long memberId,
        String name,
        String email,
        String role // 직책 (ex. 회장, 부원)
) {}
