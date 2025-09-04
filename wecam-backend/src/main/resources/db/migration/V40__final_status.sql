-- Todo / Meeting: 상태만
ALTER TABLE todo
    Drop COLUMN final_status;

ALTER TABLE meeting
    DROP COLUMN final_status;


-- Todo / Meeting: 상태만
ALTER TABLE todo_file
    ADD COLUMN final_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT';

ALTER TABLE meeting_file
    ADD COLUMN final_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT';
