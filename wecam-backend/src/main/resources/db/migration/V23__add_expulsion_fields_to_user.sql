-- User 테이블에 제명 관련 필드 추가
ALTER TABLE `user` 
ADD COLUMN expulsion_reason VARCHAR(50) NULL,
ADD COLUMN expulsion_date DATETIME NULL; 