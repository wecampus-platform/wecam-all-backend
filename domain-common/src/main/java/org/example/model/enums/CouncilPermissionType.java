package org.example.model.enums;

/**
 * 학생회 구성원 관리 페이지에서 각 역할(CouncilDepartmentRole)에 부여할 수 있는 권한 유형 정의.
 * 각 권한은 CouncilPermissionPolicy에 설정되며, 특정 역할이 수행 가능한 기능을 의미한다.
 */
public enum CouncilPermissionType {

    // 1. 학생회 관리자 페이지 접속
    ACCESS_ADMIN_PAGE("학생회 관리자 페이지 접속"),

    // 2. 권한 부여 가능 여부 (구성원 에게 역할 및 권한 설정 가능)
    GRANT_ROLE("구성원 권한 부여"),

    // 3. 구성원 목록 열람
    VIEW_MEMBERS("구성원 열람"),

    // 4. 구성원 제명
    REMOVE_MEMBER("구성원 제명"),

    // 5. 부서 생성 및 부서명 변경
    MANAGE_DEPARTMENT("부서 생성 및 부서명 변경"),

    // 6. 부서 배정 (인원 배치)
    ASSIGN_TO_DEPARTMENT("부서 배정"),

    // 7. 초대 코드 생성
    GENERATE_INVITATION_CODE("초대 코드 생성"),

    // 8. 소속 인증 요청 승인/반려
    PROCESS_AFFILIATION_VERIFICATION("소속 인증 처리"),

    // 9. 공지 사항 작성/수정/삭제
    MANAGE_NOTICE("공지사항 관리"),

    // 10. 학생 개인 정보 (전화 번호와 학번 까지 _ 마스킹 없이 볼수 있게. )
    VIEW_STUDENT_PRIVATE_INFO("학생 개인 정보 조회");

    private final String description;

    CouncilPermissionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
