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
    CAST('FILE_ASSET' AS CHAR(16) CHARACTER SET utf8mb4) COLLATE utf8mb4_0900_ai_ci,
    CAST('FILE_ASSET'       AS CHAR(16) CHARACTER SET utf8mb4) COLLATE utf8mb4_0900_ai_ci,
    fa.file_id,
    CAST(fa.file_id AS CHAR(36) CHARACTER SET utf8mb4) COLLATE utf8mb4_0900_ai_ci,
    CAST(NULL       AS CHAR(255) CHARACTER SET utf8mb4) COLLATE utf8mb4_0900_ai_ci,
    CAST(fa.original_file_name AS CHAR(255) CHARACTER SET utf8mb4) COLLATE utf8mb4_0900_ai_ci,
    fa.council_id,
    fa.user_pk_id,
    fa.created_at,
    fa.is_final
FROM file_asset fa;
