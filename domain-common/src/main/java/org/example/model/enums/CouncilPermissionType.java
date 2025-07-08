package org.example.model.enums;

/**
 * 학생회 구성원 관리 페이지에서 각 역할(CouncilDepartmentRole)에 부여할 수 있는 권한 유형 정의.
 * 각 권한은 CouncilPermissionPolicy에 설정되며, 특정 역할이 수행 가능한 기능을 의미한다.
 */
public enum CouncilPermissionType {

    MANAGE_DEPARTMENTS,     // 부서 생성, 수정, 삭제 권한

    MANAGE_ROLES,           // 해당 부서 내 역할(직책) 생성, 수정, 삭제 권한

    ASSIGN_MEMBER,          // 구성원 부서/역할 배치 권한

    REMOVE_MEMBER,          // 구성원 학생회 또는 부서에서 제거하는 권한

    UPDATE_MEMBER_ROLE,     // 구성원의 역할(Role) 또는 직책(MemberRole) 수정 권한

    VIEW_SENSITIVE_INFO     // 구성원의 연락처, 학번 등 민감 정보 열람 권한
}
