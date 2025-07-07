ALTER TABLE todo
    ADD COLUMN council_id BIGINT;

ALTER TABLE todo
    ADD CONSTRAINT fk_todo_council
        FOREIGN KEY (council_id)
            REFERENCES council(council_id);
