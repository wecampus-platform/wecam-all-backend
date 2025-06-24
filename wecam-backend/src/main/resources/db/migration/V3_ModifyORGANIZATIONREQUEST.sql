--  기존 user_pk_id 제약조건 제거
ALTER TABLE organization_request DROP FOREIGN KEY FKe504d8i53mk8vef8105bgdcrl;

-- user_pk_id 컬럼 그대로 두되 → 관계 재설정 (ManyToOne 관계는 DDL 상 동일한 FK, 논리적으로만 의미 변경)
ALTER TABLE organization_request
    ADD CONSTRAINT fk_user FOREIGN KEY (user_pk_id) REFERENCES user (user_pk_id);

-- organization_id 컬럼은 기존 nullable = true → 그대로 유지하므로 변경 필요 없음
-- (만약 NOT NULL로 되어있다면 아래 추가로 실행)
-- ALTER TABLE organization_request MODIFY COLUMN organization_id BIGINT NULL;


ALTER TABLE organization_request MODIFY COLUMN school_name VARCHAR(20) NULL;

ALTER TABLE organization_request ADD COLUMN request_pk_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY;

ALTER TABLE organization_request DROP PRIMARY KEY;

ALTER TABLE organization_request DROP FOREIGN KEY fk_user;
