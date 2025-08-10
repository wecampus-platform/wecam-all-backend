# WeCam Backend (Multi-Module)
WeCam은 대학 학생회 중심의 협업 플랫폼으로, 학생회와 일반 학생 간의 원활한 소통과 효율적인 업무 관리를 지원하려 함.
본 백엔드 프로젝트는 멀티 모듈 구조로 구성되어 있으며, 다음 3개의 모듈로 나누어져 있음.

- **domain-common** : 모든 서비스에서 공통으로 사용하는 엔티티, DTO, 예외 처리, 유틸리티 모듈
- **wecam-backend** : 일반 사용자(학생, 학생회 구성원)용 서버
- **wecamadminbackend** : 서버 마스터 관리자(Admin)용 서버

--- 

## 프로젝트 구조

wecam-all-backend/
├── domain-common/ # 공통 모듈 (엔티티, DTO, 유틸)
│ ├── model/ # JPA 엔티티 정의
│ ├── dto/ # 공통 DTO
│ ├── enums/ # Enum 타입 정의
│ ├── exceptions/ # 공통 예외 처리
│ └── utils/ # 암호화, 날짜, 파일 등 유틸
│
├── wecam-backend/ # 일반 사용자용 서버
│ ├── controller/ # REST API 컨트롤러
│ ├── service/ # 비즈니스 로직
│ ├── repos/ # JPA Repository
│ ├── config/ # 보안, CORS, Swagger 설정
│ ├── resources/
│ │ ├── application.yml
│ │ ├── application-local.properties
│ │ ├── application-prod.properties
│
├── wecamadminbackend/ # 서버 관리자용 백엔드
│ ├── controller/ # Admin API 컨트롤러
│ ├── service/
│ ├── repos/
│ ├── config/ # SecurityConfig 등 보안설정
│ ├── resources/
│ │ ├── application-local.properties
│ │ ├── application-prod.properties
│
└── build.gradle / settings.gradle

--- 
## 기술 스택

| 구분           | 기술                                                     |
| -------------- | -------------------------------------------------------- |
| **Backend**    | Spring Boot 3.x, Spring Security, Spring Data JPA        |
| **Database**   | MySQL, JPA/Hibernate                                     |
| **Auth**       | JWT, Spring Security                                     |
| **Infra**      | AWS EC2, Docker, GitHub Actions CI/CD                    |
| **Docs**       | Swagger(OpenAPI 3)                                       |
| **ETC**        | Lombok, ModelMapper, Validation, Multipart File Upload   |

--- 

## 모듈별 역할

### 1️⃣ domain-common
- 공통 엔티티 : User, Organization, University, Council, Todo 등
- 공통 DTO : 인증, 응답, 페이지네이션 DTO
- 공통 예외 처리 : BaseException, BaseResponse, BaseResponseStatus
- Enum 관리 : 유저 역할(Role), 상태(Status), 승인 타입 등
- 유틸리티 : 암호화(AES256Util), 날짜 변환, 파일 경로 처리

### 2️⃣ wecam-backend (User API Server)
일반 학생/학생회 구성원 전용 API

### 3️⃣ wecamadminbackend (Admin API Server)
서버 마스터(Admin) 전용 API
---
## API 문서

- **Swagger** UI를 통해 API 명세 확인 가능
- **wecam-backend** : http://localhost:8080/swagger-ui/index.html
- **wecamadminbackend** : http://localhost:8081/swagger-ui/index.html

---

# 모듈별 상세 설명
## domain-common

WeCam 멀티모듈 프로젝트에서 공용 엔티티/Enum/기반 클래스를 제공하는 모듈.
wecam-backend, wecamadminbackend가 이 모듈을 의존하여 동일한 도메인 모델을 공유함.

### Tech & 목적
- Java 21, JPA/Hibernate
- 도메인: 학교/조직 트리, 학생회, 사용자, 소속 인증, 초대코드, 할일(Todo), 미팅 등
- 공통 베이스 엔티티, Enum, 복합키, 연관관계 모델 정의

### 패키지 구조
org.example.model
 ├─ common/
 │   └─ BaseEntity.java            # created_at/updated_at 등 공통 컬럼
 ├─ enums/                         # 도메인 전역 Enum
 ├─ user/                          # User, UserPrivate, UserInformation, UserSignupInformation
 ├─ organization/                  # Organization(트리), OrganizationRequest, OrganizationRequestFile
 ├─ council/                       # Council, CouncilMember, CouncilDepartment, CouncilDepartmentRole, CouncilRolePermission
 ├─ affiliation/                   # AffiliationCertification(복합키), AffiliationFile
 ├─ invitation/                    # InvitationCode, InvitationHistory
 ├─ todo/                          # Todo, TodoFile, TodoManager(+ TodoManagerId @Embeddable)
 ├─ category/                      # Category, CategoryAssignment
 ├─ meeting/                       # Meeting, MeetingFile, MeetingAttendee, MeetingTemplate
 └─ University.java

### 핵심 엔티티
**사용자**
- **User**
-기본키: user_pk_id
- 주요 필드: email, password(해시 전제), role(UserRole), university, organization, academicStatus, studentGrade, nickname, auth 여부 등

-**UserPrivate**: 민감정보(전화번호 등) 분리 저장

-**UserInformation**: 표시/프로필 등 부가 정보

-**UserSignupInformation**: 가입 시 입력 값 스냅샷

**학교/조직**
- **University**

 - **Organization**

parent-children 구조(트리), OrganizationType = UNIVERSITY/COLLEGE/DEPARTMENT/MAJOR

OrganizationRequest, OrganizationRequestFile: 대표자 조직 생성 신청 + 첨부

학생회
Council, CouncilMember, CouncilDepartment, CouncilDepartmentRole, CouncilRolePermission

CouncilMember: 조직별 학생회 구성원/역할/재적 상태(ExitType, MemberRole) 관리

소속 인증
AffiliationCertification + AffiliationCertificationId(복합키)

재학생/신입생 인증(AuthenticationType, AuthenticationStatus)

첨부: AffiliationFile

