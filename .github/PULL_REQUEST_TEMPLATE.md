## 🔀 Pull Request 제목
[기능명] 간단한 요약 (예: 마이 페이지 조회 기능 구현)

---

## ✅ 작업 개요
<!---- 변경 사항 및 관련 이슈에 대해 간단하게 작성해주세요. 어떻게보다 무엇을 왜 수정했는지 설명해주세요. -->

<!---- Resolves: #(Isuue Number) -->

- 작업 목적 또는 기능 설명
- 기존 문제 또는 구현 배경 간단히 명시

예시:
> 로그인한 사용자의 마이페이지를 조회하고, 소속 조직 계층 정보까지 프론트에 전달하기 위한 API 구현

---

## PR 유형
어떤 변경 사항이 있나요?

- [ ] 새로운 기능 추가
- [ ] 버그 수정
- [ ] CSS 등 사용자 UI 디자인 변경
- [ ] 코드에 영향을 주지 않는 변경사항(오타 수정, 탭 사이즈 변경, 변수명 변경)
- [ ] 코드 리팩토링
- [ ] 주석 추가 및 수정
- [ ] 문서 수정
- [ ] 테스트 추가, 테스트 리팩토링
- [ ] 빌드 부분 혹은 패키지 매니저 수정
- [ ] 파일 혹은 폴더명 수정
- [ ] 파일 혹은 폴더 삭제

## 🔧 주요 변경 사항

| 구분 | 변경 내용 |
|------|-----------|
| 기능 추가 | `/client/user/mypage` GET API 추가 |
| 서비스 구현 | `MyPageService.getMyPageInfo()`에서 사용자 + 조직 정보 조합 |
| 보안 처리 | 커스텀 권한 어노테이션 `@IsStudent`, `@IsCouncil` 등 AOP 적용 |
| 예외 처리 | `UnauthorizedException`, `DuplicateSubmissionException` 추가 및 전역 처리 |
| 응답 통일 | `ErrorResponse` DTO 사용, 모든 예외 통일된 JSON 반환 |
| 문서화 | Swagger `@ApiResponse`, `@Schema` 어노테이션 명세 추가 |
| 기타 | `@BatchSize` 및 fetch join으로 조직 계층 N+1 문제 최소화 |

---

## 🧪 테스트 내용

- [x] 정상 사용자 요청 시 마이페이지 정보 조회 성공
- [x] 비로그인 시 401 Unauthorized 반환
- [x] 학생회가 아닌 사용자가 학생회 기능 접근 시 403 Forbidden
- [x] 이미 인증 요청을 제출한 경우 409 Conflict 발생

---

## 🧾 예외 응답 예시 (Swagger 문서화 연동)

```json
{
  "status": 401,
  "message": "로그인이 필요합니다.",
  "detail": "Unauthorized"
}
