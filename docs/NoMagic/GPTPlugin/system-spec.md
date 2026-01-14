# CATIA Magic + GPT 연계 플러그인 시스템 사양서(초안)

본 문서는 CATIA Magic 환경에서 GPT를 연계하는 플러그인의 **시스템 사양서 초안**이다. 기존 시스템 컨텍스트, 사용자 요구사항/시나리오, 서브시스템 분석 및 보완 체크리스트를 바탕으로 작성한다.

## 1. 목적 및 범위
- **목적**: CATIA Magic 모델링 작업에 GPT를 연계하여 요구사항 도출, 모델 생성, 추적성, V&V, 규격 준수 등을 지원한다.
- **범위**: 플러그인 UI, 모델 컨텍스트 수집, GPT API 연동, 제안 검토/적용, 감사/보안 로그.

## 2. 관련 문서
- 시스템 컨텍스트: `system-context.md`
- 사용자 요구사항: `user-requirements.md`
- 사용자 시나리오: `user-scenarios.md`
- 서브시스템 분석: `subsystems.md`
- 사양서 작성 전 보완사항: `spec-prep.md`

## 3. 시스템 경계 및 액터
- **내부**: CATIA Magic, MagicGPTPlugin
- **외부**: 사용자, GPT API
- **주요 액터**: 시스템 엔지니어, 아키텍트, 검증 담당자

## 4. 서브시스템 구성
- **UI/Interaction**: 사용자 입력/출력, 요약/상세 전환
- **Context Builder**: 모델 컨텍스트 수집/요약
- **GPT Orchestrator**: GPT 요청/응답 처리
- **Proposal/Review**: 제안 검토/미리보기
- **Model Apply**: 모델 변경 적용
- **Audit & Security**: 로그/마스킹/키 관리

## 5. 기능 요구사항
- **FR-01** 요구사항 도출 및 정제 제안 제공
- **FR-02** 모델 요소/관계 초안 생성
- **FR-03** 요구사항-모델 추적성 링크 제안
- **FR-04** 모델 품질 점검 체크리스트 제공
- **FR-05** V&V 계획/항목 제안
- **FR-06** 세션 컨텍스트 유지
- **FR-07** SE 단계별 가이드 제공
- **FR-08** 규격 준수(ISO 26262/21434 등) 체크 지원
- **FR-09** 반복 작업 자동화 아이디어 제안
- **FR-10** 패널 기반 즉시 호출 및 요약/상세 응답 제공

## 6. 인터페이스 및 데이터 스키마

### 6.1 UI → Context Builder (입력 스키마)
```json
{
  "request_id": "uuid",
  "user_id": "string",
  "query": "string",
  "phase": "requirements|architecture|vnv|operations|other",
  "scope": {
    "mode": "all|selection|package",
    "element_ids": ["id1", "id2"],
    "package_ids": ["pkg1"]
  },
  "response_mode": "summary|detailed"
}
```
- **검증 규칙**: `scope.mode=selection`이면 `element_ids` 필수, `scope.mode=package`이면 `package_ids` 필수.

### 6.2 Context Builder → GPT Orchestrator (컨텍스트 스키마)
```json
{
  "request_id": "uuid",
  "intent": "generate|review|trace|vnv|compliance|automation",
  "context": {
    "elements": [
      {"id": "E-1", "name": "ControlUnit", "type": "Block", "attrs": {"stereotype": "HW"}}
    ],
    "relations": [
      {"type": "composition", "source": "E-1", "target": "E-2"}
    ],
    "requirements": [
      {"id": "REQ-1", "text": "The system shall ...", "priority": "high"}
    ],
    "recent_changes": [
      {"id": "E-3", "change": "updated", "at": "2026-01-13T13:00:00Z"}
    ]
  },
  "constraints": {
    "token_budget": 6000,
    "masking_level": "high|medium|low"
  }
}
```
- **검증 규칙**: `recent_changes`는 선택, 토큰 초과 시 컨텍스트 축약 정책 적용.

### 6.3 GPT 응답 구조(제안 데이터)
```json
{
  "request_id": "uuid",
  "summary": "string",
  "proposals": {
    "requirements": [{"id": "REQ-NEW-1", "text": "..." }],
    "model_elements": [{"action": "create|update", "type": "Block", "name": "..."}],
    "trace_links": [{"requirement_id": "REQ-1", "element_id": "E-1"}],
    "checklists": [{"category": "V&V", "items": ["..."]}]
  }
}
```
- **검증 규칙**: 구조화 JSON 파싱 실패 시 원문 표시 + 1회 재요청.

## 7. 운영 흐름(워크플로우)
1. 사용자 질의 입력
2. 컨텍스트 수집/요약
3. GPT 요청/응답 수신
4. 제안 미리보기
5. **일괄 승인**
6. 모델 반영
7. 결과 리포트 및 감사 로그 기록

## 8. 보안/감사 로그
- **로그 범위**: 요청 메타데이터, 응답 요약, 적용 결과
- **마스킹 규칙**: 개인정보/보안 등급 텍스트 마스킹
- **키 관리**: API 키는 환경 변수/보안 저장소 사용

## 9. 성능 및 안정성
- **응답 시간 목표**: 평균 5초, 최대 15초
- **타임아웃**: API 호출 20초
- **오류 처리**: 네트워크 오류 2회 재시도, 파싱 오류 1회 재요청

## 10. 사용자 경험(UX)
- 요약/상세 전환 토글 제공
- 적용 성공/실패 메시지와 오류 이유 표시
- 모델링 화면 이탈 최소화(패널 내 작업 완료)

## 11. 테스트/검증
- **단위 테스트**: 컨텍스트 요약, 파싱, 변경 세트 생성
- **통합 테스트**: API 호출 + 모델 반영 + 롤백
- **보안 테스트**: 마스킹 적용, 로그 누락 여부

## 12. 미결 사항
- API 요청/응답 실제 포맷 확정
- 컨텍스트 축약 알고리즘 상세 정의
- 프로젝트별 규칙 반영 범위 확정
