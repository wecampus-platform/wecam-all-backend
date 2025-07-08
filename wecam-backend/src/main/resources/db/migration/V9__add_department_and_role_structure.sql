-- 1. CouncilDepartment 테이블 생성
CREATE TABLE council_department (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    council_id BIGINT NOT NULL,
                                    name VARCHAR(255) NOT NULL,
    `order` INT,
                                    parent_id BIGINT,
                                    is_active BOOLEAN DEFAULT TRUE,

                                    CONSTRAINT fk_department_council FOREIGN KEY (council_id) REFERENCES council(council_id)
);

-- 2. CouncilDepartmentRole 테이블 생성
CREATE TABLE council_department_role (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         department_id BIGINT NOT NULL,
                                         name VARCHAR(255) NOT NULL,
                                         level INT,

                                         CONSTRAINT fk_role_department FOREIGN KEY (department_id) REFERENCES council_department(id)
);

-- 3. CouncilPermissionPolicy 테이블 생성
CREATE TABLE council_permission_policy (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           council_id BIGINT NOT NULL,
                                           department_role_id BIGINT NOT NULL,
                                           permission VARCHAR(50) NOT NULL,

                                           CONSTRAINT fk_policy_council FOREIGN KEY (council_id) REFERENCES council(council_id),
                                           CONSTRAINT fk_policy_role FOREIGN KEY (department_role_id) REFERENCES council_department_role(id)
);

-- 4. CouncilMember 테이블에 외래키 필드 추가
ALTER TABLE council_member
    ADD COLUMN department_id BIGINT,
    ADD COLUMN department_role_id BIGINT,
    ADD CONSTRAINT fk_member_department FOREIGN KEY (department_id) REFERENCES council_department(id),
    ADD CONSTRAINT fk_member_role FOREIGN KEY (department_role_id) REFERENCES council_department_role(id);

-- 5. CouncilMember 기존 필드 제거 (더 이상 사용하지 않음)
ALTER TABLE council_member
    DROP COLUMN member_type,
    DROP COLUMN member_level,
    DROP COLUMN member_parent_id;
