
DROP VIEW IF EXISTS v_file_library;

CREATE VIEW v_file_library (
                            source_type, source_id, source_title,
                            category_ids, category_names,
                            file_id, file_name, uploader_id, council_id,
                            uploaded_at, is_final
    ) AS

/* 1) 할일 첨부 */
SELECT
    'TODO'                                   AS source_type,
    t.todo_id                                  AS source_id,
    t.title                                  AS source_title,
    GROUP_CONCAT(DISTINCT ca.category_id ORDER BY ca.category_id)                AS category_ids,
    GROUP_CONCAT(DISTINCT c.name        ORDER BY c.name        SEPARATOR ',')    AS category_names,
    fa.file_id                                AS file_id,
    fa.original_file_name                     AS file_name,
    fa.user_pk_id                             AS uploader_id,
    fa.council_id                             AS council_id,
    tf.created_at                             AS uploaded_at,
    /* is_final 컬럼이 있다면 COALESCE(tf.is_final,0) 로 바꿔도 됨 */
    0                                         AS is_final
FROM todo_file tf
         JOIN file_asset fa ON fa.file_id = tf.todo_file_id
         JOIN todo       t  ON t.todo_id     = tf.todo_id
         LEFT JOIN category_assignment ca
                   ON ca.entity_type='TODO' AND ca.entity_id=t.todo_id AND ca.status='ACTIVE'
         LEFT JOIN category c ON c.id = ca.category_id
GROUP BY
    t.todo_id, t.title, fa.file_id, fa.original_file_name, fa.user_pk_id, fa.council_id, tf.created_at

UNION ALL

/* 2) 회의록 첨부 */
SELECT
    'MEETING',
    m.meeting_id,
    m.title,
    GROUP_CONCAT(DISTINCT ca.category_id ORDER BY ca.category_id),
    GROUP_CONCAT(DISTINCT c.name        ORDER BY c.name        SEPARATOR ','),
    fa.file_id,
    fa.original_file_name,
    fa.user_pk_id,
    fa.council_id,
    mf.created_at,
    0
FROM meeting_file mf
         JOIN file_asset fa ON fa.file_id = mf.file_id
         JOIN meeting     m ON m.meeting_id      = mf.meeting_id
         LEFT JOIN category_assignment ca
                   ON ca.entity_type='MEETING' AND ca.entity_id=m.meeting_id AND ca.status='ACTIVE'
         LEFT JOIN category c ON c.id = ca.category_id
GROUP BY
    m.meeting_id, m.title, fa.file_id, fa.original_file_name, fa.user_pk_id, fa.council_id, mf.created_at

UNION ALL

/* 3) 단독 업로드 */
SELECT
    'STANDALONE',
    CAST(NULL AS SIGNED)            AS source_id,
    CAST(NULL AS CHAR(255))      AS source_title,
    GROUP_CONCAT(DISTINCT ca.category_id ORDER BY ca.category_id),
    GROUP_CONCAT(DISTINCT c.name        ORDER BY c.name        SEPARATOR ','),
    fa.file_id,
    fa.original_file_name,
    fa.user_pk_id,
    fa.council_id,
    fa.created_at,
    0
FROM file_asset fa
         LEFT JOIN category_assignment ca
                   ON ca.entity_type='FILE_ASSET' AND ca.entity_id=fa.file_id AND ca.status='ACTIVE'
         LEFT JOIN category c ON c.id = ca.category_id
WHERE NOT EXISTS (SELECT 1 FROM todo_file    tf WHERE tf.todo_file_id = fa.file_id)
  AND NOT EXISTS (SELECT 1 FROM meeting_file mf WHERE mf.file_id = fa.file_id)
GROUP BY
    fa.file_id, fa.original_file_name, fa.user_pk_id, fa.council_id, fa.created_at;
