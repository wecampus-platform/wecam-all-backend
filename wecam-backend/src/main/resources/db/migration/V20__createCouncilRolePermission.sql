DROP TABLE IF EXISTS council_member_permission;


CREATE TABLE council_role_permission (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         department_role_id BIGINT NOT NULL,
                                         permission VARCHAR(50) NOT NULL,
                                         CONSTRAINT fk_role_permission_department_role
                                             FOREIGN KEY (department_role_id)
                                                 REFERENCES council_department_role(id)
                                                 ON DELETE CASCADE
);
