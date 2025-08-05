package org.example.wecambackend.dto.responseDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.model.enums.OrganizationType;
import org.example.model.enums.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class SubOrganizationRequestDetailResponse {

    // [ 요청 기본 정보 ]
    // 워크스페이스 생성 요청 ID
    private Long requestId;

    // 요청 처리 상태
    private RequestStatus requestStatus;

    // 생성 요청 (생성) 시각
    private LocalDateTime createdAt;
    
    // [ 대표자 정보 ]
    // 대표자 이름
    private String representativeName;

    // 대표자 이메일 (아이디)
    private String representativeEmail;

    // 대표자 전화번호
    private String representativePhone;

    // 대표자 소속
    // ex: 부산대학교 경제통상대학 경제학부
    private String representativeAffiliation;

    // 대표자 입학년도
    private Integer enrollYear;
    
    // [ 학생회 정보 ]
    // 학생회 이름
    private String councilName;

    // 학생회 단위 (총학/단대/학부)
    private OrganizationType organizationType;
    
    // 증빙자료
    private List<OrganizationRequestFileResponse> files;
    
    @Getter
    @Setter
    @Builder
    public static class OrganizationRequestFileResponse {
        private Long fileId;
        private String originalFileName;
        private String downloadUrl;
    }
} 