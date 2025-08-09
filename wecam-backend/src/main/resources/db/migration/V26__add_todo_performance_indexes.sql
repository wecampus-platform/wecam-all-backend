-- V26__add_todo_performance_indexes.sql

-- 할 일 페이지 조회 성능 최적화를 위한 인덱스 추가

-- 1. 학생회별 할 일 조회 (가장 빈번한 쿼리)
-- 용도: 할 일 목록 페이지에서 학생회별 할 일 조회
-- 쿼리: SELECT * FROM todo WHERE council_id = ? AND status = 'ACTIVE'
CREATE INDEX idx_todo_council_status ON todo(council_id, status);

-- 2. 할 일 마감일 기준 정렬 조회용
-- 용도: 할 일을 마감일 순으로 정렬하여 조회
-- 쿼리: SELECT * FROM todo WHERE council_id = ? AND status = 'ACTIVE' ORDER BY due_at ASC
CREATE INDEX idx_todo_council_due_at ON todo(council_id, status, due_at);

-- 3. 할 일 생성자 기준 조회용
-- 용도: 특정 사용자가 생성한 할 일 조회
-- 쿼리: SELECT * FROM todo WHERE create_user_id = ? AND status = 'ACTIVE'
CREATE INDEX idx_todo_create_user ON todo(create_user_id, status);

-- 4. 할 일 진행 상태별 조회용
-- 용도: 진행 상태별 필터링 (진행 중, 완료 등)
-- 쿼리: SELECT * FROM todo WHERE council_id = ? AND progress_status = ? AND status = 'ACTIVE'
CREATE INDEX idx_todo_progress_status ON todo(council_id, progress_status, status);

-- 5. 카테고리 할당 복합 조회용 (할 일 + 카테고리 조인 최적화)
-- 용도: 할 일과 카테고리를 함께 조회할 때 성능 최적화
-- 쿼리: SELECT t.*, c.name FROM todo t LEFT JOIN category_assignment ca ON ... LEFT JOIN category c ON ...
CREATE INDEX idx_category_assignment_todo_optimized ON category_assignment(entity_type, status, entity_id, category_id);

-- 6. 할 일 생성일 기준 조회용 (선택사항)
-- 용도: 최근 생성된 할 일 조회
-- 쿼리: SELECT * FROM todo WHERE council_id = ? AND status = 'ACTIVE' ORDER BY created_at DESC
CREATE INDEX idx_todo_council_created_at ON todo(council_id, status, created_at);
