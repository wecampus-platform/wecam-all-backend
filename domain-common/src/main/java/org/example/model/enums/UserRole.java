package org.example.model.enums;

public enum UserRole {
    UNAUTH,       //  소속 인증 전
    GUEST_STUDENT, //신입생 인증만 진행한 학생
    STUDENT,     // 재학생 인증 진행한 학생
    COUNCIL,     // 학생회 구성원
    ADMIN        // 서버 관리자
}
