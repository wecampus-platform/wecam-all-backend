package org.example.model.user;


//탈퇴해도 기록 보존 필요할 땐 WITHDRAWN 상태로 바꾸고, 실제 삭제는 안 함
//상태관리 -> role 과 다른 거. 정지 / 활동 여부임.
public enum UserStatus {
    ACTIVE,       // 정상
    SUSPENDED,    // 정지 (일시적 차단)
    WITHDRAWN,    // 탈퇴
    BANNED        // 영구 차단 (악의적 사용자 등)
}
