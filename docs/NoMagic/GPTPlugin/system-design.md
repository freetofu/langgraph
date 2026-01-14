# MagicGPT 플러그인 시스템 설계서(초안)

본 문서는 시스템 사양서(`system-spec.md`)를 기반으로 MagicGPT 플러그인의 **구현 관점 설계**를 정의한다.

## 1. 설계 목표
- 사양서의 기능/비기능 요구사항을 모듈 구조와 데이터 흐름으로 구체화한다.
- CATIA Magic 내부 모델 API와 GPT API 간 연결을 안정적으로 관리한다.
- 일괄 승인 워크플로우를 기본으로 안전한 모델 적용을 보장한다.

## 2. 전체 아키텍처 개요
- **UI Layer**: 사용자 입력/응답 렌더링
- **Service Layer**: 컨텍스트 수집, GPT 요청, 제안 관리
- **Integration Layer**: CATIA Magic 모델 API 및 GPT API 연동
- **Security/Audit Layer**: 마스킹, 로깅, 키 관리

## 3. 서브시스템 구현 매핑

### 3.1 UI/Interaction
- UI 패널: 질의 입력, 요약/상세 보기, 일괄 승인 버튼
- 상태 표시: 로딩/오류/적용 결과

### 3.2 Context Builder
- 모델 요소/관계/요구사항 추출 모듈
- 토큰 예산 기반 요약 모듈

### 3.3 GPT Orchestrator
- 프롬프트 템플릿 관리
- API 요청/응답 핸들러
- 재시도/타임아웃 로직

### 3.4 Proposal/Review
- 응답 파서(JSON 구조화)
- 제안 리스트 뷰 모델
- 일괄 승인 상태 관리

### 3.5 Model Apply
- 변경 세트 생성기
- 적용 실행기(트랜잭션 기반)
- 롤백 핸들러

### 3.6 Audit & Security
- 마스킹 필터
- 요청/응답 메타 로그 기록
- 키 관리(환경 변수/보안 저장소 연동)

## 4. 핵심 데이터 흐름 설계

1. **User Query 입력** → UI에서 `request_id` 생성
2. **Context Builder**가 모델 데이터 수집 및 요약
3. **GPT Orchestrator**가 GPT API 호출
4. **Proposal/Review**가 제안 목록 생성 및 UI 렌더링
5. 사용자가 **일괄 승인** → Model Apply 적용
6. 적용 결과를 UI와 Audit Log에 기록

## 5. 인터페이스 설계

### 5.1 내부 API (모듈 간 호출)
- `ContextService.buildContext(request)` → `ContextPayload`
- `GptService.ask(contextPayload)` → `GptResponse`
- `ProposalService.buildProposals(gptResponse)` → `ProposalSet`
- `ApplyService.applyAll(proposalSet)` → `ApplyResult`

### 5.2 외부 API
- GPT API: JSON 기반 request/response
- CATIA Magic API: 모델 요소/관계 CRUD

## 6. 승인/적용 설계
- 승인 단위는 **일괄 승인**만 지원
- 적용 실패 시 전체 롤백
- 적용 후 결과 리포트 생성

## 7. 보안/감사 설계
- 모델 컨텍스트 전송 전 마스킹 필수
- 감사 로그: 요청 메타데이터, 응답 요약, 적용 결과 저장
- API 키는 로컬 파일 저장 금지

## 8. 성능/안정성 설계
- API 호출 타임아웃 20초
- 네트워크 오류 2회 재시도
- 파싱 오류 1회 재요청
- UI 비동기 처리로 모델링 작업 중단 최소화

## 9. 구현 순서(권장)
1. UI + Context Builder 기본 흐름 구축
2. GPT API 연동
3. Proposal/Review 및 일괄 승인 UI
4. Model Apply + 롤백
5. Audit/보안 기능 추가

