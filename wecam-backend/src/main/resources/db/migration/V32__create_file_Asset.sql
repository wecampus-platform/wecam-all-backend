-- 파일 원본 테이블 (운영문서함/첨부 공용)

CREATE TABLE `file_asset` (
                              `file_id`           BIGINT       NOT NULL AUTO_INCREMENT,
                              `uuid`              BINARY(16)   NOT NULL,
    `original_file_name` VARCHAR(255) NOT NULL,
    `stored_file_name`   VARCHAR(255) NOT NULL,
    `file_path`          VARCHAR(512) NOT NULL,   -- 서버 내부 저장 경로 (베이스 루트 + 상대경로 조합 권장)
    `file_url`           VARCHAR(512)     NULL,   -- 필요 시 외부 접근 URL(없으면 NULL)

    `council_id`        BIGINT       NOT NULL,   -- FK: council(id)
    `user_pk_id`        BIGINT       NOT NULL,   -- FK: user(user_pk_id)

  created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) NOT NULL COMMENT '생성일',
  updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) NOT NULL ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일',
  status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '상태',

    CONSTRAINT `pk_file_Asset` PRIMARY KEY (`file_id`),
    CONSTRAINT `uq_file_Asset_uuid` UNIQUE KEY (`uuid`),

    KEY `idx_file_Asset_council` (`council_id`),
    KEY `idx_file_Asset_user`    (`user_pk_id`),
    KEY `idx_file_Asset_created` (`created_at`),

    CONSTRAINT `fk_file_Asset_council`
    FOREIGN KEY (`council_id`) REFERENCES `council`(`council_id`)
                                                                           ON UPDATE CASCADE
                                                                           ON DELETE RESTRICT,

    CONSTRAINT `fk_file_Asset_user`
    FOREIGN KEY (`user_pk_id`) REFERENCES `user`(`user_pk_id`)
                                                                           ON UPDATE CASCADE
                                                                           ON DELETE RESTRICT
    ) ENGINE=InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;