초대코드
InvitationCode(codeType=CodeType, expiredAt, ... ), InvitationHistory

할 일(Todo)
Todo, TodoFile, TodoManager(+ TodoManagerId 복합키)

상태: ProgressStatus

담당자 다:다 매핑(TodoManager)

카테고리/회의
Category, CategoryAssignment

Meeting, MeetingFile, MeetingAttendee, MeetingTemplate

회의 출석 현황: MeetingAttendanceStatus, 회의 내 역할 : MeetingRole

### 공통 베이스
BaseEntity (@MappedSuperclass)

created_at, updated_at 컬럼 제공

(감사/감사자 컬럼은 소스상 명시X, 필요 시 추후 확장)

### 주요 Enum 
UserRole: UNAUTH, GUEST_STUDENT, STUDENT, COUNCIL, ADMIN

OrganizationType: UNIVERSITY, COLLEGE, DEPARTMENT, MAJOR

AuthenticationType: FRESHMAN, CURRENT_STUDENT …

AuthenticationStatus: PENDING, APPROVED, REJECTED …

ProgressStatus(Todo): TODO, IN_PROGRESS, DONE …

ExitType, MemberRole, CouncilPermissionType, RequestStatus, FileType, AcademicStatus, CodeType 등

### 연관관계 스케치
University 1 - N Organization (type=COLLEGE/DEPARTMENT/...)

Organization (self) 1 - N children (트리)

User N - 1 University, User N - 1 Organization

Council N - 1 Organization

CouncilMember N - 1 Council, N - 1 User, N - 1 Department(Optional)

TodoManager (Embeddable Id: todoId+userPkId) 로 Todo ↔ User 매핑

AffiliationCertification (Embeddable Id) 로 사용자-유형별 단일요청 제약 모델링

### 설계 컨벤션
ID Long, Enum은 대부분 @Enumerated(EnumType.STRING)

민감정보 분리(UserPrivate)

복합키는 @Embeddable + 식별자 클래스로 관리

텍스트 대용량은 @Column(columnDefinition="TEXT") 사용

BaseEntity로 생성/수정 시각 공통화

### 의존/사용 방법 (멀티모듈 기준)
settings.gradle:


include(":domain-common", ":wecam-backend", ":wecamadminbackend")
wecam-backend/build.gradle:


dependencies {
  implementation(project(":domain-common"))
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  // ...
}
### 마이그레이션
실제 스키마는 상위 서비스 모듈(Flyway)에서 관리

이 모듈은 엔티티 정의 전용 (DDL은 서비스 모듈에서 생성/검증)

### 주의사항
User ↔ Organization/University 지연로딩 이슈 주의(Open-In-View 비활성 시 서비스 계층에서 fetch 필요)

Enum 저장 형식 확인(JPA 설정에서 EnumType.STRING 일관 유지)

복합키 엔티티의 Repository 기본키 타입 정의 정확히(예: TodoManagerId)


## WeCam Backend (Spring Boot)

대학 학생회 중심 협업 플랫폼 WeCam의 일반 사용자용 백엔드 모듈.

회원가입(일반/대표자), 로그인/토큰 리프레시

학교/조직 트리 조회(학교 → 단과대/학과)

마이페이지 수정

초대코드 사용

학생회 관리자 기능(학생/구성원/부서/할일/소속인증/하위조직 승인 등)


### Tech Stack
Java 21, Spring Boot 3

Spring Web / Validation / Spring Security + JWT

Spring Data JPA, HikariCP, Flyway

MySQL 8, Redis

Gradle

Swagger(OpenAPI)

### 모듈 경로
(main 기준)
main/
 ├─ java/org/example/wecambackend/...
 └─ resources/
     ├─ application.properties
     ├─ application-local.properties
     └─ application-prod.properties


### 보안 / 인증

JWT: Authorization: Bearer <accessToken>

공개 엔드포인트(permitAll)

