ALTER TABLE affiliation_certification DROP COLUMN sel_organization_name;
ALTER TABLE affiliation_certification DROP COLUMN sel_school_name;
ALTER TABLE affiliation_certification DROP COLUMN sel_enroll_year;
ALTER TABLE affiliation_certification DROP COLUMN issuance_date;


DROP DATABASE wecam_local;
CREATE DATABASE wecam_local CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
