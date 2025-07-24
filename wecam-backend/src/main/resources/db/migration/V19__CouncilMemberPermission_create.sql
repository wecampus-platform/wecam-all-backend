DROP TABLE IF EXISTS council_permission;
DROP TABLE IF EXISTS council_permission;

CREATE TABLE council_member_permission (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           member_id BIGINT NOT NULL,
                                           permission VARCHAR(50) NOT NULL,
                                           CONSTRAINT fk_cmp_member FOREIGN KEY (member_id) REFERENCES council_member(council_member_pk_id)
);


