-- auto-generated definition
CREATE TABLE university (
                            school_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            school_name VARCHAR(20) NOT NULL,
                            school_address VARCHAR(100),
                            created_at DATETIME,
                            updated_at DATETIME
);
-- auto-generated definition
create table organization (
                              organization_id     bigint auto_increment primary key,
                              school_id           bigint       not null,
                              organization_name   varchar(50)  not null,
                              level               int,
                              organization_type   enum('UNIVERSITY', 'COLLEGE', 'DEPARTMENT'),
                              parent_id           bigint,

                              created_at          datetime(6),
                              updated_at          datetime(6),

                              constraint FK_organization_university
                                  foreign key (school_id) references university (school_id),

                              constraint FK_organization_parent
                                  foreign key (parent_id) references organization (organization_id)
);

-- auto-generated definition
create table user (
                      user_pk_id        bigint auto_increment primary key,
                      email             varchar(255) not null unique,
                      expires_at        datetime(6),
                      created_at        datetime(6) not null,
                      updated_at        datetime(6),
                      is_authentication bit not null,
                      is_email_verified bit not null,
                      role              enum ('UNAUTH', 'STUDENT','GUEST_STUDENT','COUNCIL', 'ADMIN') default 'UNAUTH',
                      status            enum ('ACTIVE', 'SUSPENDED', 'WITHDRAWN','BANNED') not null,
                      is_superuser      boolean not null default false,
                      enroll_year       varchar(4),
                      school_id         bigint,
                      organization_id   bigint,

                      constraint FK_user_school
                          foreign key (school_id) references university (school_id),

                      constraint FK_user_organization
                          foreign key (organization_id) references organization (organization_id)
);
CREATE TABLE affiliation_certification (
                                           pk_upload_userid BIGINT NOT NULL,
                                           authentication_type VARCHAR(30) NOT NULL,  -- ENUM → VARCHAR(30)
                                           ocr_school_grade INT NOT NULL,
                                           ocr_result VARCHAR(30) NOT NULL,           -- ENUM → VARCHAR(30)
                                           ocr_user_name VARCHAR(20),
                                           ocr_enroll_year VARCHAR(4),
                                           ocr_school_name VARCHAR(20),
                                           ocr_organization_name VARCHAR(20),
                                           status VARCHAR(30) NOT NULL,               -- ENUM → VARCHAR(30)
                                           reason TEXT,
                                           user_name VARCHAR(20),
                                           requested_at DATETIME,
                                           reviewed_at DATETIME,
                                           pk_reviewer_userid BIGINT,
                                           school_pk_id BIGINT,
                                           organization_pk_id BIGINT,
                                           PRIMARY KEY (pk_upload_userid, authentication_type),
                                           CONSTRAINT FK_cert_user FOREIGN KEY (pk_upload_userid)
                                               REFERENCES user(user_pk_id),
                                           CONSTRAINT FK_cert_reviewer FOREIGN KEY (pk_reviewer_userid)
                                               REFERENCES user(user_pk_id),
                                           CONSTRAINT FK_cert_school FOREIGN KEY (school_pk_id)
                                               REFERENCES university(school_id),
                                           CONSTRAINT FK_cert_org FOREIGN KEY (organization_pk_id)
                                               REFERENCES organization(organization_id)
);
CREATE TABLE affiliation_file (
                                   pk_upload_userid BIGINT NOT NULL,
                                   authentication_type VARCHAR(30) NOT NULL,  -- ENUM → VARCHAR(30)
                                   uuid BINARY(16) NOT NULL,
                                   file_path TEXT NOT NULL,
                                   file_name VARCHAR(255) NOT NULL,
                                   file_type VARCHAR(30) NOT NULL,            -- ENUM → VARCHAR(30)
                                   expires_at DATETIME,
                                   created_at DATETIME NOT NULL,
                                   PRIMARY KEY (pk_upload_userid, authentication_type),
                                   CONSTRAINT FK_file_cert FOREIGN KEY (pk_upload_userid, authentication_type)
                                       REFERENCES affiliation_certification(pk_upload_userid, authentication_type)
 );

-- auto-generated definition
create table council (
                         council_id      bigint auto_increment primary key,
                         organization_id bigint      not null,
                         council_name    varchar(50) not null,
                         start_date      datetime(6),
                         end_date        datetime(6),
                         is_active       boolean     not null,
                         creator_user_id bigint      not null,

                         constraint FK_council_organization
                             foreign key (organization_id) references organization (organization_id),

                         constraint FK_council_creator
                             foreign key (creator_user_id) references user (user_pk_id)
);

