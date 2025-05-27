# Shaun the Sheep AI
숀더쉽 스킨케어 AI 는 사용자가 스스로의 피부 상태를 파악하고 관리하는데 도움을 주는 서비스 입니다.
피부타입별 MBTI를 제공합니다.


이 프로젝트는 Next.js 프론트엔드, Java Spring Boot 백엔드, 그리고 Vercel AI SDK를 활용한 AI 챗 통합을 포함하는 모노레포입니다. PostgreSQL 데이터베이스에 채팅 메시지를 저장하고, OpenAI, Groq 등 다양한 AI 제공자와 연동할 수 있습니다.

---

## 프로젝트 구조

- `frontend/` — Next.js 앱 (AI 챗 UI, 게시판 CRUD)
- `backend/` — Java Spring Boot 앱 (API, 채팅 메시지 저장)
- `docker-compose.yml` — 로컬에서 PostgreSQL 실행용

---

## 로컬(Local)에서 사용 하기

### 1. 저장소 클론
```sh
git clone <your-repo-url>
cd shaun-the-sheep-ai
```

### 2. 데이터베이스 설정 (PostgreSQL)
Docker Compose로 데이터베이스를 실행하세요:
```sh
docker-compose up
```
이 명령어로 올바른 사용자, 비밀번호, 데이터베이스가 설정된 PostgreSQL 인스턴스가 실행됩니다.

---

## 프론트엔드 설정 (Next.js)

1. 디렉토리로 이동하세요.
2. 아래 명령어로 의존성을 설치하세요.
   ```sh
   npm install
   ```
3. `frontend/` 폴더 하위에 `.env.local` 파일을 만드세요. 그리고 아래 내용을 입력하세요.
   
   ```.env.local
   # OpenAI Key for GPT 4o mini
   OPENAI_API_KEY="sk-..."
   ```
   (키는 제이크에게 문의하세요.)

4. `frontend/` 하위에서 프론트엔드를 실행하세요:
   ```sh
   npm run dev
   ```


---

## AI 제공자 설정
- **OpenAI:** [API 키 받기](https://platform.openai.com/api-keys) (`gpt-4o-mini` 모델은 신규 계정에 한해 무료 제공) **반드시 새 계정으로 생성해야 합니다!**

---

## 채팅 메시지 저장
- 모든 사용자 및 AI 메시지는 `/api/chat-messages/bulk` 엔드포인트를 통해 백엔드 DB에 저장됩니다.
- DB에서 저장된 메시지를 확인하거나, 프론트엔드에서 대화 내역을 확장해 볼 수 있습니다.

---

## CORS
- 백엔드는 `http://localhost:3000`에서의 요청을 허용하도록 설정되어 있습니다.

---