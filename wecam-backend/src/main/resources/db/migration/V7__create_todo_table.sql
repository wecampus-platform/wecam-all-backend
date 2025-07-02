CREATE TABLE `todo` (
                    `todo_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '할일 고유 번호',
                    `create_user_id` BIGINT NOT NULL COMMENT '생성자 고유 번호',
                    `title` VARCHAR(20) NOT NULL COMMENT '제목',
                    `content` TEXT NULL COMMENT '내용',
                    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
                    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
                    `due_at` DATETIME NOT NULL COMMENT '마감 시간',
                    `progress_status` ENUM('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED') NOT NULL DEFAULT 'NOT_STARTED' COMMENT '진행 상태',
                    PRIMARY KEY (`todo_id`),
                    CONSTRAINT `FK_todo_user` FOREIGN KEY (`create_user_id`) REFERENCES `user` (`user_pk_id`)
);

CREATE TABLE `todo_manager` (
                    `todo_id` BIGINT NOT NULL COMMENT '할일 고유 번호',
                    `user_pk_id` BIGINT NOT NULL COMMENT '담당자 고유 번호',
                    PRIMARY KEY (`todo_id`, `user_pk_id`),
                    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
                    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
                    CONSTRAINT `FK_todo_manager_todo` FOREIGN KEY (`todo_id`) REFERENCES `todo` (`todo_id`),
                    CONSTRAINT `FK_todo_manager_user` FOREIGN KEY (`user_pk_id`) REFERENCES `user` (`user_pk_id`)
);

CREATE TABLE `todo_file` (
                     `todo_file_id` BINARY(16) NOT NULL COMMENT '파일 고유 ID (UUID)',
                     `todo_id` BIGINT NOT NULL COMMENT '할일 고유 번호',
                     `original_file_name` VARCHAR(255) NOT NULL COMMENT '원본 파일명',
                     `stored_file_name` VARCHAR(255) NOT NULL COMMENT '저장 파일명',
                     `file_path` VARCHAR(255) NOT NULL COMMENT 'S3 Key 또는 경로',
                     `file_url` VARCHAR(255) NULL COMMENT '파일 접근 URL',
                     `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
                     `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
                     PRIMARY KEY (`todo_file_id`),
                     CONSTRAINT `FK_todo_file_todo` FOREIGN KEY (`todo_id`) REFERENCES `todo` (`todo_id`)
);
