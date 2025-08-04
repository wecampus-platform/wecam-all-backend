
-- V20__Add_BaseEntity_Fields.sql

-- -- 1. admin_user
-- ALTER TABLE admin_user
--     ADD COLUMN created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP NOT NULL,
--     ADD COLUMN updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
--                                                                                                                   ADD COLUMN status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE';

# -- 2. affiliation_certification
# ALTER TABLE affiliation_certification
#     CHANGE COLUMN status authentication_status VARCHAR(30) NOT NULL,
#     ADD COLUMN status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE';
#
