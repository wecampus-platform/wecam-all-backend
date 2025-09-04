ALTER TABLE file_asset
    ADD COLUMN description        TEXT NULL,
    ADD COLUMN final_status       VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    ADD COLUMN final_set_by       BIGINT NULL,
    ADD COLUMN final_set_at       DATETIME(6) NULL;  -- ← 자동 갱신/기본값 없이, 승인 시에만 직접 세팅

-- Todo / Meeting: 상태만
ALTER TABLE todo
    ADD COLUMN final_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT';

ALTER TABLE meeting
    ADD COLUMN final_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT';
