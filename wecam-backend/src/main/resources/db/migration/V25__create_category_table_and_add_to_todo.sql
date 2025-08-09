-- V25__create_category_table_and_add_to_todo.sql

-- 1. 카테고리 테이블 생성
CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '카테고리 고유 번호',
    council_id BIGINT NOT NULL COMMENT '소속 학생회 ID',
    name VARCHAR(50) NOT NULL COMMENT '카테고리명',
    created_user_id BIGINT NOT NULL COMMENT '생성자 ID',
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) NOT NULL COMMENT '생성일',
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) NOT NULL ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일',
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '상태',
    
    CONSTRAINT fk_category_council FOREIGN KEY (council_id) REFERENCES council(council_id),
    CONSTRAINT fk_category_user FOREIGN KEY (created_user_id) REFERENCES user(user_pk_id),
    CONSTRAINT uk_category_council_name UNIQUE (council_id, name) -- 학생회 내부에서 카테고리 중복 생성 불가
);

-- 2. 범용 카테고리 할당 테이블 생성 (Generic Category System)
CREATE TABLE category_assignment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '카테고리 할당 고유 번호',
    category_id BIGINT NOT NULL COMMENT '카테고리 ID',
    entity_type VARCHAR(50) NOT NULL COMMENT '엔티티 타입 (TODO, MEETING, SCHEDULE)',
    entity_id BIGINT NOT NULL COMMENT '엔티티 ID',
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) NOT NULL COMMENT '생성일',
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) NOT NULL ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일',
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '상태',
    
    CONSTRAINT fk_category_assignment_category FOREIGN KEY (category_id) REFERENCES category(id),
    CONSTRAINT uk_category_assignment UNIQUE (category_id, entity_type, entity_id) -- 같은 카테고리를 같은 엔티티에 중복으로 할당 불가
);

-- 3. 기본 인덱스 생성 (성능 최적화)
-- 학생회별 카테고리 조회용 (가장 빈번한 쿼리)
CREATE INDEX idx_category_council_status ON category(council_id, status);

-- 카테고리 할당 조회용 (엔티티별 카테고리 조회)
CREATE INDEX idx_category_assignment_entity ON category_assignment(entity_type, entity_id, status);

-- 카테고리별 할당 조회용 (카테고리별 엔티티 조회)
CREATE INDEX idx_category_assignment_category ON category_assignment(category_id, status);
