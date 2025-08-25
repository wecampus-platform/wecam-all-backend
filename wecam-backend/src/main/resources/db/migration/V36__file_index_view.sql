-- 카테고리 집계 서브뷰
CREATE OR REPLACE VIEW v_category_agg AS
SELECT
    ca.entity_type,
    ca.entity_id,
    GROUP_CONCAT(c.name ORDER BY c.name SEPARATOR ',') AS category_names
FROM category_assignment ca
         JOIN category c ON c.id = ca.category_id
WHERE ca.status = 'ACTIVE'
GROUP BY ca.entity_type, ca.entity_id;

-- 통합 파일 뷰
DROP VIEW IF EXISTS v_file_library;
CREATE VIEW v_file_library AS
SELECT
    CAST('TODO'    AS CHAR(16) CHARACTER SET utf8mb4) COLLATE utf8mb4_0900_ai_ci AS source_type,
    CAST('TODO'    AS CHAR(16) CHARACTER SET utf8mb4) COLLATE utf8mb4_0900_ai_ci AS entity_type,
    t.todo_id                                                            AS entity_id,
    CAST(BIN_TO_UUID(tf.todo_file_id, 1) AS CHAR(36) CHARACTER SET utf8mb4) COLLATE utf8mb4_0900_ai_ci AS file_id,
    CAST(t.title               AS CHAR(255) CHARACTER SET utf8mb4) COLLATE utf8mb4_0900_ai_ci AS source_title,
    CAST(tf.original_file_name AS CHAR(255) CHARACTER SET utf8mb4) COLLATE utf8mb4_0900_ai_ci AS file_name,
    t.council_id                                                         AS council_id,
    t.create_user_id                                                        AS uploader_id,
    tf.created_at                                                        AS uploaded_at,
    tf.is_final                                                          AS is_final
FROM todo_file tf
         JOIN todo t ON t.todo_id = tf.todo_id

UNION ALL
SELECT
    CAST('MEETING' AS CHAR(16) CHARACTER SET utf8mb4) COLLATE utf8mb4_0900_ai_ci,
    CAST('MEETING' AS CHAR(16) CHARACTER SET utf8mb4) COLLATE utf8mb4_0900_ai_ci,
    m.meeting_id,
    CAST(mf.file_id AS CHAR(36) CHARACTER SET utf8mb4) COLLATE utf8mb4_0900_ai_ci,
    CAST(m.title               AS CHAR(255) CHARACTER SET utf8mb4) COLLATE utf8mb4_0900_ai_ci,
    CAST(mf.original_file_name AS CHAR(255) CHARACTER SET utf8mb4) COLLATE utf8mb4_0900_ai_ci,
    m.council_id,
    m.created_by,
    mf.created_at,
    mf.is_final
FROM meeting_file mf
         JOIN meeting m ON m.meeting_id = mf.meeting_id

UNION ALL
SELECT
    CAST('STANDALONE' AS CHAR(16) CHARACTER SET utf8mb4) COLLATE utf8mb4_0900_ai_ci,
    CAST('FILE'       AS CHAR(16) CHARACTER SET utf8mb4) COLLATE utf8mb4_0900_ai_ci,
    fa.file_id,
    CAST(fa.file_id AS CHAR(36) CHARACTER SET utf8mb4) COLLATE utf8mb4_0900_ai_ci,
    CAST(NULL       AS CHAR(255) CHARACTER SET utf8mb4) COLLATE utf8mb4_0900_ai_ci,
    CAST(fa.original_file_name AS CHAR(255) CHARACTER SET utf8mb4) COLLATE utf8mb4_0900_ai_ci,
    fa.council_id,
    fa.user_pk_id,
    fa.created_at,
    fa.is_final
FROM file_asset fa;

-- 인덱스(변경 없음)
CREATE INDEX idx_todo_file_final_created     ON todo_file(is_final, created_at);
CREATE INDEX idx_meeting_file_final_created  ON meeting_file(is_final, created_at);
CREATE INDEX idx_file_asset_final_created    ON file_asset(is_final, created_at);
CREATE INDEX idx_todo_council                ON todo(council_id);
CREATE INDEX idx_meeting_council             ON meeting(council_id);
CREATE INDEX idx_todo_file_todo_id           ON todo_file(todo_id);
CREATE INDEX idx_meeting_file_meeting        ON meeting_file(meeting_id);
CREATE INDEX idx_category_assignment_optimized
    ON category_assignment(entity_type, status, entity_id, category_id);
