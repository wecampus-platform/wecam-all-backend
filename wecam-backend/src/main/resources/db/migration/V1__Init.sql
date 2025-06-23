-- auto-generated definition
create table affiliation_certification
(
    ocr_enroll_year       varchar(4)                                                       null,
    organization_pk_id    bigint                                                           null,
    pk_reviewer_userid    bigint                                                           null,
    pk_upload_userid      bigint                                                           not null,
    requested_at          datetime(6)                                                      null,
    reviewed_at           datetime(6)                                                      null,
    school_pk_id          bigint                                                           null,
    ocr_organization_name varchar(20)                                                      null,
    ocr_school_name       varchar(20)                                                      null,
    ocr_user_name         varchar(20)                                                      null,
    reason                text                                                             null,
    authentication_type   enum ('NEW_STUDENT', 'CURRENT_STUDENT')                          not null,
    ocr_result            enum ('FAILURE', 'SUCCESS', 'UNCLEAR')                           not null,
    status                enum ('APPROVED', 'CANCELLED', 'EXPIRED', 'PENDING', 'REJECTED') not null,
    primary key (pk_upload_userid, authentication_type),
    constraint FK234xlvuts8rd2dj3inma243pb
        foreign key (pk_upload_userid) references user (user_pk_id),
    constraint FK4o80gbmc8km2e9eog5x1tqtul
        foreign key (organization_pk_id) references organization (organization_id),
    constraint FKb7u93vabgpr11tmjhvbc2c9yj
        foreign key (pk_reviewer_userid) references user (user_pk_id),
    constraint FKjlk72m2i57aoaqt3e6fw9e7od
        foreign key (school_pk_id) references university (school_id)
);


-- auto-generated definition
create table affiliation_file
(
    authentication_type tinyint                               not null,
    created_at          datetime(6)                           not null,
    expires_at          datetime(6)                           null,
    pk_upload_userid    bigint                                not null,
    uuid                binary(16)                            not null,
    file_name           varchar(255)                          not null,
    file_path           text                                  not null,
    file_type           enum ('DOC', 'IMAGE', 'OTHER', 'PDF') not null,
    primary key (authentication_type, pk_upload_userid)
);

-- auto-generated definition
create table council
(
    is_active       bit         not null,
    council_id      bigint auto_increment
        primary key,
    creator_user_id bigint      not null,
    end_date        datetime(6) null,
    organization_id bigint      not null,
    start_date      datetime(6) null,
    council_name    varchar(50) not null,
    constraint FKemp2vp2fl4nmb7hjpw5eeibhg
        foreign key (creator_user_id) references user (user_pk_id),
    constraint FKrmh4rkcw0ig0n20x7kefevgfb
        foreign key (organization_id) references organization (organization_id)
);

-- auto-generated definition
create table council_member
(
    is_active            bit                                                                 not null,
    member_level         int                                                                 null,
    council_id           bigint                                                              not null,
    council_member_pk_id bigint auto_increment
        primary key,
    member_parent_id     bigint                                                              null,
    user_pk_id           bigint                                                              null,
    member_type          varchar(20)                                                         null,
    member_role          enum ('DEPUTY', 'DIRECTOR', 'PRESIDENT', 'STAFF', 'VICE_PRESIDENT') not null,
    constraint FKd1o0i7h413db3c7jp8v2iwfkw
        foreign key (user_pk_id) references user (user_pk_id),
    constraint FKlvoe9jlsms0xn5cdght0anrqq
        foreign key (council_id) references council (council_id)
);

-- auto-generated definition
create table organization
(
    organization_id   bigint auto_increment
        primary key,
    created_at        datetime(6)                                           null,
    updated_at        datetime(6)                                           null,
    level             int                                                   null,
    organization_name varchar(50)                                           not null,
    organization_type enum ('COLLEGE', 'DEPARTMENT', 'MAJOR', 'UNIVERSITY') null,
    parent_id         bigint                                                null,
    school_id         bigint                                                not null,
    constraint FKc30yedjwp9qw1f3nn2ytda7tj
        foreign key (parent_id) references organization (organization_id)
);

create index FKmfmy536ffhgfnjv9elwb5vp7h
    on organization (school_id);

