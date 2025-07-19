-- 초대코드 테이블 수정: 만료일 컬럼 추가 및 사용 제한 컬럼 제거

ALTER TABLE invitation_code
ADD COLUMN expiration_date DATETIME;

ALTER TABLE invitation_code
DROP COLUMN is_usage_limit;

ALTER TABLE invitation_code
DROP COLUMN usage_limit;
