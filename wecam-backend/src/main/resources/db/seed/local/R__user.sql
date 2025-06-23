#학생회장 한명 만들어둠.
INSERT INTO user (
    is_authentication, is_email_verified, is_superuser,
    created_at, expires_at, organization_id, updated_at,
    email, role,organization,school_id,enroll_year
) VALUES (
             1, 1, 0,
             NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), 303, NOW(),
             'president@example.com', 'COUNCIL',303,2,'2021'
         );

INSERT INTO user_private (
    user_pk_id, password, phone_number, password_update_at
) VALUES (2, '$2a$10$D7ekb1rNGPRcdgwzgeiRIOrIBYi1636Ndl1fuy3d.3N5tupyJ8usa', 'yqjOjarKLwjBNw5gU2b2htrwGE1RLPHe', NOW());

INSERT INTO user_information (
    user_pk_id,
    name,
    nickname,
    school_email,
    student_id,
    student_grade,
    academic_status,
    school_id,
    is_authentication,
    is_council_fee,
    created_at,
    updated_at
) VALUES (
             2,
             '홍길동',
             '학생회장',
             NULL,
             '202312345',
             3,
             'ENROLLED',
             2,
             1,
             1,
             NOW(),
             NOW()
         );

INSERT INTO council (
    creator_user_id,
    organization_id,
    council_name,
    start_date,
    end_date,
    is_active
) VALUES (
             2,             -- 학생회장 user_pk_id
             303,           -- 컴공
             '세론',
             NOW(),
             DATE_ADD(NOW(), INTERVAL 1 YEAR),
             1
         );

INSERT INTO council_member (
    user_pk_id,
    council_id,
    member_level,
    member_role,
    member_type,
    is_active
) VALUES (
             2,
             1,
             1,                 -- 최고 등급
             'PRESIDENT',
             '학생회장',            -- 자유 형식이면 이렇게
             1
         );
