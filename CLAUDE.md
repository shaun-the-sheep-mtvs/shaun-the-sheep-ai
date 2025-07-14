# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Frontend (Next.js in `/frontend/`)
```bash
cd frontend
npm install          # Install dependencies
npm run dev          # Start development server (localhost:3000)
npm run build        # Build production bundle
npm start            # Start production server
npm run lint         # Run ESLint
```

### Backend (Spring Boot in `/backend/`)
```bash
cd backend
./gradlew bootRun    # Start development server (localhost:8080)
./gradlew build      # Build JAR file
./gradlew test       # Run tests
./gradlew flywayMigrate  # Run database migrations
```

### Database (PostgreSQL via Docker)
```bash
docker-compose up -d  # Start PostgreSQL container
# Database: sheep-ai-db, User: user, Password: 5656, Port: 5432
```

## Architecture Overview

This is a **full-stack AI-powered skincare platform** with:

### Tech Stack
- **Frontend**: Next.js 13.5.6 (App Router) + TypeScript + CSS Modules
- **Backend**: Spring Boot 3.4.5 + Java 17 + PostgreSQL 16
- **AI Integration**: Google Gemini API via Vercel AI SDK
- **Authentication**: JWT with Spring Security
- **Deployment**: Frontend on Vercel, Backend on EC2 (Docker + Jenkins)

### Core Features
1. **Skin Analysis System**: 16 MBTI-style skin types based on 4 factors (Moisture, Oil, Sensitivity, Tension)
2. **AI Chat**: Gemini AI integration for skincare consultation
3. **Product Recommendations**: AI-powered skincare product suggestions
4. **Routine Management**: Personalized skincare routine creation and tracking
5. **User Authentication**: JWT-based auth with guest mode support

### Key Architectural Patterns

#### Frontend Structure (`/frontend/src/`)
- `app/` - Next.js App Router pages (each directory = route)
- `components/` - Reusable React components
- `config/api.ts` - API endpoint configuration
- `types/` - TypeScript type definitions
- `data/` - Data hooks and utilities

#### Backend Structure (`/backend/src/main/java/org/mtvs/backend/`)
- `auth/` - JWT authentication & authorization
- `user/` - User management and profile handling
- `chat/` - AI chat message storage and retrieval
- `checklist/` - Skin analysis checklist API
- `recommend/` & `deeprecommend/` - Product recommendation engines
- `routine/` - Skincare routine management
- `naver/` - External API integration (product images)
- `global/` - Security configuration and common utilities

#### Database Design
- PostgreSQL with JPA/Hibernate ORM
- Flyway for database migrations
- Connection pooling with HikariCP (max 2 connections)

## Key Configuration Files

### Environment Setup
- **Frontend**: Requires `GEMINI_API_KEY` in `.env.local`
- **Backend**: Database credentials and JWT secrets in `application.yml`
- **Database**: Configured via `docker-compose.yml` for local development

### Branch Strategy
- `main` - Production deployment (auto-deploys to Vercel + EC2)
- `dev` - Development integration branch  
- `feature/*` - New features
- Team of 6 developers, always merge to `dev` first for testing

## Development Workflow

### Team Process
1. Create feature branch from `dev`
2. Develop and test locally
3. Merge to `dev` branch first
4. Test feature in `dev` environment
5. Team leader merges `dev` to `main`
6. Auto-deployment triggers (Vercel + Jenkins/Docker)

### CI/CD Pipeline
- **Frontend**: Auto-deploys to Vercel on `main` and `dev` branches
- **Backend**: Jenkins pipeline builds Docker image and deploys to EC2
- **Database**: Local via Docker Compose, production on EC2

## Important Implementation Details

### AI Integration
- Primary AI service: Google Gemini API
- Frontend API route: `/frontend/src/app/api/chat/route.ts`
- Chat messages stored in PostgreSQL via backend
- Context-aware responses using user skin type data

### Authentication Flow
- JWT tokens with 10-hour expiration (access), 7-day (refresh), 30-min (guest)
- Spring Security configuration in `/backend/src/main/java/org/mtvs/backend/global/`
- Guest mode allows unauthenticated access to basic features

### Skin Analysis Logic
- 16 skin types: MOST, MOSL, MBST, MBSL, DOST, DOSL, DBST, DBSL, etc.
- 4-factor scoring system for skin characteristics
- Checklist-based diagnosis with personalized product recommendations

### API Architecture
All APIs follow RESTful design:
- `/api/auth/*` - Authentication endpoints
- `/api/checklist/*` - Skin analysis
- `/api/recommend/*` & `/api/deep/*` - Recommendations
- `/api/chat-messages/*` - Chat history
- `/api/naver/search` - External product search

## UI/UX Standards

### Color System
- Primary colors: Green (#3DD598), Blue (#4E9BFA), Red (#FF6B6B), Orange (#FF9F43)
- App themes: Pink (#FF88A9), Mint (#40DDBA)
- Consistent CSS custom properties in stylesheets

### Component Patterns
- All cards: white background, rounded corners (1.25rem), subtle shadows
- Buttons: gradient backgrounds, hover animations (translateY + shadow)
- Typography: Pretendard font, 17px base size, weight 700 for emphasis
- Responsive: Mobile-first design with 480px, 768px, 1024px breakpoints

## Testing Strategy
- Backend: JUnit tests via `./gradlew test`
- Frontend: Next.js built-in linting via `npm run lint`
- Integration: Test in `dev` branch before merging to `main`
- No specific test frameworks configured beyond defaults