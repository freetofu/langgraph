# 시스템 사양서 작성 전 보완사항 초안

본 문서는 시스템 컨텍스트/시나리오/서브시스템 분석을 바탕으로 **사양서 작성 전에 보완해야 할 항목**을 초안 형태로 정리한 것이다.

## 1. 인터페이스/데이터 스키마 보완

### 1.1 UI → Context Builder 입력 스키마(초안)
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
**기준**: `scope.mode=selection`이면 `element_ids` 필수, `scope.mode=package`이면 `package_ids` 필수.

### 1.2 Context Builder → GPT Orchestrator 스키마(초안)
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
**기준**: `recent_changes`는 옵션이며, `token_budget` 초과 시 컨텍스트 축약 규칙 적용.

### 1.3 GPT 응답 구조(제안 데이터, 초안)
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
**기준**: 제안은 구조화 JSON으로 수신하며, 파싱 실패 시 사용자에게 원문 표시 + 재요청.

## 2. GPT API 요청/응답 포맷 정의
- **요청 포맷**: `system` 프롬프트 + `user` 요청 + `context` JSON 결합.
- **요약 정책**: 요소 수가 임계치 초과 시 `requirements → relations → elements` 순서로 축약.
- **오류 정책**: 네트워크 오류 2회 재시도, 파싱 오류 1회 재요청.
- **응답 규칙**: 구조화 JSON 필수, 실패 시 사용자 승인으로 텍스트 수용 가능.

## 3. 컨텍스트 수집 범위 및 정책
- **범위 기본값**: 선택 요소 → 관련 관계 1-hop → 상위 패키지 정보.
- **규칙 반영**: 프로젝트 네이밍/스테레오타입 규칙 포함.
- **민감 데이터**: 개인정보/보안 등급 표식 요소는 마스킹 후 전송.

## 4. 승인/적용 워크플로우
- **단계**: 제안 생성 → 미리보기 → **일괄 승인** → 적용 → 결과 리포트.
- **승인 단위**: 일괄 승인만 지원(부분 승인 미지원).
- **롤백 기준**: 적용 실패 시 전체 변경 세트 롤백.

## 5. 보안/감사 로그 정책
- **로그 범위**: 요청 메타데이터, 응답 요약, 적용 결과.
- **마스킹 규칙**: 사용자 식별 정보/보안 등급 텍스트 마스킹.
- **키 관리**: 로컬 저장 금지, 환경 변수/보안 저장소 사용.

## 6. 성능/안정성 기준
- **응답 시간**: 평균 5초, 최대 15초 이내 목표.
- **장애 대응**: 오프라인 시 로컬 캐시 안내 메시지 표시.
- **타임아웃**: API 호출 20초, 초과 시 사용자 재시도 안내.

## 7. 사용자 경험(UX) 상세
- **요약/상세 전환**: 동일 응답에 대해 토글 제공.
- **피드백**: 적용 성공/실패 메시지 + 오류 이유 표시.
- **작업 흐름**: 모델링 화면 이탈 최소화(패널 내 완료).

## 8. 테스트 및 검증 계획
- **단위 테스트**: 컨텍스트 요약, 응답 파싱, 변경 세트 생성.
- **통합 테스트**: API 호출 + 모델 반영 + 롤백.
- **보안 테스트**: 마스킹 적용, 로그 누락 여부 점검.
