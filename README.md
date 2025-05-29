# 🐑 Shaun the Sheep AI - 스킨케어 AI 플랫폼

> **AI 기반 개인화 스킨케어 솔루션**  
> 사용자의 피부 상태를 분석하고 맞춤형 케어 솔루션을 제공하는 지능형 스킨케어 플랫폼

[![Next.js](https://img.shields.io/badge/Next.js-13.5.6-black?logo=next.js)](https://nextjs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-green?logo=spring)](https://spring.io/projects/spring-boot)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.0-blue?logo=typescript)](https://www.typescriptlang.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql)](https://www.postgresql.org/)
[![Vercel](https://img.shields.io/badge/Deployed%20on-Vercel-black?logo=vercel)](https://vercel.com/)

## 📋 목차

- [🎯 프로젝트 개요](#-프로젝트-개요)
- [✨ 주요 기능](#-주요-기능)
- [🛠 기술 스택](#-기술-스택)
- [📁 프로젝트 구조](#-프로젝트-구조)
- [🚀 시작하기](#-시작하기)
- [⚙️ 환경 설정](#️-환경-설정)
- [🔧 개발 가이드](#-개발-가이드)
- [📊 API 문서](#-api-문서)
- [🚢 배포](#-배포)
- [🤝 기여하기](#-기여하기)
- [👨‍💻 팀 소개 & 기여자](#-팀-소개--기여자)

## 🎯 프로젝트 개요

**Shaun the Sheep AI**는 사용자가 스스로의 피부 상태를 파악하고 관리하는데 도움을 주는 AI 기반 스킨케어 서비스입니다.

### 핵심 가치
- 🧬 **개인화된 분석**: 피부타입별 MBTI 제공
- 🎯 **맞춤형 추천**: Gemini AI 기반 개인화 제품 추천
- 🤖 **지능형 상담**: Gemini AI 모델 기반 실시간 상담

## ✨ 주요 기능

### 🩺 피부 분석 & 진단
- **스킨 체크리스트**: 체계적인 피부 상태 진단
- **피부 타입 분석**: MBTI 스타일의 피부 유형 분류
- **개인화 리포트**: 상세한 피부 분석 결과 제공

### 🛍 스마트 추천 시스템
- **제품 추천**: AI 기반 개인 맞춤 스킨케어 제품 추천
- **루틴 관리**: 개인화된 스킨케어 루틴 생성 및 관리


### 💬 AI 상담 서비스
- **실시간 채팅**: Gemini AI 기반 스킨케어 상담
- **대화 기록**: 상담 내역 저장 및 관리


### 👥 커뮤니티 기능
- **게시판**: 사용자 간 정보 공유 및 소통
- **리뷰 시스템**: 제품 사용 후기 및 평가

## 🛠 기술 스택

### Frontend
- **Framework**: Next.js 13.5.6 (App Router)
- **Language**: TypeScript 5.0
- **Styling**: CSS Modules, Tailwind CSS
- **UI Components**: Lucide React, Recharts
- **AI Integration**: Vercel AI SDK
- **HTTP Client**: Axios

### Backend
- **Framework**: Spring Boot 3.4.5
- **Language**: Java 17
- **Database**: PostgreSQL 16
- **ORM**: Spring Data JPA + Hibernate
- **Security**: Spring Security + JWT
- **Migration**: Flyway
- **Build Tool**: Gradle

### AI & External Services
- **Primary AI**: Google Gemini
- **AI SDK**: Vercel AI SDK 4.3.16

### DevOps & Deployment
- **Frontend Deployment**: Vercel
- **Backend Deployment**: Docker + Jenkins
- **Database**: PostgreSQL (Docker Compose for local)
- **CI/CD**: Jenkins Pipeline
- **Version Control**: Git

## 📁 프로젝트 구조

```
shaun-the-sheep-ai/
├── 📁 frontend/                 # Next.js 프론트엔드
│   ├── 📁 src/
│   │   ├── 📁 app/              # App Router 페이지
│   │   │   ├── 📁 ai-chat/      # AI 채팅 페이지
│   │   │   ├── 📁 checklist/    # 피부 체크리스트
│   │   │   ├── 📁 recommend/    # 제품 추천
│   │   │   ├── 📁 routine-manage/ # 루틴 관리
│   │   │   ├── 📁 report/       # 분석 리포트
│   │   │   ├── 📁 register/     # 회원가입
│   │   │   ├── 📁 login/        # 로그인
│   │   │   └── 📁 api/          # API 라우트
│   │   ├── 📁 components/       # 재사용 컴포넌트
│   │   ├── 📁 types/           # TypeScript 타입 정의
│   │   ├── 📁 config/          # 설정 파일
│   │   └── 📁 data/            # 정적 데이터
│   ├── 📄 package.json
│   ├── 📄 next.config.js
│   └── 📄 tsconfig.json
├── 📁 backend/                  # Spring Boot 백엔드
│   ├── 📁 src/main/java/org/mtvs/backend/
│   │   ├── 📁 auth/            # 인증/인가
│   │   ├── 📁 user/            # 사용자 관리
│   │   ├── 📁 chat/            # 채팅 기능
│   │   ├── 📁 product/         # 제품 관리
│   │   ├── 📁 recommend/       # 추천 시스템
│   │   ├── 📁 routine/         # 루틴 관리
│   │   ├── 📁 checklist/       # 체크리스트
│   │   ├── 📁 board/           # 게시판
│   │   └── 📁 global/          # 공통 설정
│   ├── 📄 build.gradle
│   └── 📄 Dockerfile
├── 📄 docker-compose.yml       # PostgreSQL 로컬 환경
├── 📄 vercel.json             # Vercel 배포 설정
├── 📄 Jenkinsfile_*           # Jenkins CI/CD 파이프라인
└── 📄 README.md
```

## 🚀 시작하기

### 📋 사전 요구사항

- **Node.js** 18.0.0 이상
- **Java** 17 이상
- **Docker** & **Docker Compose**
- **PostgreSQL** 16 (또는 Docker 사용)

### 1️⃣ 저장소 클론

```bash
git clone https://github.com/your-username/shaun-the-sheep-ai.git
cd shaun-the-sheep-ai
```

### 2️⃣ 데이터베이스 설정

Docker Compose로 PostgreSQL 실행:

```bash
docker-compose up -d
```

이 명령어로 다음 설정의 PostgreSQL이 실행됩니다:
- **Database**: `sheep-ai-db`
- **Username**: `user`
- **Password**: `5656`
- **Port**: `5432`

### 3️⃣ 백엔드 실행

```bash
cd backend
./gradlew bootRun
```

백엔드 서버가 `http://localhost:8080`에서 실행됩니다.

### 4️⃣ 프론트엔드 실행

```bash
cd frontend
npm install
npm run dev
```

프론트엔드 서버가 `http://localhost:3000`에서 실행됩니다.

## ⚙️ 환경 설정

### Frontend 환경변수

`frontend/.env.local` 파일을 생성하고 다음 내용을 추가하세요:

```env
# OpenAI API Key (GPT-4o-mini)
OPENAI_API_KEY="sk-..."

# Groq API Key (선택사항)
GROQ_API_KEY="gsk_..."

# Anthropic API Key (선택사항)
ANTHROPIC_API_KEY="sk-ant-..."

# xAI API Key (선택사항)
XAI_API_KEY="xai-..."
```

### Backend 환경변수

`backend/src/main/resources/application.yml` 또는 환경변수로 설정:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/sheep-ai-db
    username: user
    password: 5656
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

## 🔧 개발 가이드

### 프론트엔드 개발

```bash
cd frontend

# 개발 서버 실행
npm run dev

# 빌드
npm run build

# 프로덕션 서버 실행
npm start

# 린팅
npm run lint
```

### 백엔드 개발

```bash
cd backend

# 개발 서버 실행
./gradlew bootRun

# 빌드
./gradlew build

# 테스트 실행
./gradlew test

# Docker 이미지 빌드
docker build -t shaun-backend .
```

### 데이터베이스 마이그레이션

Flyway를 사용한 데이터베이스 마이그레이션:

```bash
cd backend
./gradlew flywayMigrate
```

## 📊 API 문서

### 주요 엔드포인트

#### 인증
- `POST /api/auth/login` - 로그인
- `POST /api/auth/register` - 회원가입
- `POST /api/auth/refresh` - 토큰 갱신

#### 사용자
- `GET /api/users/profile` - 프로필 조회
- `PUT /api/users/profile` - 프로필 수정

#### 채팅
- `POST /api/chat-messages/bulk` - 채팅 메시지 저장
- `GET /api/chat-messages` - 채팅 기록 조회

#### 추천
- `POST /api/recommend/products` - 제품 추천
- `GET /api/recommend/routines` - 루틴 추천

#### 체크리스트
- `POST /api/checklist/submit` - 체크리스트 제출
- `GET /api/checklist/results` - 결과 조회

## 🚢 배포

### 프론트엔드 (Vercel)

1. Vercel에 프로젝트 연결
2. 환경변수 설정
3. 자동 배포 (main, dev 브랜치)

### 백엔드 (Docker + Jenkins)

```bash
# Docker 이미지 빌드
docker build -t shaun-backend ./backend

# 컨테이너 실행
docker run -p 8080:8080 shaun-backend
```

Jenkins 파이프라인:
- `Jenkinsfile_main` - 프로덕션 배포
- `Jenkinsfile_dev` - 개발 환경 배포

## 🤝 기여하기

1. 이 저장소를 Fork 합니다
2. 새로운 기능 브랜치를 생성합니다 (`git checkout -b feature/amazing-feature`)
3. 변경사항을 커밋합니다 (`git commit -m 'Add some amazing feature'`)
4. 브랜치에 Push 합니다 (`git push origin feature/amazing-feature`)
5. Pull Request를 생성합니다

### 브랜치 전략

- `main` - 프로덕션 배포 브랜치
- `dev` - 개발 브랜치
- `feature/*` - 새로운 기능 개발
- `fix/*` - 버그 수정
- `security` - 보안 관련 수정

## 📞 문의 및 지원

- **이슈 리포팅**: [GitHub Issues](https://github.com/your-username/shaun-the-sheep-ai/issues)
- **기능 요청**: [GitHub Discussions](https://github.com/your-username/shaun-the-sheep-ai/discussions)

---

<div align="center">

**Made with ❤️ by Shaun the Sheep AI Team**

[🌟 Star this repo](https://github.com/your-username/shaun-the-sheep-ai) • [🐛 Report Bug](https://github.com/your-username/shaun-the-sheep-ai/issues) • [💡 Request Feature](https://github.com/your-username/shaun-the-sheep-ai/issues)

</div>