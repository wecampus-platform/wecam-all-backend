package org.example.model.enums;

/**
 * 학생회 구성원 관리 페이지에서 각 역할(CouncilDepartmentRole)에 부여할 수 있는 권한 유형 정의.
 * 각 권한은 CouncilPermissionPolicy에 설정되며, 특정 역할이 수행 가능한 기능을 의미한다.
 */
public enum CouncilPermissionType {

    // 학생회 관리자 페이지 접근 권한
    ACCESS_ADMIN_PAGE("학생회 관리자 페이지 접속"),

    // 구성원 권한 부여 (예: 부서장, 일반 구성원 등)
    GRANT_ROLE("구성원 권한 부여"),

    // 구성원 목록 조회
    VIEW_MEMBERS("구성원 열람"),

    // 구성원 제명 처리
    REMOVE_MEMBER("구성원 제명"),

    // 부서 생성 및 부서명 수정
    MANAGE_DEPARTMENT("부서 생성 및 부서명 변경"),

    // 부서에 인원 배치
    ASSIGN_TO_DEPARTMENT("부서 배정"),

    // 초대 코드 생성 (학생회 or 일반)
    GENERATE_INVITATION_CODE("초대코드 생성"),

    // 소속 인증 요청 승인/반려
    PROCESS_AFFILIATION_VERIFICATION("소속인증 처리"),

    // 공지사항 작성, 수정, 삭제 등
    MANAGE_NOTICE("공지 관리");

    private final String description;

    CouncilPermissionType (String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
