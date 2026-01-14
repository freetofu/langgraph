# MagicGPTPlugin 빌드/설치 가이드

## 준비 사항

- CATIA Magic 설치 경로 지정
  - Windows: `MAGICDRAW_HOME` 환경 변수에 설치 경로를 지정하세요.
  - macOS/Linux: 동일하게 `MAGICDRAW_HOME` 사용

예시 (PowerShell):

```powershell
$env:MAGICDRAW_HOME = "C:\Program Files\Magic Systems of Systems Architect 2026x"
```

예시 (bash):

```bash
export MAGICDRAW_HOME="/Applications/Magic Systems of Systems Architect 2026x"
```

## API 키 설정

플러그인은 환경 변수로 API 키를 읽습니다.

- `MAGICGPT_API_KEY` : OpenAI API 키 (필수)
- `MAGICGPT_MODEL` : 모델 이름 (기본값: `gpt-4o-mini`)
- `MAGICGPT_API_URL` : API 엔드포인트 (기본값: `https://api.openai.com/v1/chat/completions`)
- `MAGICGPT_TIMEOUT_SECONDS` : 타임아웃(초) (기본값: 45)

## 빌드

### Windows (PowerShell)

```powershell
./build-plugin.ps1
```

### macOS/Linux (bash)

```bash
./build-plugin.sh
```

빌드가 완료되면 `dist/MagicGPTPlugin.zip`가 생성됩니다.

## 설치

1. `dist/MagicGPTPlugin.zip` 압축을 풉니다.
2. 생성된 `MagicGPTPlugin` 폴더를 CATIA Magic 설치 경로의 `plugins` 폴더에 복사합니다.
   - 예시: `C:\Program Files\Magic Systems of Systems Architect 2026x\plugins\MagicGPTPlugin`
3. CATIA Magic을 재시작합니다.

## 실행

- 메뉴에서 MagicGPT 패널을 열고 대화를 시작합니다.
- 네트워크 연결 및 API 키가 올바르면 응답이 출력됩니다.
