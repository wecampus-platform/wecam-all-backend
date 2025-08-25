-- 할일 첨부
ALTER TABLE todo_file
    ADD COLUMN is_final TINYINT(1) NOT NULL DEFAULT 0,
    ADD COLUMN final_set_by BIGINT NULL,
    ADD COLUMN final_set_at DATETIME(6) NULL;

-- 회의록 첨부
ALTER TABLE meeting_file
    ADD COLUMN is_final TINYINT(1) NOT NULL DEFAULT 0,
    ADD COLUMN final_set_by BIGINT NULL,
    ADD COLUMN final_set_at DATETIME(6) NULL;

-- 단독 업로드
ALTER TABLE file_asset
    ADD COLUMN is_final TINYINT(1) NOT NULL DEFAULT 0;
