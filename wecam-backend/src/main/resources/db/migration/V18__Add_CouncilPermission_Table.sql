ALTER TABLE council_permission_policy DROP COLUMN permission;

CREATE TABLE council_permission (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    permission VARCHAR(100) NOT NULL,
                                    policy_id BIGINT,
                                    CONSTRAINT fk_permission_policy
                                        FOREIGN KEY (policy_id)
                                            REFERENCES council_permission_policy(id)
                                            ON DELETE CASCADE
);

