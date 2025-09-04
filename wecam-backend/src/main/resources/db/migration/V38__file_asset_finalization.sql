-- 0) 안전옵션
SET @OLD_SQL_SAFE_UPDATES := @@SQL_SAFE_UPDATES;
SET SQL_SAFE_UPDATES = 0;

-- 1) 테이블이 없으면 새로 생성
CREATE TABLE IF NOT EXISTS file_asset_finalization (
                                                       finalization_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                       entity_type       VARCHAR(30)  NOT NULL,             -- CategoryAssignment.EntityType 을 문자열로 저장
                                                       entity_id         BIGINT       NOT NULL,             -- 타겟 엔티티 PK
                                                       final_status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING',  -- PENDING/APPROVED/REJECTED
                                                       requested_by_id   BIGINT       NOT NULL,
                                                       requested_at      DATETIME(6)  NOT NULL,
                                                       approved_by_id    BIGINT       NULL,
                                                       approved_at       DATETIME(6)  NULL
    -- 필요 시 외래키 추가 (테이블/컬럼명 맞춰 수정)
    -- ,CONSTRAINT fk_faf_requested_by FOREIGN KEY (requested_by_id) REFERENCES user(user_pk_id)
    -- ,CONSTRAINT fk_faf_approved_by  FOREIGN KEY (approved_by_id)  REFERENCES user(user_pk_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2) 컬럼 존재/부재에 따라 정리(ALTER): 동적 SQL로 안전하게 처리
-- 2-1) status -> final_status로 변경(있다면)
SET @col_status := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='file_asset_finalization' AND COLUMN_NAME='status'
    );
SET @sql := IF(@col_status>0,
    'ALTER TABLE file_asset_finalization CHANGE COLUMN `status` `final_status` VARCHAR(20) NOT NULL DEFAULT ''PENDING'';',
    'DO 0'
    );
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2-2) final_status 없으면 추가
SET @col_final_status := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='file_asset_finalization' AND COLUMN_NAME='final_status'
    );
SET @sql := IF(@col_final_status=0,
    'ALTER TABLE file_asset_finalization ADD COLUMN `final_status` VARCHAR(20) NOT NULL DEFAULT ''PENDING'' AFTER entity_id;',
    'DO 0'
    );
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2-3) 과거 스키마에서 쓰던 컬럼들 제거 (있으면)
-- file_id, target_type, target_id, reject_reason 등
SET @drop_cols := '';
SELECT GROUP_CONCAT(CONCAT('DROP COLUMN `', COLUMN_NAME, '`') SEPARATOR ', ')
INTO @drop_cols
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME   = 'file_asset_finalization'
  AND COLUMN_NAME IN ('file_id','target_type','target_id','reject_reason');

SET @sql := IF(@drop_cols IS NOT NULL AND @drop_cols <> '',
    CONCAT('ALTER TABLE file_asset_finalization ', @drop_cols, ';'),
    'DO 0'
    );
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2-4) entity_type 없으면 추가
SET @col_entity_type := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='file_asset_finalization' AND COLUMN_NAME='entity_type'
    );
SET @sql := IF(@col_entity_type=0,
    'ALTER TABLE file_asset_finalization ADD COLUMN `entity_type` VARCHAR(30) NOT NULL AFTER finalization_id;',
    'DO 0'
    );
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2-5) entity_id 없으면 추가
SET @col_entity_id := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='file_asset_finalization' AND COLUMN_NAME='entity_id'
    );
SET @sql := IF(@col_entity_id=0,
    'ALTER TABLE file_asset_finalization ADD COLUMN `entity_id` BIGINT NOT NULL AFTER entity_type;',
    'DO 0'
    );
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2-6) requested_by_id 없으면 추가
SET @col_requested_by_id := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='file_asset_finalization' AND COLUMN_NAME='requested_by_id'
    );
SET @sql := IF(@col_requested_by_id=0,
    'ALTER TABLE file_asset_finalization ADD COLUMN `requested_by_id` BIGINT NOT NULL AFTER final_status;',
    'DO 0'
    );
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2-7) requested_at 없으면 추가
SET @col_requested_at := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='file_asset_finalization' AND COLUMN_NAME='requested_at'
    );
SET @sql := IF(@col_requested_at=0,
    'ALTER TABLE file_asset_finalization ADD COLUMN `requested_at` DATETIME(6) NOT NULL AFTER requested_by_id;',
    'DO 0'
    );
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2-8) approved_by_id 없으면 추가
SET @col_approved_by_id := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='file_asset_finalization' AND COLUMN_NAME='approved_by_id'
    );
SET @sql := IF(@col_approved_by_id=0,
    'ALTER TABLE file_asset_finalization ADD COLUMN `approved_by_id` BIGINT NULL AFTER requested_at;',
    'DO 0'
    );
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2-9) approved_at 없으면 추가
SET @col_approved_at := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='file_asset_finalization' AND COLUMN_NAME='approved_at'
    );
SET @sql := IF(@col_approved_at=0,
    'ALTER TABLE file_asset_finalization ADD COLUMN `approved_at` DATETIME(6) NULL AFTER approved_by_id;',
    'DO 0'
    );
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3) 인덱스 정리 (있으면 유지, 없으면 생성)
-- 3-1) (entity_type, entity_id)
SET @idx := (
    SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='file_asset_finalization' AND INDEX_NAME='idx_faf_target'
    );
SET @sql := IF(@idx=0,
    'CREATE INDEX idx_faf_target ON file_asset_finalization (entity_type, entity_id);',
    'DO 0'
    );
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3-2) final_status
SET @idx := (
    SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='file_asset_finalization' AND INDEX_NAME='idx_faf_final_status'
    );
SET @sql := IF(@idx=0,
    'CREATE INDEX idx_faf_final_status ON file_asset_finalization (final_status);',
    'DO 0'
    );
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3-3) requested_at
SET @idx := (
    SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='file_asset_finalization' AND INDEX_NAME='idx_faf_requested_at'
    );
SET @sql := IF(@idx=0,
    'CREATE INDEX idx_faf_requested_at ON file_asset_finalization (requested_at);',
    'DO 0'
    );
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- (선택) 외래키 추가: 실제 사용자 테이블/PK 이름에 맞춰 수정
-- 4-1) requested_by FK
-- ALTER TABLE file_asset_finalization
--   ADD CONSTRAINT fk_faf_requested_by
--   FOREIGN KEY (requested_by_id) REFERENCES user(user_pk_id);

-- 4-2) approved_by FK
-- ALTER TABLE file_asset_finalization
--   ADD CONSTRAINT fk_faf_approved_by
--   FOREIGN KEY (approved_by_id) REFERENCES user(user_pk_id);

-- 5) 롤백 안전옵션 원복
SET SQL_SAFE_UPDATES = @OLD_SQL_SAFE_UPDATES;
