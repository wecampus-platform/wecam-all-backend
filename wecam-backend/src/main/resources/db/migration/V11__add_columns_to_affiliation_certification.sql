-- V<버전>__add_columns_to_affiliation_certification.sql

ALTER TABLE affiliation_certification
    ADD COLUMN sel_organization_name VARCHAR(20),
    ADD COLUMN sel_school_name VARCHAR(20),
    ADD COLUMN sel_enroll_year VARCHAR(20),
    ADD COLUMN issuance_date DATETIME(6);
