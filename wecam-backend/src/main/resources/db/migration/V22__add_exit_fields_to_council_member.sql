-- V22__add_exit_fields_to_council_member.sql

-- council_member 테이블에 탈퇴/만료 관련 필드 추가
ALTER TABLE council_member
    ADD COLUMN exit_type ENUM('ACTIVE', 'GRADUATION', 'EXPULSION', 'RESIGNATION') NOT NULL DEFAULT 'ACTIVE',
    ADD COLUMN expulsion_reason VARCHAR(500) NULL,
    ADD COLUMN exit_date TIMESTAMP(6) NULL;