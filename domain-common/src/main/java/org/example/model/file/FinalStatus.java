package org.example.model.file;

public enum FinalStatus {
    DRAFT,      // 기본값 (초안/일반)
    PENDING,    // 최종본 신청됨(승인 대기중)
    APPROVED,   // 승인됨 (최종본)
    REJECTED    // 반려됨
}
