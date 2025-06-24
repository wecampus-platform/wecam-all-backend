ALTER TABLE affiliation_certification ADD COLUMN user_name VARCHAR(255);
ALTER TABLE user MODIFY expires_at DATETIME NULL;

ALTER TABLE user ADD COLUMN enroll_year VARCHAR(4);

ALTER TABLE affiliation_certification ADD COLUMN ocr_school_grade INT;
ALTER TABLE user_information MODIFY COLUMN student_grade INT;
