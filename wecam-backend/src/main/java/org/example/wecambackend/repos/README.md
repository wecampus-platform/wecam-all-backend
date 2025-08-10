# Repository 구조

이 폴더는 데이터 접근 계층의 Repository들을 도메인별로 그룹화하여 관리합니다.

## 패키지 구조

### 📁 user/
사용자 관련 Repository들
- `UserRepository.java` - 사용자 기본 CRUD
- `UserCustomRepository.java` - 사용자 커스텀 쿼리 인터페이스
- `UserCustomRepositoryImpl.java` - 사용자 커스텀 쿼리 구현체
- `UserRepositoryCustom.java` - 사용자 검색 관련 커스텀 쿼리
- `UserSignupInformationRepository.java` - 회원가입 정보
- `UserInformationRepository.java` - 사용자 상세 정보
- `UserPrivateRepository.java` - 사용자 개인정보

### 📁 council/
학생회 관련 Repository들
- `CouncilRepository.java` - 학생회 기본 CRUD
- `CouncilMemberRepository.java` - 학생회 구성원 관리
- `CouncilMemberCustomRepository.java` - 학생회 구성원 커스텀 쿼리 인터페이스
- `CouncilMemberCustomRepositoryImpl.java` - 학생회 구성원 커스텀 쿼리 구현체
- `CouncilDepartmentRepository.java` - 학생회 부서 관리
- `CouncilDepartmentRoleRepository.java` - 학생회 부서 역할 관리
- `CouncilRolePermissionRepository.java` - 학생회 역할 권한 관리

### 📁 todo/
할일 관리 관련 Repository들
- `TodoRepository.java` - 할일 기본 CRUD
- `TodoManagerRepository.java` - 할일 담당자 관리
- `TodoFileRepository.java` - 할일 첨부파일 관리

### 📁 invitation/
초대 관련 Repository들
- `InvitationCodeRepository.java` - 초대 코드 관리
- `InvitationHistoryRepository.java` - 초대 이력 관리

### 📁 organization/
조직 관련 Repository들
- `OrganizationRepository.java` - 조직 기본 CRUD
- `OrganizationRequestRepository.java` - 조직 요청 관리
- `OrganizationRequestFileRepository.java` - 조직 요청 첨부파일

### 📁 affiliation/
소속 인증 관련 Repository들
- `AffiliationCertificationRepository.java` - 소속 인증 기본 CRUD
- `AffiliationCertificationRepositoryCustom.java` - 소속 인증 커스텀 쿼리 인터페이스
- `AffiliationCertificationRepositoryImpl.java` - 소속 인증 커스텀 쿼리 구현체
- `AffiliationFileRepository.java` - 소속 인증 첨부파일

### 📁 school/
학교 관련 Repository들
- `SchoolRepository.java` - 학교 정보 관리

## 리팩토링 이점

1. **논리적 그룹화**: 관련된 Repository들이 도메인별로 그룹화되어 코드 탐색이 용이
2. **책임 분리**: 각 패키지가 명확한 책임을 가짐
3. **유지보수성**: 관련 코드들이 한 곳에 모여 있어 수정 시 영향 범위 파악이 쉬움
4. **확장성**: 새로운 도메인 추가 시 해당 패키지만 생성하면 됨
5. **가독성**: 프로젝트 구조를 이해하기 쉬워짐

## 사용 예시

```java
// 기존
import org.example.wecambackend.repos.UserRepository;

// 변경 후
import org.example.wecambackend.repos.user.UserRepository;
```
