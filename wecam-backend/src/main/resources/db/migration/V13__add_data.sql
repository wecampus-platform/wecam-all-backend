-- 정보의생명공학대학 (org_id: 108)
INSERT INTO organization (organization_id, school_id, organization_name, organization_type, level, parent_id, created_at, updated_at)
VALUES
    (322, 2, '정보의생명공학대학', 'COLLEGE', 1, 100, NOW(), NOW()),
    (323,2,'정보컴퓨터공학부','DEPARTMENT',2,322,NOW(), NOW()),
    (324, 2, '경제통상대학', 'COLLEGE', 1, 100, NOW(), NOW()),
    (325,2,'경제학부','DEPARTMENT',2,324,NOW(), NOW());

