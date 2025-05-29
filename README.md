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

## 🎯 프로젝트 개요

**Shaun the Sheep AI**는 사용자의 피부 상태를 체계적으로 분석하고 개인 맞춤형 스킨케어 솔루션을 제공하는 AI 기반 플랫폼입니다.

### 🌟 핵심 가치
- 🧬 **과학적 분석**: 수분, 유분, 민감도, 탄력 4가지 지표 기반 피부 분석
- 🎯 **개인화 추천**: 16가지 피부 MBTI 타입별 맞춤 제품 추천
- 🤖 **AI 상담**: Gemini AI 기반 실시간 스킨케어 상담
- 📊 **데이터 기반**: 사용자 데이터 축적을 통한 지속적인 추천 개선

## ✨ 주요 기능

### 🩺 피부 진단 시스템
- **4단계 체크리스트**: 수분(Moisture), 유분(Oil), 민감도(Sensitivity), 탄력(Tension) 분석
- **16가지 피부 MBTI**: MOST, MOSL, MBST 등 세분화된 피부 타입 분류
- **개인화 분석 리포트**: 피부 상태별 맞춤 케어 가이드 제공

### 🛍 스마트 추천 엔진
- **AI 기반 제품 추천**: 피부 타입과 고민에 맞는 스킨케어 제품 추천
- **루틴 관리**: 개인화된 아침/저녁 스킨케어 루틴 생성 및 관리
- **Naver API 연동**: 실시간 제품 이미지 및 정보 제공

### 💬 AI 상담 서비스
- **Gemini AI 통합**: Google Gemini 모델 기반 스킨케어 전문 상담
- **실시간 채팅**: 스킨케어 전문 AI 어시스턴트와 실시간 상담
- **대화 기록 저장**: 상담 내역 데이터베이스 저장 및 히스토리 관리
- **컨텍스트 인식**: 사용자의 피부 타입과 이전 상담 내역을 고려한 맞춤 답변




## 🛠 기술 스택

### Frontend
- **Framework**: Next.js 13.5.6 (App Router)
- **Language**: TypeScript 5.0
- **Styling**: CSS Modules + Custom CSS
- **UI Components**: Lucide React (아이콘)
- **AI Integration**: Vercel AI SDK 4.3.16
- **HTTP Client**: Axios 1.9.0
- **State Management**: React Hooks

### Backend
- **Framework**: Spring Boot 3.4.5
- **Language**: Java 17
- **Database**: PostgreSQL 16
- **ORM**: Spring Data JPA + Hibernate
- **Security**: Spring Security + JWT
- **Migration**: Flyway
- **Build Tool**: Gradle
- **External API**: Naver Search API

### AI & External Services
- **Primary AI**: Google Gemini
- **AI SDK**: Vercel AI SDK
- **Image Search**: Naver API

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
│   │   │   ├── 📁 ai-chat/      # AI 채팅 인터페이스
│   │   │   ├── 📁 checklist/    # 피부 체크리스트 진단
│   │   │   ├── 📁 recommend/    # 제품 추천 페이지
│   │   │   ├── 📁 routine-manage/ # 루틴 관리 시스템
│   │   │   ├── 📁 step2/        # 2단계 진단 프로세스
│   │   │   ├── 📁 register/     # 회원가입
│   │   │   ├── 📁 login/        # 로그인
│   │   │   ├── 📁 testDB-CRUD/  # 데이터베이스 테스트
│   │   │   └── 📁 api/          # API 라우트
│   │   ├── 📁 components/       # 재사용 컴포넌트
│   │   ├── 📁 config/          # API 설정
│   │   ├── 📁 data/            # 사용자 데이터 훅
│   │   └── 📁 types/           # TypeScript 타입 정의
│   ├── 📄 package.json
│   └── 📄 next.config.js
├── 📁 backend/                  # Spring Boot 백엔드
│   ├── 📁 src/main/java/org/mtvs/backend/
│   │   ├── 📁 auth/            # JWT 인증/인가
│   │   ├── 📁 user/            # 사용자 관리
│   │   ├── 📁 chat/            # AI 채팅 메시지 관리
│   │   ├── 📁 checklist/       # 피부 체크리스트 API
│   │   ├── 📁 product/         # 제품 정보 관리
│   │   ├── 📁 recommend/       # 기본 추천 시스템
│   │   ├── 📁 deeprecommend/   # 심화 추천 시스템
│   │   ├── 📁 routine/         # 루틴 관리 API
│   │   ├── 📁 naver/           # Naver API 연동
│   │   ├── 📁 board/           # 게시판 기능
│   │   └── 📁 global/          # 공통 설정 및 보안
│   ├── 📄 build.gradle
│   └── 📄 Dockerfile
├── 📄 docker-compose.yml       # PostgreSQL 로컬 환경
├── 📄 Jenkinsfile             # Jenkins CI/CD 파이프라인
├── 📄 vercel.json             # Vercel 배포 설정
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
git clone https://github.com/shaun-the-sheep-mtvs/shaun-the-sheep-ai.git
cd shaun-the-sheep-ai
```

### 2️⃣ 데이터베이스 설정

Docker Compose로 PostgreSQL 실행:

```bash
docker-compose up -d
```

데이터베이스 설정:
- **Database**: `sheep-ai-db`
- **Username**: `user`
- **Password**: `5656`
- **Port**: `5432`

### 3️⃣ 백엔드 실행

```bash
cd backend
./gradlew bootRun
```

백엔드 서버: `http://localhost:8080`

