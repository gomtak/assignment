# 📘 ProtoPie

## 🛠 프로젝트 설정 및 실행 방법

### 빌드 && Docker Compose 실행

```bash
./gradlew build
docker-compose up --build
```

### Swagger 접속 경로

* Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* OpenAPI Docs: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### 환경 변수 설정 (기본값 있음)

```yaml
DB_HOST=localhost
KAFKA_HOST=localhost
```

---

## 🧰 사용 기술 스택 및 라이브러리

* **언어**: Kotlin (JVM 21)
* **프레임워크**: Spring Boot 3.4.5
* **보안**: Spring Security + JWT
* **데이터베이스**: PostgreSQL
* **메시징 시스템**: Kafka (Zookeeper 기반)
* **API 문서화**: SpringDoc OpenAPI 3 (Swagger)
* **테스트**: JUnit 5 + MockK + Testcontainers

---

## 🧱 설계 결정 이유

### 아키텍처 구조

* 헥사고날 아키텍처 (Hexagonal Architecture)

  * 도메인, 애플리케이션, 어댑터 계층을 명확히 분리
  * 테스트 용이성과 유지보수성을 확보

### 인증/인가

* **JWT 기반 인증**: Stateless 구조로 확장성 확보
* **커스텀 AuthenticationToken + @AuthenticationPrincipal**로 현재 사용자 정보 접근
* **Admin / Member 권한 분기**는 서비스 단에서 명확하게 검증

### 데이터베이스 설계

* User 테이블의 PK는 `UUID`
  * JWT 기반 인증에서 사용자 ID가 토큰 payload에 포함되므로, 추측이 어려운 UUID를 사용하여 보안을 강화했습니다.
* Email은 Unique 제약 적용

---

## 🧩 문제 해결 과정 및 고민

### 1. Kafka 병렬 소비를 위한 파티션 확장

* 사용자 탈퇴 시 발생하는 여러 후속 작업(예: 메일 전송, 리소스 정리 등)을 병렬로 처리할 수 있도록 user.deleted 토픽에 예상되는 컨슈머 수만큼 파티션 수를 증가.
* 병렬 처리 성능을 향상

---

## 🚀 비동기 처리 설명

### 목적

* 사용자가 탈퇴할 때 리소스 정리 및 알림 메일 전송을 **서비스와 분리된 컨슈머에서 비동기 처리**

### 구조

* Kafka로 `UserDeletedEvent` publish
* 2개의 Consumer 그룹으로 분기 처리:

  * `mail-service`: 이메일 전송
  * `user-service`: 리소스 정리 로그 출력 (파일 삭제 등 확장 고려)

### 기술 스택

* KafkaTemplate
* `@TransactionalEventListener(phase = AFTER_COMMIT)`
* Consumer 간 Group 분리 (fan-out)

---

## 🧪 테스트 전략

### 단위 테스트

* `SignUpService`, `SignInService`, `UserService` 등에 대해 `MockK` 기반 테스트 작성

### 통합 테스트

* SpringBootTest + Testcontainers 기반으로 PostgreSQL/Kafka 환경 구성
* 실제 HTTP 요청 + JWT 인증 시나리오 검증
* Kafka 메시지 publish 여부 검증

### 주요 시나리오

* 회원가입 성공 / 이메일 중복 실패
* 로그인 성공 / 비밀번호 불일치 실패
* 사용자 본인/관리자만 정보 조회 가능
* 사용자 탈퇴 시 Kafka 이벤트 발행
