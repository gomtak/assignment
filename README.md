# WireBarley Bank API

Spring Boot 기반의 단순 계좌 시스템으로, 계좌 생성/삭제, 입금, 출금, 이체, 거래 내역 조회 기능을 제공합니다.

## 기술 스택

- Java 21
- Spring Boot 3.4.5
- Spring Data JPA
- H2 / MySQL
- Redis
- QueryDSL
- Docker Compose

## 모듈 구조

- `api`: 메인 애플리케이션 (웹 및 서비스 계층)
- `support:main-db`: JPA 및 DB 관련 설정
- `support:cache`: Redis 락 및 캐시 관련 구현

## 실행 방법 (Docker Compose)

1. 프로젝트 루트에서 실행:

```bash
./gradlew build
docker-compose up --build ## mysql, redis 연결 문제 시 재실행
```
- swagger : http://localhost:8080/swagger-ui/index.html