-- auto-generated definition
create table council_member (
                                council_member_pk_id bigint auto_increment primary key,
                                council_id           bigint       not null,
                                user_pk_id           bigint,
                                member_type          varchar(20),
                                member_level         int,
                                member_parent_id     bigint,
                                member_role          enum ('PRESIDENT', 'VICE_PRESIDENT', 'DEPUTY', 'DIRECTOR', 'STAFF') not null,
                                is_active            boolean      not null,

                                constraint FK_council_member_council
                                    foreign key (council_id) references council (council_id),

                                constraint FK_council_member_user
                                    foreign key (user_pk_id) references user (user_pk_id)
);


create index FKmfmy536ffhgfnjv9elwb5vp7h
    on organization (school_id);

-- auto-generated definition
CREATE TABLE organization_request (
                                      request_pk_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      organization_id BIGINT,
                                      school_name VARCHAR(20),
                                      college_name VARCHAR(20),
                                      department_name VARCHAR(20),
                                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      updated_at datetime(6),
                                      council_name VARCHAR(20) NOT NULL,
                                      organization_type ENUM('DEPARTMENT', 'COLLEGE', 'MAJOR','UNIVERSITY') NOT NULL,
                                      status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') NOT NULL,
                                      user_pk_id BIGINT NOT NULL,
                                      CONSTRAINT FK_orgreq_user FOREIGN KEY (user_pk_id) REFERENCES user(user_pk_id),
                                      CONSTRAINT FK_orgreq_org FOREIGN KEY (organization_id) REFERENCES organization(organization_id)
);


-- auto-generated definition
create table user_information (
                                  information_pk_id bigint auto_increment primary key,
                                  user_pk_id        bigint       not null,
                                  student_id        varchar(255),
                                  school_email      varchar(255) unique,
                                  school_id         bigint,
                                  student_grade     int,
                                  academic_status   enum ('ENROLLED', 'ON_LEAVE'),
                                  nickname          varchar(20) unique,
                                  name              varchar(20) not null,
                                  is_authentication boolean     not null,
                                  is_council_fee    boolean     not null default false,
                                  created_at        datetime(6),
                                  updated_at        datetime(6),

                                  constraint FK_user_info_user foreign key (user_pk_id) references user (user_pk_id),
                                  constraint FK_user_info_university foreign key (school_id) references university (school_id)
);

-- auto-generated definition
create table user_position_history (
                                       history_pk_id   bigint auto_increment primary key,
                                       member_role     enum ('PRESIDENT', 'VICE_PRESIDENT', 'DEPUTY', 'DIRECTOR', 'STAFF') not null,
                                       member_type     varchar(20),
                                       start_date      datetime(6),
                                       end_date        datetime(6),
                                       user_pk_id      bigint not null,
                                       council_id      bigint not null,
                                       organization_id bigint not null,

                                       constraint FK_position_user
                                           foreign key (user_pk_id) references user (user_pk_id),

                                       constraint FK_position_council
                                           foreign key (council_id) references council (council_id),

                                       constraint FK_position_organization
                                           foreign key (organization_id) references organization (organization_id)
);

-- auto-generated definition
create table user_private (
                              user_pk_id          bigint       not null primary key,
                              phone_number        varchar(150) not null unique,
                              password            varchar(100) not null,
                              password_update_at  datetime(6),

                              constraint FK_user_private_user
                                  foreign key (user_pk_id) references user (user_pk_id)
);


-- auto-generated definition
create table user_signup_information (
                                         user_pk_id              bigint       not null primary key,
                                         select_organization_id bigint,
                                         select_school_id       bigint,
                                         enroll_year            varchar(4),
                                         name                   varchar(20)   not null,
                                         input_college_name     varchar(20),
                                         input_school_name      varchar(20),
                                         input_department_name  varchar(20),
                                         created_at datetime(6),
                                         updated_at datetime(6),
                                         is_make_workspace      bit,

                                         constraint FK_user_signup_user
                                             foreign key (user_pk_id) references user (user_pk_id)
);

ALTER TABLE user
    ADD COLUMN organization BIGINT,
    ADD CONSTRAINT fk_user_organizationschool_pk_id
        FOREIGN KEY (organization)
            REFERENCES organization(organization_id);
