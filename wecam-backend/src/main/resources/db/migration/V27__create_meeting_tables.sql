-- V27__create_meeting_tables.sql

-- 회의록 관련 테이블 생성
-- 요구 동작:
-- - 학생회/작성자 삭제 ⇒ 회의록은 남음 (RESTRICT)
-- - 회의록 삭제 ⇒ 참석자/첨부만 자동 삭제 (CASCADE)

-- 1. 회의록 템플릿 테이블
CREATE TABLE `meeting_template` (
    `template_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '템플릿 고유 번호',
    `name` VARCHAR(100) NOT NULL COMMENT '템플릿명',
    `description` TEXT NULL COMMENT '템플릿 설명',
    `content_template` MEDIUMTEXT NOT NULL COMMENT '마크다운 템플릿 내용',
    `is_default` BOOLEAN NULL DEFAULT NULL COMMENT '기본 템플릿 여부 (NULL: 비기본, TRUE: 기본)',
    `council_id` BIGINT NULL COMMENT '소속 학생회 ID (NULL이면 전체 공통)',
    `created_by` BIGINT NULL COMMENT '템플릿 생성자 (NULL이면 시스템 제공)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    `status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '상태',
    PRIMARY KEY (`template_id`),
    UNIQUE KEY `UK_meeting_template_default` (`council_id`, `is_default`),
    CONSTRAINT `FK_meeting_template_council`
        FOREIGN KEY (`council_id`) REFERENCES `council` (`council_id`)
            ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `FK_meeting_template_user`
        FOREIGN KEY (`created_by`) REFERENCES `council_member` (`council_member_pk_id`)
            ON DELETE SET NULL ON UPDATE CASCADE);

-- 2. 회의록 테이블
CREATE TABLE `meeting` (
    `meeting_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '회의록 고유 번호',
    `title` VARCHAR(200) NOT NULL COMMENT '회의록 제목',
    `location` VARCHAR(100) NULL COMMENT '회의 장소',
    `meeting_datetime` DATETIME NOT NULL COMMENT '회의 일시',
    `content` MEDIUMTEXT NULL COMMENT '회의 내용 (마크다운)',
    `created_by` BIGINT NOT NULL COMMENT '회의록 생성자(작성자)',
    `council_id` BIGINT NOT NULL COMMENT '소속 학생회',
    `template_id` BIGINT NULL COMMENT '사용된 템플릿 ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    `status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '상태',
    PRIMARY KEY (`meeting_id`),
    CONSTRAINT `FK_meeting_writer_council_member`
        FOREIGN KEY (`created_by`) REFERENCES `council_member` (`council_member_pk_id`)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `FK_meeting_council`
        FOREIGN KEY (`council_id`) REFERENCES `council` (`council_id`)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `FK_meeting_template`
        FOREIGN KEY (`template_id`) REFERENCES `meeting_template` (`template_id`)
            ON DELETE SET NULL ON UPDATE CASCADE);

-- 3. 회의 참석자 테이블
CREATE TABLE `meeting_attendee` (
    `attendee_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '참석자 고유 번호',
    `meeting_id` BIGINT NOT NULL COMMENT '회의록 고유 번호',
    `council_member_id` BIGINT NOT NULL COMMENT '학생회 멤버 고유 번호',
    `attendance_status` ENUM('PRESENT', 'ABSENT', 'LATE') NULL DEFAULT 'PRESENT' COMMENT '참석 상태',
    `role` ENUM('HOST', 'RECORDER', 'ATTENDEE') NULL DEFAULT 'ATTENDEE' COMMENT '회의 내 역할',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    `status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '상태',
    PRIMARY KEY (`attendee_id`),
    UNIQUE KEY `UK_meeting_attendee` (`meeting_id`, `council_member_id`),
    CONSTRAINT `FK_meeting_attendee_meeting`
        FOREIGN KEY (`meeting_id`) REFERENCES `meeting` (`meeting_id`)
            ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_meeting_attendee_council_member`
        FOREIGN KEY (`council_member_id`) REFERENCES `council_member` (`council_member_pk_id`)
            ON DELETE RESTRICT ON UPDATE CASCADE);

-- 4. 회의록 첨부파일 테이블
CREATE TABLE `meeting_file` (
    `file_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '파일 고유 번호',
    `meeting_id` BIGINT NOT NULL COMMENT '회의록 고유 번호',
    `file_name` VARCHAR(255) NOT NULL COMMENT '원본 파일명',
    `file_path` VARCHAR(500) NOT NULL COMMENT '파일 저장 경로',
    `file_url` VARCHAR(500) NOT NULL COMMENT '파일 접근용 URL',
    `file_size` BIGINT NOT NULL COMMENT '파일 크기 (bytes)',
    `file_type` VARCHAR(100) NOT NULL COMMENT '파일 타입 (MIME type)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    `status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '상태',
    PRIMARY KEY (`file_id`),
    CONSTRAINT `FK_meeting_file_meeting`
        FOREIGN KEY (`meeting_id`) REFERENCES `meeting` (`meeting_id`)
            ON DELETE CASCADE ON UPDATE CASCADE);

-- 5. 성능 최적화를 위한 인덱스 추가

-- 회의록 조회 성능 최적화
CREATE INDEX idx_meeting_council_datetime ON meeting(council_id, status, meeting_datetime DESC, meeting_id);

-- 회의록 제목/내용 전문 검색
ALTER TABLE `meeting`
    ADD FULLTEXT KEY `ft_meeting_title_content` (`title`, `content`);

-- FK 인덱스
CREATE INDEX `idx_meeting_created_by` ON `meeting`(`created_by`, `status`);
CREATE INDEX `idx_meeting_template_id` ON `meeting`(`template_id`, `status`);

-- 참석자 조회 최적화
CREATE INDEX `idx_meeting_attendee_meeting` ON `meeting_attendee`(`meeting_id`, `status`);
CREATE INDEX `idx_meeting_attendee_council_member` ON `meeting_attendee`(`council_member_id`, `status`);