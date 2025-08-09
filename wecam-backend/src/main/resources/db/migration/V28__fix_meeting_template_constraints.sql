-- V28__insert_meeting_template_dummy_data.sql

-- 회의록 템플릿 더미데이터 삽입

-- 전체 공통 템플릿 (시스템 제공)
INSERT INTO `meeting_template` (`name`, `description`, `content_template`, `is_default`, `council_id`, `created_by`) VALUES
('기본 회의록', '모든 학생회에서 사용할 수 있는 기본 회의록 템플릿', 
'# 회의록

## 📅 회의 정보
- **일시**: {{meeting_datetime}}
- **장소**: {{location}}
- **참석자**: {{attendees}}

## 📋 안건
1. 
2. 
3. 

## 💬 토론 내용
### 안건 1
- 

### 안건 2
- 

### 안건 3
- 

## ✅ 결의사항
1. 
2. 
3. 

## 📝 특이사항
- 

## 📎 첨부파일
- ', 
NULL, NULL, NULL);

-- 학생회 1의 기본 템플릿
INSERT INTO `meeting_template` (`name`, `description`, `content_template`, `is_default`, `council_id`, `created_by`) VALUES
('정기회의', '정기적인 학생회 회의를 위한 템플릿', 
'# 회의록

## 📅 회의 정보
- **일시**: {{meeting_datetime}}
- **장소**: {{location}}
- **참석자**: {{attendees}}

## 📋 안건
1. 
2. 
3. 

## 💬 토론 내용
### 안건 1
- 

### 안건 2
- 

### 안건 3
- 

## ✅ 결의사항
1. 
2. 
3. 

## 📝 다음 회의
- **일시**: 
- **안건**: 

## 📎 첨부파일
- ', 
TRUE, 1, 1);

-- 학생회 1의 신입생 OT 템플릿
INSERT INTO `meeting_template` (`name`, `description`, `content_template`, `is_default`, `council_id`, `created_by`) VALUES
('신입생 OT', '신입생 오리엔테이션을 위한 템플릿', 
'# 신입생 OT 회의록

## 📅 회의 정보
- **일시**: {{meeting_datetime}}
- **장소**: {{location}}
- **참석자**: {{attendees}}

## 🎯 OT 목표
- 

## 📋 진행 내용
### 1. 학생회 소개
- 

### 2. 조직도 및 역할 설명
- 

### 3. 주요 활동 안내
- 

### 4. Q&A
- 

## ✅ 결정사항
1. 
2. 
3. 

## 📝 특이사항
- ',
NULL, 1, 1);

-- 학생회 4의 간식행사 템플릿
INSERT INTO `meeting_template` (`name`, `description`, `content_template`, `is_default`, `council_id`, `created_by`) VALUES
('간식행사', '학생회 간식행사 기획 및 진행을 위한 템플릿', 
'# 간식행사 회의록

## 📅 회의 정보
- **일시**: {{meeting_datetime}}
- **장소**: {{location}}
- **참석자**: {{attendees}}

## 🎯 간식행사 목표
- **행사명**: 
- **목적**: 
- **예상 참여 인원**: 

## 📋 진행 내용

### 1. 행사 개요
- **행사 일시**: 
- **행사 장소**: 
- **행사 시간**: 
- **대상**: 

### 2. 예산 계획
- **총 예산**: 
- **간식 구매비**: 
- **부대비용**: 
- **예비비**: 

### 3. 간식 메뉴 선정
| 메뉴명 | 수량 | 단가 | 총액 | 비고 |
|--------|------|------|------|------|
|        |      |      |      |      |
|        |      |      |      |      |
|        |      |      |      |      |

### 4. 구매 및 배송 계획
- **구매처**: 
- **주문 일시**: 
- **배송 일시**: 
- **배송 방법**: 
- **보관 방법**: 

### 5. 행사 진행 계획
- **준비 시간**: 
- **배치 시간**: 
- **행사 진행**: 
- **정리 시간**: 

### 6. 담당자 배정
| 역할 | 담당자 | 연락처 | 비고 |
|------|--------|--------|------|
| 총괄 |        |        |      |
| 예산 관리 |        |        |      |
| 구매 담당 |        |        |      |
| 행사 진행 |        |        |      |
| 정리 담당 |        |        |      |

### 7. 홍보 계획
- **홍보 방법**: 
- **홍보 일정**: 
- **담당자**: 

### 8. 안전 및 위생 관리
- **위생 관리**: 
- **안전 관리**: 
- **응급 상황 대처**: 

## ✅ 결정사항
1. 
2. 
3. 

## ⚠️ 주의사항
- 

## 📝 특이사항
- 

## 📎 첨부파일
- 예산서
- 메뉴 리스트
- 담당자 명단
', 
NULL, 4, 3);

-- 학생회 4의 기본 템플릿 (MT)
INSERT INTO `meeting_template` (`name`, `description`, `content_template`, `is_default`, `council_id`, `created_by`) VALUES
('MT', 'Membership Training을 위한 템플릿', 
'# MT 회의록

## 📅 회의 정보
- **일시**: {{meeting_datetime}}
- **장소**: {{location}}
- **참석자**: {{attendees}}

## 🎯 MT 목표
- 

## 📋 진행 내용
### 1. MT 계획 수립
- 

### 2. 예산 및 일정
- 

### 3. 프로그램 구성
- 

### 4. 담당자 배정
- 

## ✅ 결정사항
1. 
2. 
3. 

## 📝 특이사항
- ', 
NULL, 4, 3);
