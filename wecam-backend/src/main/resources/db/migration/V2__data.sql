INSERT INTO university (school_id, school_name, school_address, created_at, updated_at)
VALUES
    (1, '서울대학교', '서울 관악구 관악로 1', NOW(), NOW()),
    (2, '부산대학교', '부산 금정구 부산대학로 63번길 2', NOW(), NOW());

-- 여러 단과대학 (school_id = 1, 서울대 기준)
INSERT INTO organization (organization_id, school_id, organization_name, organization_type, level, parent_id, created_at, updated_at)
VALUES
    (1,1,'서울대학교','UNIVERSITY',0,NULL, NOW(), NOW()),
    (2, 1, '공과대학', 'COLLEGE', 1, 1, NOW(), NOW()),
    (3, 1, '인문대학', 'COLLEGE', 1, 1, NOW(), NOW()),
    (4, 1, '자연과학대학', 'COLLEGE', 1, 1, NOW(), NOW()),
    (5, 1, '사회과학대학', 'COLLEGE', 1, 1, NOW(), NOW()),
    (6, 1, '경영대학', 'COLLEGE', 1, 1, NOW(), NOW()),
    (7, 1, '생활과학대학', 'COLLEGE', 1, 1, NOW(), NOW()),
    (8, 1, '미술대학', 'COLLEGE', 1, 1, NOW(), NOW()),
    (9, 1, '음악대학', 'COLLEGE', 1, 1, NOW(), NOW()),
    (10, 1, '사범대학', 'COLLEGE', 1, 1, NOW(), NOW());

-- 부산대학교 단과대학들 (COLLEGE, school_id = 2)
INSERT INTO organization (organization_id, school_id, organization_name, organization_type, level, parent_id, created_at, updated_at)
VALUES
    (100,2,'부산대학교','UNIVERSITY',0,NULL, NOW(), NOW()),
    (101, 2, '공과대학', 'COLLEGE', 1, 100, NOW(), NOW()),
    (102, 2, '인문대학', 'COLLEGE', 1, 100, NOW(), NOW()),
    (103, 2, '자연과학대학', 'COLLEGE', 1, 100, NOW(), NOW()),
    (104, 2, '사회과학대학', 'COLLEGE', 1, 100, NOW(), NOW()),
    (105, 2, '경영대학', 'COLLEGE', 1, 100, NOW(), NOW()),
    (106, 2, '사범대학', 'COLLEGE', 1, 100, NOW(), NOW()),
    (107, 2, '의과대학', 'COLLEGE', 1, 100, NOW(), NOW()),
    (108, 2, '약학대학', 'COLLEGE', 1, 100, NOW(), NOW()),
    (109, 2, '예술대학', 'COLLEGE', 1, 100, NOW(), NOW()),
    (110, 2, '국제학부', 'COLLEGE', 1, 100, NOW(), NOW());



-- 공과대학 (org_id: 1)
INSERT INTO organization (organization_id, school_id, organization_name, organization_type, level, parent_id, created_at, updated_at)
VALUES
    (201, 1, '컴퓨터공학부', 'DEPARTMENT', 2, 1, NOW(), NOW()),
    (202, 1, '기계공학부', 'DEPARTMENT', 2, 1, NOW(), NOW()),
    (203, 1, '전기정보공학부', 'DEPARTMENT', 2, 1, NOW(), NOW()),
    (204, 1, '건축학과', 'DEPARTMENT', 2, 1, NOW(), NOW()),
    (205, 1, '재료공학부', 'DEPARTMENT', 2, 1, NOW(), NOW());

-- 인문대학 (org_id: 2)
INSERT INTO organization (organization_id, school_id, organization_name, organization_type, level, parent_id, created_at, updated_at)
VALUES
    (211, 1, '국어국문학과', 'DEPARTMENT', 2, 2, NOW(), NOW()),
    (212, 1, '영어영문학과', 'DEPARTMENT', 2, 2, NOW(), NOW()),
    (213, 1, '불어불문학과', 'DEPARTMENT', 2, 2, NOW(), NOW()),
    (214, 1, '언어학과', 'DEPARTMENT', 2, 2, NOW(), NOW()),
    (215, 1, '철학과', 'DEPARTMENT', 2, 2, NOW(), NOW());

-- 자연과학대학 (org_id: 3)
INSERT INTO organization (organization_id, school_id, organization_name, organization_type, level, parent_id, created_at, updated_at)
VALUES
    (221, 1, '수학과', 'DEPARTMENT', 2, 3, NOW(), NOW()),
    (222, 1, '물리학과', 'DEPARTMENT', 2, 3, NOW(), NOW()),
    (223, 1, '화학과', 'DEPARTMENT', 2, 3, NOW(), NOW()),
    (224, 1, '지구과학과', 'DEPARTMENT', 2, 3, NOW(), NOW()),
    (225, 1, '통계학과', 'DEPARTMENT', 2, 3, NOW(), NOW());

-- 공과대학 (org_id: 101)
INSERT INTO organization (organization_id, school_id, organization_name, organization_type, level, parent_id, created_at, updated_at)
VALUES
    (301, 2, '전기공학과', 'DEPARTMENT', 2, 101, NOW(), NOW()),
    (302, 2, '전자공학과', 'DEPARTMENT', 2, 101, NOW(), NOW()),
    (303, 2, '컴퓨터공학과', 'DEPARTMENT', 2, 101, NOW(), NOW()),
    (304, 2, '신소재공학과', 'DEPARTMENT', 2, 101, NOW(), NOW()),
    (305, 2, '화공생명공학과', 'DEPARTMENT', 2, 101, NOW(), NOW());

-- 인문대학 (org_id: 102)
INSERT INTO organization (organization_id, school_id, organization_name, organization_type, level, parent_id, created_at, updated_at)
VALUES
    (311, 2, '국어국문학과', 'DEPARTMENT', 2, 102, NOW(), NOW()),
    (312, 2, '영어영문학과', 'DEPARTMENT', 2, 102, NOW(), NOW()),
    (313, 2, '철학과', 'DEPARTMENT', 2, 102, NOW(), NOW()),
    (314, 2, '사학과', 'DEPARTMENT', 2, 102, NOW(), NOW()),
    (315, 2, '일어일문학과', 'DEPARTMENT', 2, 102, NOW(), NOW());

-- 약학대학 (org_id: 108)
INSERT INTO organization (organization_id, school_id, organization_name, organization_type, level, parent_id, created_at, updated_at)
VALUES
    (321, 2, '약학과', 'DEPARTMENT', 2, 108, NOW(), NOW());
