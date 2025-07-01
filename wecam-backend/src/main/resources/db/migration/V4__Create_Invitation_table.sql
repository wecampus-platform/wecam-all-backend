CREATE TABLE invitation_code (
                                 invitation_pk_id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                 council_id BIGINT NOT NULL,
                                 user_pk_id BIGINT,
                                 code VARCHAR(20) NOT NULL,

                                 usage_count INT NOT NULL DEFAULT 0,
                                 organization_id BIGINT NOT NULL,

                                 code_type VARCHAR(50) NOT NULL, -- ENUM 매핑
                                 usage_limit INT,
                                 is_usage_limit BOOLEAN,
                                 is_active BOOLEAN NOT NULL DEFAULT TRUE,

                                 created_at DATETIME NOT NULL,
                                 updated_at DATETIME NOT NULL,

                                 CONSTRAINT fk_invitation_code_council FOREIGN KEY (council_id) REFERENCES council(council_id),
                                 CONSTRAINT fk_invitation_code_user FOREIGN KEY (user_pk_id) REFERENCES user(user_pk_id),
                                 CONSTRAINT fk_invitation_code_organization FOREIGN KEY (organization_id) REFERENCES organization(organization_id)
);


CREATE TABLE invitation_history (
                                    history_pk_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    used_at DATETIME NOT NULL,

                                    invitation_pk_id BIGINT NOT NULL,  -- FK 없이 ID만 저장
                                    target_user_id BIGINT,             -- User 테이블과 연관 (FK 있음)

                                    CONSTRAINT fk_invitation_history_user FOREIGN KEY (target_user_id)
                                        REFERENCES user(user_pk_id)
                                        ON DELETE SET NULL
);


ALTER TABLE invitation_code
    ADD CONSTRAINT uq_invitation_code UNIQUE (code);
