--
-- -- 3. affiliation_file
-- ALTER TABLE affiliation_file
--     ADD COLUMN updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),    ADD COLUMN status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE';

-- 4. council
-- ALTER TABLE council
--     DROP COLUMN is_active,
--     ADD COLUMN created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
--     ADD COLUMN updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),                                                                                     ADD COLUMN status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE';

-- 5. council_department
-- ALTER TABLE council_department
--     DROP COLUMN is_active;
-- -- 6. council_department_role
--
-- -- 7. council_member
-- ALTER TABLE council_member
--     DROP COLUMN is_active;
-- 8. council_role_permission
-- ALTER TABLE council_role_permission
--     ADD COLUMN created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
--     ADD COLUMN updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),                                                                                                                  ADD COLUMN status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE';

-- 9. invitation_code
-- ALTER TABLE invitation_code
--     ADD COLUMN status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE';
--
-- -- 10. organization_request
-- ALTER TABLE organization_request
--     CHANGE COLUMN status request_status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') NOT NULL,
--     ADD COLUMN status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE';

-- 11. invitation_history
ALTER TABLE invitation_history
    ADD COLUMN created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    ADD COLUMN updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    ADD COLUMN status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE';

-- 12. organization
ALTER TABLE organization
    ADD COLUMN status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE';

-- 13. organization_request_file
ALTER TABLE organization_request_file
    ADD COLUMN status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE';

-- 14. todo
ALTER TABLE todo
    ADD COLUMN status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE';

-- 15. todo_file
ALTER TABLE todo_file
    ADD COLUMN status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE';

-- 16. todo_manager
ALTER TABLE todo_manager
    ADD COLUMN status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE';

-- 17. university
ALTER TABLE university
    ADD COLUMN status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE';

-- 18. user
ALTER TABLE user
    CHANGE COLUMN status user_status ENUM('ACTIVE', 'SUSPENDED', 'WITHDRAWN', 'BANNED') NOT NULL,
    ADD COLUMN status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE';

-- 19. user_information
ALTER TABLE user_information
    ADD COLUMN status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE';

-- 20. user_position_history
ALTER TABLE user_position_history
    ADD COLUMN created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    ADD COLUMN updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),                                                                                                                  ADD COLUMN status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE';

-- 21. user_private
ALTER TABLE user_private
    DROP COLUMN password_update_at,
    ADD COLUMN created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    ADD COLUMN updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),                                                                                     ADD COLUMN status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE';

-- 22. user_signup_information
ALTER TABLE user_signup_information
    ADD COLUMN status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE';
