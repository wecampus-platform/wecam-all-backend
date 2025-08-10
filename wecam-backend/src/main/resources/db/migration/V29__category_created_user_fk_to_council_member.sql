-- V29__category_created_user_fk_to_council_member.sql
-- 목적: category.created_user_id 컬럼명을 created_member_id로 변경하고,
--       FK 대상을 user(user_pk_id) → council_member(council_member_pk_id)로 변경

-- 1) 기존 FK 제거 (V25에서 생성된 이름 기준)
ALTER TABLE category
  DROP FOREIGN KEY fk_category_user;

-- 2) 컬럼명 변경: created_user_id -> created_member_id
ALTER TABLE category
  CHANGE COLUMN created_user_id created_member_id BIGINT NOT NULL;

-- 3) 새 FK 추가: council_member를 참조하도록 변경
ALTER TABLE category
  ADD CONSTRAINT fk_category_created_member
  FOREIGN KEY (created_member_id)
  REFERENCES council_member(council_member_pk_id)
  ON DELETE RESTRICT
  ON UPDATE CASCADE;
