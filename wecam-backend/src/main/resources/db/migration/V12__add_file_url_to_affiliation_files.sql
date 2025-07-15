-- 소속 인증 파일 테이블에 file_url 컬럼 추가
ALTER TABLE affiliation_file
ADD COLUMN file_url VARCHAR(512);

-- 기존 foreign key가 없었다면 아래 추가
-- affiliation_certification 삭제 시 → 자동으로 해당 인증의 affiliation_file들도 DB에서 삭제
ALTER TABLE affiliation_file
    ADD CONSTRAINT fk_affiliation_file_to_certification
        FOREIGN KEY (pk_upload_userid, authentication_type)
            REFERENCES affiliation_certification (pk_upload_userid, authentication_type)
            ON DELETE CASCADE;
