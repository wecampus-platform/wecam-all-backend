-- V31__remove_unique_constraints.sql

-- UNIQUE 제약조건들을 제거하여 Soft Delete 패턴과 이력 보존이 가능하도록 함
-- 제약조건이 존재하지 않아도 에러 없이 진행

-- 1. category_assignment: 같은 카테고리를 같은 엔티티에 여러 번 할당 가능 (이력 보존)
-- 제약조건이 존재하지 않아도 에러 없이 진행
ALTER TABLE category_assignment DROP CONSTRAINT uk_category_assignment;

-- 2. meeting_attendee: 같은 회의에 같은 멤버가 여러 번 참석 가능 (이력 보존)
-- 제약조건이 존재하지 않아도 에러 없이 진행
ALTER TABLE meeting_attendee DROP CONSTRAINT UK_meeting_attendee;