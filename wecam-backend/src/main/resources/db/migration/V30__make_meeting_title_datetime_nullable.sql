-- V30__make_meeting_title_datetime_nullable.sql

-- 회의록 테이블의 title과 meeting_datetime 컬럼을 NULL 허용으로 변경
-- 요구사항: 회의록 생성/수정 시 필수값이 아닌 선택값으로 변경

ALTER TABLE `meeting` 
    MODIFY COLUMN `title` VARCHAR(200) NULL COMMENT '회의록 제목',
    MODIFY COLUMN `meeting_datetime` DATETIME NULL COMMENT '회의 일시';

-- 기존 인덱스는 그대로 유지 (NULL 값도 인덱싱됨)