-- auto-generated definition
create table organization_request
(
    created_at        datetime(6)                                           null,
    organization_id   bigint                                                null,
    updated_at        datetime(6)                                           null,
    user_pk_id        bigint                                                not null
        primary key,
    college_name      varchar(20)                                           null,
    council_name      varchar(20)                                           not null,
    department_name   varchar(20)                                           null,
    school_name       varchar(20)                                           not null,
    organization_type enum ('COLLEGE', 'DEPARTMENT', 'MAJOR', 'UNIVERSITY') not null,
    status            enum ('APPROVED', 'PENDING', 'REJECTED')              not null,
    constraint FK7fd6i5o92m9vv89gkyuogrqe
        foreign key (organization_id) references organization (organization_id),
    constraint FKe504d8i53mk8vef8105bgdcrl
        foreign key (user_pk_id) references user (user_pk_id)
);

-- auto-generated definition
create table university
(
    created_at     datetime(6)  null,
    school_id      bigint auto_increment
        primary key,
    updated_at     datetime(6)  null,
    school_name    varchar(20)  not null,
    school_address varchar(100) null
);

-- auto-generated definition
create table user
(
    user_pk_id        bigint auto_increment
        primary key,
    created_at        datetime(6)  not null,
    email             varchar(255) not null,
    expires_at        datetime(6)  not null,
    is_authentication bit          not null,
    is_email_verified bit          not null,
    updated_at        datetime(6)  null,
    constraint UKob8kqyqqgmefl0aco34akdtpe
        unique (email)
);

-- auto-generated definition
create table user_information
(
    is_authentication bit                           not null,
    is_council_fee    bit                           not null,
    student_grade     tinyint                       null,
    created_at        datetime(6)                   null,
    information_pk_id bigint auto_increment
        primary key,
    school_id         bigint                        null,
    updated_at        datetime(6)                   null,
    user_pk_id        bigint                        not null,
    name              varchar(20)                   not null,
    nickname          varchar(20)                   null,
    school_email      varchar(255)                  null,
    student_id        varchar(255)                  null,
    academic_status   enum ('ENROLLED', 'ON_LEAVE') null,
    constraint UKejka2bos520sht8v139lpx0xm
        unique (school_email),
    constraint UKl6qb45ht6c79bi0pkk51wt82q
        unique (nickname),
    constraint UKp9apkv6rlhufs8mdjobtw5eym
        unique (user_pk_id),
    constraint FK4r3it3mkxnn3ddob5n1vw2ejl
        foreign key (school_id) references university (school_id),
    constraint FKj1xcfxued5hwto4vooimhfl4h
        foreign key (user_pk_id) references user (user_pk_id)
);

-- auto-generated definition
create table user_position_history
(
    council_id      bigint                                                              not null,
    end_date        datetime(6)                                                         null,
    history_pk_id   bigint auto_increment
        primary key,
    organization_id bigint                                                              not null,
    start_date      datetime(6)                                                         null,
    user_pk_id      bigint                                                              not null,
    member_type     varchar(20)                                                         null,
    member_role     enum ('DEPUTY', 'DIRECTOR', 'PRESIDENT', 'STAFF', 'VICE_PRESIDENT') not null,
    constraint FK67st029d3fyuk6ed19jxqjr8q
        foreign key (council_id) references council (council_id),
    constraint FKcrwmc88gxj7bwkrlg8gpi68qj
        foreign key (user_pk_id) references user (user_pk_id),
    constraint FKshat6rtlwsienhx1xuullcq01
        foreign key (organization_id) references organization (organization_id)
);

-- auto-generated definition
create table user_private
(
    password_update_at datetime(6)  null,
    user_pk_id         bigint       not null
        primary key,
    password           varchar(100) not null,
    phone_number       varchar(150) not null,
    constraint UK67snnf8c36tnf6qnvaoip02lm
        unique (phone_number),
    constraint FKmrc2d7hv33tq2xowdd263knlx
        foreign key (user_pk_id) references user (user_pk_id)
);

-- auto-generated definition
create table user_signup_information
(
    enroll_year            varchar(4)  null,
    is_make_workspace      bit         null,
    created_at             datetime(6) null,
    select_organization_id bigint      null,
    select_school_id       bigint      null,
    updated_at             datetime(6) null,
    user_pk_id             bigint      not null
        primary key,
    input_college_name     varchar(20) null,
    input_department_name  varchar(20) null,
    input_school_name      varchar(20) null,
    name                   varchar(20) not null,
    constraint FKjitkfbpet5tu7xlchabgplxvp
        foreign key (user_pk_id) references user (user_pk_id)
);