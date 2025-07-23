ALTER TABLE user_information
    DROP COLUMN name;

ALTER TABLE user
    ADD COLUMN name VARCHAR(255);

ALTER TABLE user
    ADD COLUMN user_tag VARCHAR(255);
