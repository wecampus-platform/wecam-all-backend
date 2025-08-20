-- CATEGORY: unique (council_id, name) + index(council_id)
ALTER TABLE category
    ADD CONSTRAINT uk_category_council_name
        UNIQUE (council_id, name);

CREATE INDEX idx_category_council
    ON category (council_id);

-- CATEGORY_ASSIGNMENT: unique (category_id, entity_type, entity_id)
-- + indexes(category_id), (entity_type, entity_id)
ALTER TABLE category_assignment
    ADD CONSTRAINT uk_assignment_unique
        UNIQUE (category_id, entity_type, entity_id);

CREATE INDEX idx_assignment_category
    ON category_assignment (category_id);

CREATE INDEX idx_assignment_entity
    ON category_assignment (entity_type, entity_id);