### 4️⃣ 프론트엔드 실행

```bash
cd frontend
npm install
npm run dev
```

프론트엔드 서버: `http://localhost:3000`

## ⚙️ 환경 설정

### Frontend 환경변수

`frontend/.env.local` 파일 생성:

```env
# Gemini API Key (필수)
GEMINI_API_KEY="your-gemini-api-key"

# 또는 OpenAI API Key (대체 옵션)
OPENAI_API_KEY="sk-..."
```

### Backend 환경변수

`backend/src/main/resources/application.yml`:

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

# Naver API 설정
naver:
  client-id: ${NAVER_CLIENT_ID}
  client-secret: ${NAVER_CLIENT_SECRET}

# JWT 설정
jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000
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

```bash
cd backend
./gradlew flywayMigrate
```

## 📊 API 문서

### 인증 API
```
POST /api/auth/login          # 로그인
POST /api/auth/register       # 회원가입
POST /api/auth/refresh        # 토큰 갱신
```

### 사용자 API
```
GET  /api/users/profile       # 프로필 조회
PUT  /api/users/profile       # 프로필 수정
```

### 체크리스트 API
```
POST /api/checklist/submit    # 체크리스트 제출
GET  /api/checklist           # 체크리스트 결과 조회
GET  /api/checklist/mbti      # MBTI 결과 조회
```

### 추천 API
```
POST /api/recommend/products  # 제품 추천
GET  /api/recommend/routines  # 루틴 추천
POST /api/deeprecommend       # 딥러닝 추천
```

### 채팅 API
```
POST /api/chat-messages/bulk  # 채팅 메시지 저장
GET  /api/chat-messages       # 채팅 기록 조회
```

### Naver API
```
GET  /api/naver/search        # 제품 이미지 검색
```

## 🚢 배포

### 프론트엔드 (Vercel)

1. Vercel에 GitHub 저장소 연결
2. 환경변수 설정 (AI API 키들)
3. 자동 배포 (main, dev 브랜치)

### 백엔드 (Docker + Jenkins)

```bash
# Docker 이미지 빌드
docker build -t shaun-backend ./backend

# 컨테이너 실행
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/sheep-ai-db \
  -e NAVER_CLIENT_ID=your_client_id \
  -e NAVER_CLIENT_SECRET=your_client_secret \
  -e JWT_SECRET=your_jwt_secret \
  shaun-backend
```

Jenkins 파이프라인을 통한 자동 배포 지원

## 🤝 기여하기

1. 저장소 Fork
2. 기능 브랜치 생성 (`git checkout -b feature/amazing-feature`)
3. 변경사항 커밋 (`git commit -m 'Add amazing feature'`)
4. 브랜치 Push (`git push origin feature/amazing-feature`)
5. Pull Request 생성

### 브랜치 전략

- `main` - 프로덕션 배포
- `dev` - 개발 통합
- `feature/*` - 새로운 기능
- `fix/*` - 버그 수정

## 📞 문의 및 지원

- **GitHub Issues**: 버그 리포트 및 기능 요청
- **GitHub Discussions**: 일반적인 질문 및 토론

---

<div align="center">

**Made with ❤️ by Shaun the Sheep AI Team**

[🌟 Star this repo](https://github.com/shaun-the-sheep-mtvs/shaun-the-sheep-ai) • [🐛 Report Bug](https://github.com/shaun-the-sheep-mtvs/shaun-the-sheep-ai/issues) • [💡 Request Feature](https://github.com/shaun-the-sheep-mtvs/shaun-the-sheep-ai/discussions)

</div>