/public/**, /swagger-ui/**, /v3/api-docs/**, /auth/check/**

/client/auth/token/refresh, /client/auth/logout (permitAll)

그 외는 인증 필요

일부 관리자/학생회 영역은 X-Council-Id 헤더 필수

### API 요약
0) 유틸/검증
GET /auth/check/email – 이메일 중복 확인

GET /auth/check/phone – 전화번호 중복 확인

GET /auth/check/both – 이메일+전화 동시 확인

1) 공개(회원/조직/로그인)
GET /public/schools – 학교 리스트

GET /public/schools/{schoolId}/organizations – 상위 조직(단과대 등)

GET /public/organizations/{parentId}/children – 하위 조직(학과 등)

POST /public/auth/sign/student – 일반 학생 회원가입
Request: StudentRegisterRequest


{
  "email":"user@ex.com",
  "password":"****",
  "phoneNumber":"010-1234-5678",
  "name":"홍길동",
  "enrollYear":"2023",
  "selectSchoolId":1,
  "selectOrganizationId":303
}
POST /public/auth/sign/leader – 대표자 회원가입
Request: RepresentativeRegisterRequest (선택/수동 입력 필드 포함)

POST /public/auth/login – 로그인
Request: LoginRequest → Response: LoginResponse(access/refresh, email, role, councilList 등)

2) 클라이언트(로그인 후)
Auth

POST /client/auth/token/refresh – 토큰 리프레시

POST /client/auth/logout – 로그아웃

마이페이지

POST /client/user/mypage/userInfo/edit – 이름 수정

POST /client/user/mypage/userOrganization/edit – 소속(입학년도/조직) 수정

POST /client/user/profile-image – 프로필 이미지 업로드

초대코드

POST /client/invitation-code/use/{CodeType} – 초대코드 사용

조직 생성 요청(대표자)

POST /client/organization-request/create – 조직 생성 신청 + 첨부

3) 관리자(학생회 워크스페이스)
헤더 X-Council-Id 필수, 경로에 {councilName} 포함

접근/홈

GET /admin/council/home – 관리자 홈

GET /admin/council/change-council – 학생회 전환(목록)

GET /admin/council/{councilId}/change-council – 특정 전환

하위조직(단과대/학과) 관리

GET /admin/council/{councilName}/organization/subs

GET /admin/council/{councilName}/organization/sub/{councilId}

GET /admin/council/{councilName}/organization/requests

GET /admin/council/{councilName}/organization/request/{requestId}/detail

GET /admin/council/{councilName}/organization/request/{requestId}/file/{fileId}/download

워크스페이스 승인

POST /admin/council/{councilName}/workspace/{requestId}/Approve

POST /admin/council/{councilName}/workspace/{requestId}/reject

초대코드

GET /admin/council/{councilName}/invitation/list

POST /admin/council/{councilName}/invitation/create/{codeType}

PUT /admin/council/{councilName}/invitation/{invitationId}/edit/expiredAt

GET /admin/council/{councilName}/invitation/{invitationId}/show/history

학생/구성원

GET /admin/council/{councilName}/student/students

GET /admin/council/{councilName}/student/search

DELETE /admin/council/{councilName}/student/{userId}

GET /admin/council/{councilName}/member/search

DELETE /admin/council/{councilName}/member/{memberId}

부서(조직 내)

GET /admin/council/{councilName}/composition/members

GET /admin/council/{councilName}/composition/members/department

POST /admin/council/{councilName}/composition/department/create

PUT /admin/council/{councilName}/composition/department/rename

소속 인증(재학생/신입생 서류 등)

GET /admin/council/{councilName}/affiliation/requests/all

GET /admin/council/{councilName}/affiliation/requests/show

POST /admin/council/{councilName}/affiliation/approve

POST /admin/council/{councilName}/affiliation/select/approve

PUT /admin/council/{councilName}/affiliation/reject

DELETE /admin/council/{councilName}/affiliation/delete

할 일(Todo)

POST /admin/council/{councilName}/todo/{councilId}/create

PUT /admin/council/{councilName}/todo/{todoId}/edit

GET /admin/council/{councilName}/todo/{todoId}

GET /admin/council/{councilName}/todo/list

PATCH /admin/council/{councilName}/todo/{todoId}/status

DELETE /admin/council/{councilName}/todo/{todoId}/delete

GET /admin/council/{councilName}/todo/dashboard/todo-summary

### 요청/응답 예시
로그인

curl -X POST http://localhost:8080/public/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"president@example.com","password":"****"}'


{
  "accessToken":"...","refreshToken":"...",
  "email":"president@example.com",
  "role":"PRESIDENT",
  "auth": true,
  "councilList":[{"councilId":303,"councilName":"컴퓨터공학과", ...}]
}
학생회 관리자 API 호출 예시

curl -H "Authorization: Bearer <token>" \
     -H "X-Council-Id: 303" \
     http://localhost:8080/admin/council/컴퓨터공학과/todo/list
     
### 패키지 구조
org.example.wecambackend
 ├─ common/response/           # BaseResponseStatus 등 공통 응답/에러
 ├─ config/
 │   ├─ auth/                  # JwtTokenProvider, JwtAuthenticationFilter
 │   ├─ security/              # SecurityConfig, AOP(Access/Role/Owner), ArgumentResolver
 ├─ controller/
 │   ├─ admin/                 # 학생/부서/할일/소속인증/조직관리
 │   ├─ client/                # 마이페이지, 초대코드
 │   ├─ publicinfo/            # 학교/조직 조회, 회원가입/로그인
 │   └─ ...
 ├─ dto/
 │   ├─ requestDTO/            # *Request
 │   └─ responseDTO/           # *Response
 ├─ repos/                     # Repository
 ├─ service/
 │   ├─ admin/
 │   ├─ client/
 │   └─ auth/
 └─ util/ ...
 
### 에러/응답 규격
공통 래퍼: { isSuccess, code, message, result } 패턴(컨트롤러 반환 래퍼 적용)

에러코드: BaseResponseStatus

예: MISSING_COUNCIL_ID_HEADER, ACCESS_DENIED, ROLE_REQUIRED,
INVALID_INPUT, ORGANIZATION_NOT_FOUND, INVITATION_CODE_EXPIRED 등

### 파일 업로드
저장 경로: UPLOAD_DIR (기본 ./uploads)

URL prefix: UPLOAD_DIR_prefix (기본 /uploads)

Multipart 최대: spring.servlet.multipart.max-file-size=10MB (prod)

### 개발 팁 / 트러블슈팅
환경변수 미로딩: IDE Run/Debug 구성에 .env 연결 or 시스템 환경변수 설정

JDBC Driver 오류: mysql-connector-j 의존성/URL 확인

권한 문제: X-Council-Id 누락/불일치, AOP(Access/Role) 체크 로그 확인

CORS: Security에서 AllowedOriginPatterns("*")로 열어둠(개발용)


## WeCam Admin Backend

학생회/조직 승인 등 관리자 전용 백오피스 서버
폼 로그인 기반의 Spring MVC + Thymeleaf 구조로 동작하며, 조직 생성 요청 승인 등 운영 기능을 제공



### Tech Stack
Java 21, Spring Boot 3

Spring Web (MVC), Thymeleaf

Spring Security (Form Login + BCrypt)

Spring Data JPA, HikariCP, Flyway

MySQL 8

Gradle


### 패키지 구조 
main/
 ├─ java/org/example/wecamadminbackend
 │   ├─ controller/
 │   │   ├─ AdminController.java               # /admin/login, /admin/dashboard
 │   │   ├─ HomeController.java                # "/" → /admin/login 리다이렉트
 │   │   └─ AdminOrganizationController.java   # 조직 생성 요청 리스트/승인
 │   ├─ config/
 │   │   └─ SecurityConfig.java                # 폼 로그인, 인가 정책
 │   ├─ service/
 │   │   ├─ AdminOrganizationService.java
 │   │   └─ CustomAdminUserDetailsService.java # 관리자 인증 소스
 │   ├─ repos/                                 # JPA Repositories
 │   └─ WecamadminbackendApplication.java
 └─ resources/
     ├─ application.properties                 # active=local
     ├─ application-local.properties           # 포트/DB/Flyway/Thymeleaf 설정
     ├─ application-prod.properties            # (비어있음/추가 필요)
     ├─ templates/                             # Thymeleaf 템플릿 (예: admin/organization/list.html)
     └─ static/


### 보안/인증
Spring Security (폼 로그인)
로그인 페이지: /admin/login

성공 시: /admin/dashboard 이동

로그아웃: /admin/logout → /admin/login?logout

비밀번호 해시: BCrypt

인증은 CustomAdminUserDetailsService를 통해 DB에서 로드됩니다.
(관리자 계정 시드가 필요하면 Flyway seed 또는 수동 INSERT로 생성하세요.)

### 주요 화면 & 라우트
AdminController
GET /admin/login – 로그인 페이지

GET /admin/dashboard – 대시보드(로그인 필요)

HomeController
GET / → redirect:/admin/login

AdminOrganizationController (조직 생성 요청 관리)
GET /admin/organization/list – 대기 중인 요청 목록 페이지

AdminOrganizationService#getPendingRequests()로 데이터 주입

템플릿: admin/organization/list

POST /admin/organization/{Id}/approve – 요청 승인

서비스: approveWorkspaceRequest(id)

응답: 200 OK / "워크스페이스 생성 요청 승인 완료."

컨트롤러 네임스페이스는 클래스에 @RequestMapping("admin/organization") 형태로 설정되어 있으며, 메소드 매핑(@GetMapping, @PostMapping)으로 세부 경로가 붙습니다.

### 서비스/레포지토리 
AdminOrganizationService

조직 생성 요청 조회/승인 플로우

CustomAdminUserDetailsService

관리자 계정 로드(UserDetails 반환)

Repositories

OrganizationRequestRepository, OrganizationRepository, CouncilRepository,
CouncilMemberRepository, UniversityRepository, UserRepository,
CouncilDepartmentRoleRepository, PresidentSignupInformationRepository 등

### 설정 (application-local.properties 주요 항목)
서버: server.port=8081, server.address=0.0.0.0

Thymeleaf: classpath:/templates/, .html, 캐시 비활성(dev)

JPA: ddl-auto=validate, open-in-view=false, format_sql=true

Flyway: enabled=true, baseline-on-migrate=true,
locations=classpath:db/migration,classpath:db/seed

### 템플릿/정적 리소스
templates/ 아래 Thymeleaf 페이지 사용 (예: admin/organization/list.html)

운영용 뷰/레이아웃은 템플릿 디렉토리에 추가

### 배포 팁
프로필 분리: --spring.profiles.active=prod + application-prod.properties 채우기

Secrets: DB 계정/암호, PHONE_ENCRYPT_KEY 등은 환경변수/CI Secrets로 주입

DB 마이그레이션: 애플리케이션 기동 시 Flyway 자동 실행

### 트러블슈팅
로그인 무한 루프: 관리자 계정 미존재/비밀번호 불일치 → 관리자 시드 확인

DDL 검증 실패: ddl-auto=validate로 스키마 불일치 발생 → Flyway 스크립트 확인

템플릿 404: Thymeleaf 템플릿 경로/파일명 확인 (templates/…, .html)

접속 포트 충돌: server.port 변경 또는 사용중 프로세스 종료

# WeCam 서버 기능 총정리 (현재 구현 기준)

## 0) 공통 인프라/보안/유틸

### 0-1. 인증/인가 (JWT + Security)

* **구성**

  * `SecurityConfig` (wecam-backend):

    * `permitAll`: `/`, `/swagger-ui/**`, `/v3/api-docs/**`, `/public/**`, `/auth/check/**`, `/client/auth/token/refresh`, `/client/auth/logout`
    * 이외 **모두 인증 필요**
    * CORS(모든 Origin/Method/Header 허용 – 개발용)
  * `JwtAuthenticationFilter` + `JwtTokenProvider`

    * `Authorization: Bearer <token>` 파싱 → 사용자 조회 → `SecurityContext`에 인증 주입
    * `jwt.secret` 환경변수 필요
  * `CurrentUserArgumentResolver` + `CurrentUserContext`

    * 컨트롤러에서 현재 사용자/역할/소속 꺼내 쓰는 헬퍼
* **권한/AOP**

  * `CheckCouncilAccessAspect`, `RoleCheckAspect`, `OwnerCheckAspect`, `PresidentTeamAuthorityAspect`, `CouncilEntityAccessAspect`

    * **학생회 스코프 검증**(요청의 `{councilName}`/`X-Council-Id`와 현재 사용자 소속 매칭)
    * 역할(Role) 기반 접근 제어(팀장/운영진/작성자만 수정/삭제 등)

### 0-2. 공통 응답/예외

* `BaseResponseStatus` (wecam-backend/common/response)

  * 예: `MISSING_COUNCIL_ID_HEADER`, `ACCESS_DENIED`, `ROLE_REQUIRED`,
    `INVALID_INPUT`, `ORGANIZATION_NOT_FOUND`, `INVITATION_CODE_EXPIRED`,
    `EMAIL_DUPLICATED`, `ALREADY_PROCESSED`, `COUNCIL_MISMATCH` 등 다수
* `GlobalExceptionHandler`

  * 위 상태코드를 공통 JSON 래퍼로 리턴

### 0-3. 파일 저장

* 클라이언트: `UserProfileController` → `UserProfileService`
* 관리자/운영: `AdminFileStorageService`, `service/client/common/filesave/FileStorageService`
* 설정 키: `UPLOAD_DIR`, `app.file.url-prefix`

---

## 1) 퍼블릭 영역 (로그인 전)

### 1-1. 계정 유효성 검사

* **컨트롤러**: `AuthController` (`/auth/check/*`)

  * `GET /auth/check/email` / `phone` / `both`
* **서비스 로직 요약**

  1. 이메일/전화번호 존재 여부 조회
  2. 중복이면 `EMAIL_DUPLICATED`/`PHONE_DUPLICATED`/`EMAIL_PHONE_DUPLICATED`
  3. 사용 가능 시 OK
* **주요 예외**: 중복, 형식 오류

### 1-2. 공용 데이터 조회 (학교/조직 트리)

* **컨트롤러**: `PublicInfoController` (`/public`)

  * `GET /public/schools`
  * `GET /public/schools/{schoolId}/organizations` (상위 단계)
  * `GET /public/organizations/{parentId}/children` (하위 단계)
* **서비스 로직 요약**

  1. `University`, `Organization`(트리) 조회
  2. `OrganizationType` 기준 필터링
* **주요 예외**: `SCHOOL_NOT_FOUND`, `ORGANIZATION_NOT_FOUND`

### 1-3. 회원가입/로그인

* **컨트롤러**: `PublicAuthController` (`/public/auth`)

  * `POST /sign/student` (일반 학생 가입) – `StudentRegisterRequest`
  * `POST /sign/leader` (대표자 가입) – `RepresentativeRegisterRequest`
  * `POST /login` → `LoginResponse(accessToken, refreshToken, role, councilList...)`
* **서비스**: `AuthService`
* **서비스 로직 (요약)**

  1. **회원가입**

     * 입력 검증(이메일/비번/입학년도/조직 선택)
     * `User` + `UserSignupInformation` 생성
     * 대표자는 입력/선택 혼합(학교/단과대/학과) 처리
  2. **로그인**

     * 사용자 조회 → 비밀번호 매칭 → JWT **access/refresh** 토큰 발급
     * 소속 학생회 목록(councilList) 포함 리턴
* **주요 예외**: `INVALID_SIGNUP_REQUEST`, `EMAIL_INFO_NOT_FOUND`, `INVALID_USER`

---

## 2) 클라이언트 영역 (로그인 필요)

### 2-1. 토큰/세션

* **컨트롤러**: `ClientAuthController` (`/client/auth`)

  * `POST /token/refresh` – 리프레시로 액세스 재발급
  * `POST /logout` – 논리적 로그아웃(클라이언트 측 토큰 폐기)
* **로직**: `JwtTokenProvider` 만료 확인 → 재발급/거부

### 2-2. 마이페이지

* **컨트롤러**: `UserMyPageController` (`client/user/mypage`)

  * `POST /userInfo/edit` – 이름 등 기본정보 수정
  * `POST /userOrganization/edit` – 입학년도/조직 변경
* **서비스**: `MyPageService`
* **로직**

  1. 현재 사용자 식별 → 입력값 검증
  2. `UserInformation`/`User` 업데이트
  3. 필요한 경우 조직/학적 상태 동기화
* **예외**: `USER_NOT_FOUND`, `INVALID_INPUT`, `ONLY_AUTHOR_CAN_MODIFY`

### 2-3. 프로필 이미지 업로드

* **컨트롤러**: `UserProfileController` (`/client/user`)

  * `POST /profile-image`
* **서비스**: `UserProfileService`
* **로직**: 멀티파트 검증 → 파일 저장 → 사용자 프로필 경로 업데이트
* **예외**: `FILE_EMPTY`, `EMPTY_FILENAME`

### 2-4. 소속 인증(학생 인증)

* **컨트롤러**: `UserAffiliationController` (`/client/user/affiliation`)

  * `POST /freshman` – 신입생 인증 신청
  * `POST /currentStudent` – 재학생 인증 신청
* **서비스**: `AffiliationService`
* **로직**

  1. **타입별(AUTHENTICATION\_TYPE)** 필수 서류/필드 검증
  2. `AffiliationCertification`(복합키) 생성/중복 방지
  3. 첨부파일 저장(`AffiliationFile`)
  4. 상태는 초기 `PENDING`
* **예외**: `AFFILIATION_ALREADY_EXISTS`, `INVALID_INPUT`

### 2-5. 초대코드 사용

* **컨트롤러**: `ClientInvitationCodeController` (`/client/invitation-code`)

  * `POST /use/{CodeType}`
* **서비스**: (관리자초대/조직초대 공통 처리) `InvitationCodeService` 일부 로직 재사용
* **로직**

  1. 코드 조회 → 만료/유형 검증
  2. 사용자-조직(혹은 학생회) 연결/권한 부여
  3. `InvitationHistory` 기록
* **예외**: `INVITATION_CODE_NOT_FOUND`, `INVITATION_CODE_EXPIRED`, `INVALID_INVITE_CODE`

### 2-6. 조직 생성 요청 (대표자)

* **컨트롤러**: `OrganizationRequestController` (`/client/organization-request`)

  * `POST /create` – 신청서 + 첨부
* **서비스**: (백엔드 측 승인용과 맞물림)
* **로직**

  1. 대표자 권한/본인확인
  2. `OrganizationRequest` 생성 + 파일(`OrganizationRequestFile`) 저장
  3. 상태는 `PENDING`
* **예외**: `ALREADY_EXIST_COUNCIL`, `INVALID_INPUT`

---

## 3) 관리자(학생회 운영) 영역 – wecam-backend의 /admin 스코프

> 경로 공통 Prefix: `/admin/council/{councilName}`
> 헤더: `X-Council-Id` 필수 (AOP로 소속 검증)

### 3-1. 접근/홈

* **컨트롤러**: `CouncilAccessController` (`/admin/council`)

  * `GET /home` – 관리자 홈
  * `GET /change-council` / `GET /{councilId}/change-council` – 학생회 전환
* **로직**: 사용자 소속 학생회 목록 조회 → 전환 컨텍스트 설정

### 3-2. 하위 조직 관리

* **컨트롤러**: `AdminOrganizationController` (`/organization`)

  * `GET /subs` – 하위 조직 목록
  * `GET /sub/{councilId}` – 단일 상세
  * `GET /requests` – 하위 조직 요청 목록
  * `GET /request/{requestId}/detail` – 요청 상세
  * `GET /request/{requestId}/file/{fileId}/download` – 파일 다운로드 URL
* **서비스**: `AdminOrganizationService`
* **로직**

  1. 상위 학생회 권한 검증
  2. 요청/조직 조회 → 세부 정보 조합
  3. 파일 접근 권한 검증 → 서명 URL/직접 링크 반환
* **예외**: `NO_PERMISSION_TO_MANAGE`, `ACCESS_DENIED_REQUEST`, `ORGANIZATION_NOT_FOUND`

### 3-3. 워크스페이스 승인/거절

* **컨트롤러**: `WorkSpaceManageController` (`/workspace`)

  * `POST /{requestId}/Approve`
  * `POST /{requestId}/reject`
* **서비스**: `WorkSpaceManageService`
* **로직**

  1. `OrganizationRequest` 상태 확인(`PENDING`만 처리)
  2. 승인 시 `Council`/조직 트리 생성/매핑
  3. 요청 상태 `APPROVED/REJECTED` 업데이트 + 알림 트리거(있다면)
* **예외**: `ALREADY_PROCESSED`, `ACCESS_DENIED_REQUEST`

### 3-4. 초대코드 관리

* **컨트롤러**: `InvitationCodeController` (`/invitation`)

  * `GET /list`
  * `POST /create/{codeType}`
  * `PUT /{invitationId}/edit/expiredAt`
  * `GET /{invitationId}/show/history`
* **서비스**: `InvitationCodeService`
* **로직**

  1. 코드 생성(만료 정책: 최소 5분 이후 등)
  2. 수정(만료일 갱신)
  3. 사용 이력 조회
* **예외**: `INVALID_EXPIRATION_TIME`, `INVITATION_CODE_NOT_FOUND`

### 3-5. 학생/구성원 관리

* **컨트롤러**: `StudentController` (`/student`)

  * `GET /students` – 학부 소속 학생 목록
  * `GET /search` – 학생 검색
  * `DELETE /{userId}` – 제명/탈퇴(학생)
* **컨트롤러**: `CouncilMemberController` (`/member`)

  * `GET /search` – 학생회 구성원 검색
  * `DELETE /{memberId}` – 제명/탈퇴(구성원)
* **서비스**: `StudentService`, `CouncilMemberService`
* **로직**

  1. 조직 레벨/학번/학년/상태 필터링
  2. 권한 검증(해당 학생회 운영진만 제명 가능)
  3. 제명 시 `ExitType`, `Status` 업데이트 + 사유 기록
* **예외**: `INVALID_COLLEGE_ORG`, `INVALID_DEPARTMENT_ORG`, `NO_PERMISSION_TO_MANAGE`

### 3-6. 부서(Department) 구성 관리

* **컨트롤러**: `CouncilCompositionController` (`/composition`)

  * `GET /members` – 부서별 구성원 View
  * `GET /members/department` – 부서 목록/멤버 맵핑
  * `POST /department/create`
  * `PUT /department/rename`
* **서비스**: `CouncilDepartmentService`
* **로직**

  1. 부서 엔티티 생성/이름 중복 검증
  2. 멤버와 역할(`CouncilDepartmentRole`) 매핑/수정
  3. 읽기 시 Left Join으로 “멤버 없어도 부서 1행 보장” 형태 결과 제공
* **예외**: `INVALID_INPUT`, `ORGANIZATION_NOT_FOUND`

### 3-7. 소속 인증(관리자 검수)

* **컨트롤러**: `AffiliationCertificationController` (`/affiliation`)

  * `GET /requests/all` – 전체 목록
  * `GET /requests/show` – 상세
  * `POST /approve` – 단건 승인
  * `POST /select/approve` – 선택 승인(복수)
  * `PUT /reject` – 거절
  * `DELETE /delete` – 삭제
* **서비스**: `AffiliationCertificationAdminService`
* **로직**

  1. 요청 상태 확인(`PENDING`)
  2. 승인 시 `AuthenticationStatus=APPROVED` 반영 + 사용자 Role 승격(`GUEST_STUDENT`→`STUDENT` 등)
  3. 거절/삭제도 상태/이력 관리
* **예외**: `ALREADY_PROCESSED`, `ACCESS_DENIED`

### 3-8. 할 일(Todo)

* **컨트롤러**: `TodoController` (`/todo`)

  * `POST /{councilId}/create`
  * `PUT /{todoId}/edit`
  * `GET /{todoId}`
  * `GET /list`
  * `PATCH /{todoId}/status`
  * `DELETE /{todoId}/delete`
  * `GET /dashboard/todo-summary`
* **서비스**: `TodoService`
* **로직**

  1. 생성: 제목/마감일 필수, 담당자(`TodoManager`) 매핑, 첨부(`TodoFile`) 저장
  2. 수정: **작성자/권한** 검증(AOP `OwnerCheckAspect`/`RoleCheckAspect`) 후 필드 업데이트
  3. 상태 변경: `ProgressStatus` 전환 + 변경 이력(필요시)
  4. 목록/상세: 학생회 스코프 내 페이징/정렬
  5. 요약: “오늘/이번 주/받은 할 일/보낸 할 일” 카운트 및 완료율 계산 응답
* **예외**: `ONLY_AUTHOR_CAN_MODIFY`, `INVALID_COUNCIL_ACCESS`, `MISSING_COUNCIL_ID_HEADER`

---

## 4) 서버 관리자(백오피스) – wecamadminbackend

> **폼 로그인 기반**(세션), 포트 기본 8081

### 4-1. 접근 라우트

* `HomeController`

  * `GET /` → `redirect:/admin/login`
* `AdminController`

  * `GET /admin/login` – 로그인 페이지
  * `GET /admin/dashboard` – 로그인 성공 후 대시보드
* 보안: `SecurityConfig`

  * 로그인 성공 시 `/admin/dashboard`로 이동
  * `/admin/logout` 지원

### 4-2. 조직 생성 요청 승인

* **컨트롤러**: `AdminOrganizationController` (`/admin/organization`)

  * `GET /list` – 승인 대기 목록 페이지 (Thymeleaf: `admin/organization/list`)
  * `POST /{Id}/approve` – 단건 승인
* **서비스**: `AdminOrganizationService`
* **로직**

  1. `OrganizationRequest` 상태 `PENDING` 확인
  2. 승인 시 학생회(`Council`) 및 관련 조직 레코드 생성/연결
  3. 요청자/관련자에게 알림(있다면)
* **예외**: `ALREADY_PROCESSED`, `ACCESS_DENIED_REQUEST`

---

## 5) 도메인 모델(요약: domain-common 기준)

* **User 그룹**: `User`, `UserPrivate`, `UserInformation`, `UserSignupInformation`
* **조직 트리**: `University` → `Organization(UNIVERSITY/COLLEGE/DEPARTMENT/MAJOR)`
* **학생회**: `Council`, `CouncilMember`, `CouncilDepartment`, `CouncilDepartmentRole`, `CouncilRolePermission`
* **인증**: `AffiliationCertification(+Id)`, `AffiliationFile`, `AuthenticationType/Status`
* **초대**: `InvitationCode`, `InvitationHistory`, `CodeType`
* **Todo**: `Todo`, `TodoFile`, `TodoManager(+TodoManagerId)`, `ProgressStatus`
* **그 외**: `Category`, `CategoryAssignment`, `Meeting*` 일체 (회의/템플릿/참석)

**공통 규칙**

* `@Enumerated(EnumType.STRING)` 사용
* `BaseEntity`로 `created_at`, `updated_at` 공통 제공
* 복합키는 `@Embeddable` Id 클래스

---

## 6) 환경 변수(핵심)

### wecam-backend (`application-local.properties`)

```properties
# DB
DB_URL=jdbc:mysql://localhost:3306/wecam?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
DB_USER=wecam
DB_PASSWORD=secret

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT / 보안
JWT_SECRET=change_me
PHONE_ENCRYPT_KEY=change_me_32bytes

# 파일
UPLOAD_DIR=./uploads
UPLOAD_DIR_prefix=/uploads
```

### wecamadminbackend (`application-local.properties`)

```properties
server.port=8081
DB_URL=jdbc:mysql://localhost:3306/wecam?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
DB_USER=wecam
DB_PASSWORD=secret
spring.flyway.enabled=true
```

---

## 7) 빌드/실행

```bash
# 전체 빌드
./gradlew clean build

# wecam-backend
cd wecam-backend
java -jar build/libs/wecam-backend-*.jar

# wecamadminbackend
cd ../wecamadminbackend
java -jar build/libs/wecamadminbackend-*.jar
```

---

## 8) 트러블슈팅 포인트

* **환경변수 미주입**: `application.properties`에서 `${}`만 있고 값이 없으면 JDBC 드라이버 에러 → `.env`/Run Config 확인
* **학생회 스코프 오류**: `X-Council-Id` 누락/불일치 → AOP에서 `INVALID_COUNCIL_ACCESS`/`COUNCIL_MISMATCH`
* **Enum 저장 타입 혼선**: 복합키/EmbeddedId와 섞일 때 `TINYINT`로 저장되지 않게 `EnumType.STRING` 재확인
* **파일 업로드**: 빈 파일/파일명 빈 값 → `FILE_EMPTY`/`EMPTY_FILENAME`
* **초대코드 만료 정책**: 최소 5분 이후로 설정 (`INVALID_EXPIRATION_TIME` 방지)

  # 진행 상황 (표)

  좋지! 앞으로 계속 추가/업데이트하기 쉬운 **진행상황(Status) 표 템플릿** 만들어줬고, 네가 정리해준 현재 구현 기준으로 **초기 데이터도 다 채워**놨어. 그대로 README에 붙여 쓰면 됨. 필요하면 섹션별로 쪼개서 써도 되고.

---

# WeCam 서버 기능 진행상황 표 (추가/업데이트용)

**Legend:** ✅ 완료 · 🟡 일부완료/검증중 · 🔴 진행전/이슈 · 🧪 테스트필요 · 🧩 의존기능대기

## 마스터 트래커

| 영역    | 기능           | 엔드포인트(요약)                                                                                                                                                                             | 서비스/클래스                                                               | 권한/AOP 포인트               | 주요 예외                                        | 상태 | 비고/다음 할 일                 |
| ----- | ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------- | ------------------------ | -------------------------------------------- | -- | ------------------------- |
| 공통    | 인증/인가 설정     | `SecurityConfig`, `JwtAuthenticationFilter`                                                                                                                                           | `JwtTokenProvider`, `CurrentUserArgumentResolver`                     | 전역 인증·CORS, Bearer 토큰 주입 | 토큰만료, 미인증                                    | ✅  | 운영 프로필 CORS 축소 필요         |
| 공통    | 공통 예외/응답     | `GlobalExceptionHandler`                                                                                                                                                              | `BaseResponseStatus`                                                  | -                        | 다수(ACCESS\_DENIED 등)                         | ✅  | 코드/메시지 표 통합 문서화           |
| 공통    | 파일 저장        | `POST /client/user/profile-image`                                                                                                                                                     | `UserProfileService`, `FileStorageService`, `AdminFileStorageService` | 파일 경로 접근 검증              | `FILE_EMPTY`, `EMPTY_FILENAME`               | ✅  | S3 전환 시 인터페이스 분리 고려       |
| 퍼블릭   | 계정 유효성 검사    | `GET /auth/check/*`                                                                                                                                                                   | `AuthController`                                                      | -                        | 중복 관련                                        | ✅  | phone/e-mail 포맷 추가 검증?    |
| 퍼블릭   | 공용 트리 조회     | `GET /public/schools`, `/schools/{id}/organizations`, `/organizations/{id}/children`                                                                                                  | `PublicInfoController`                                                | -                        | `SCHOOL_NOT_FOUND`, `ORGANIZATION_NOT_FOUND` | ✅  | 캐시 도입 검토                  |
| 퍼블릭   | 회원가입(학생/대표)  | `POST /public/auth/sign/*`, `POST /public/auth/login`                                                                                                                                 | `AuthService`, `PublicAuthController`                                 | 입력 검증                    | `INVALID_SIGNUP_REQUEST`, `EMAIL_DUPLICATED` | ✅  | 비번 정책/레이트리밋 추가            |
| 클라이언트 | 토큰 갱신/로그아웃   | `POST /client/auth/token/refresh`, `/logout`                                                                                                                                          | `ClientAuthController`, `JwtTokenProvider`                            | 리프레시 검증                  | 만료/위조                                        | ✅  | 블랙리스트 전략 여부 결정            |
| 클라이언트 | 마이페이지 수정     | `POST /client/user/mypage/userInfo/edit`, `/userOrganization/edit`                                                                                                                    | `MyPageService`, `UserMyPageController`                               | 본인만 수정                   | `USER_NOT_FOUND`, `INVALID_INPUT`            | ✅  | 변경 이력(Audit) 필요시 추가       |
| 클라이언트 | 프로필 이미지      | `POST /client/user/profile-image`                                                                                                                                                     | `UserProfileService`                                                  | 본인만 수정                   | 파일 예외                                        | ✅  | 파일 크기/타입 제한 강화            |
| 클라이언트 | 소속 인증 신청     | `POST /client/user/affiliation/freshman`, `/currentStudent`                                                                                                                           | `AffiliationService`, `UserAffiliationController`                     | 본인/필수 서류 검증              | `AFFILIATION_ALREADY_EXISTS`                 | ✅  | 파일 바이러스 스캔(옵션)            |
| 클라이언트 | 초대코드 사용      | `POST /client/invitation-code/use/{CodeType}`                                                                                                                                         | `InvitationCodeService`, `ClientInvitationCodeController`             | 코드 유형·만료 검증              | `INVITATION_CODE_EXPIRED`                    | ✅  | 사용 실패 사유 UX 개선            |
| 클라이언트 | 조직 생성 요청     | `POST /client/organization-request/create`                                                                                                                                            | `OrganizationRequestController`                                       | 대표 권한 검증                 | `ALREADY_EXIST_COUNCIL`                      | ✅  | 첨부 필수 항목 체크 강화            |
| 관리자   | 접근/홈/전환      | `GET /admin/council/home`, `/change-council`                                                                                                                                          | `CouncilAccessController`                                             | `X-Council-Id` 스코프       | `MISSING_COUNCIL_ID_HEADER`                  | ✅  | 최근 전환 히스토리 제공 고려          |
| 관리자   | 하위 조직 조회/요청  | `GET /organization/subs`, `/sub/{id}`, `/requests`, `/request/{id}/detail`, `/file/{fileId}/download`                                                                                 | `AdminOrganizationService`, `AdminOrganizationController`             | 상위-하위 권한                 | `ACCESS_DENIED_REQUEST`                      | ✅  | 서명 URL 만료시간 조정            |
| 관리자   | 워크스페이스 승인/거절 | `POST /workspace/{requestId}/Approve`, `/reject`                                                                                                                                      | `WorkSpaceManageService`, `WorkSpaceManageController`                 | 상태 PENDING만              | `ALREADY_PROCESSED`                          | ✅  | 승인 → 알림 연동(추후)            |
| 관리자   | 초대코드 관리      | `GET /invitation/list`, `POST /create/{codeType}`, `PUT /{id}/edit/expiredAt`, `GET /{id}/show/history`                                                                               | `InvitationCodeService`, `InvitationCodeController`                   | 만료 정책                    | `INVALID_EXPIRATION_TIME`                    | ✅  | 타입별 기본 만료 템플릿             |
| 관리자   | 학생 목록/검색     | `GET /student/students`, `/search`, `DELETE /student/{userId}`                                                                                                                        | `StudentService`, `StudentController`                                 | 조직 레벨·운영진 권한             | `INVALID_COLLEGE_ORG`                        | ✅  | 페이징/정렬 파라미터 표준화           |
| 관리자   | 구성원 목록/검색/제명 | `GET /member/search`, `DELETE /member/{memberId}`                                                                                                                                     | `CouncilMemberService`, `CouncilMemberController`                     | 운영진 권한                   | `NO_PERMISSION_TO_MANAGE`                    | ✅  | 제명 사유 enum 통일             |
| 관리자   | 부서 구성 관리     | `GET /composition/members`, `/members/department`, `POST /department/create`, `PUT /department/rename`                                                                                | `CouncilDepartmentService`, `CouncilCompositionController`            | 이름 중복·역할 매핑              | `INVALID_INPUT`                              | 🟡 | SQL Left Join 뷰 최적화 이슈 점검 |
| 관리자   | 할 일 CRUD/요약  | `POST /todo/{councilId}/create`, `PUT /todo/{id}/edit`, `GET /todo/{id}`, `GET /todo/list`, `PATCH /todo/{id}/status`, `DELETE /todo/{id}/delete`, `GET /todo/dashboard/todo-summary` | `TodoService`, `TodoController`                                       | 작성자/권한 AOP               | `ONLY_AUTHOR_CAN_MODIFY`                     | ✅  | 대량 담당자 추가 성능 점검           |
| 백오피스  | 폼 로그인/대시보드   | `GET /admin/login`, `/admin/dashboard`                                                                                                                                                | `SecurityConfig`, `AdminController`, `HomeController`                 | 세션 인증                    | -                                            | ✅  | 접근 로그/감사 필요시 추가           |
| 백오피스  | 조직요청 승인      | `GET /admin/organization/list`, `POST /admin/organization/{Id}/approve`                                                                                                               | `AdminOrganizationService`, `AdminOrganizationController`             | 상태 PENDING만              | `ALREADY_PROCESSED`                          | ✅  | 거절 플로우(UI) 확장 계획          |
---

* **상태 업데이트 규칙**:

  * ✅: 코드/핵심 테스트/에러처리까지 완료
  * 🟡: 기능 동작은 하나 엣지케이스/성능/권한 일부 미검증
  * 🔴: 컨트롤러/서비스 뼈대만 있거나 미착수
* **권한/AOP 적기**: `CheckCouncilAccessAspect`, `OwnerCheckAspect`, `RoleCheckAspect` 등 **어떤 AOP가 트리거되는지** 꼭 표에 남겨두기.
* **예외 코드**: `BaseResponseStatus`의 **심볼 그대로** 표에 기록(문구 X, 코드명 O). 추후 다국어/문구 변경에도 표 유지 쉬움.
* **엔드포인트 표기**: 가능하면 **메서드 + 경로**(예: `GET /admin/...`)로 짧게.
* **비고**: “캐시 도입”, “S3 전환”, “테스트 보강” 같이 다음 스텝을 한 줄로.
