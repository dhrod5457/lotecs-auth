# lotecs-auth 운영환경 위험 요소 분석

## 1. 서킷브레이커/Fallback 미적용 (Critical)

현재 외부 시스템 호출 시 장애 대응이 전혀 없습니다.

### 1.1 위험 지점 목록

| 위치 | 외부 호출 | 현재 상태 | 위험도 |
|------|----------|----------|--------|
| `RelaySsoProvider` | Relay gRPC 서버 | 타임아웃만 설정 (5초), 장애 시 그대로 실패 | Critical |
| `KeycloakSsoProvider` | Keycloak REST API | 예외 발생 시 그대로 전파 | Critical |
| `LdapSsoProvider` | LDAP 서버 | 연결 실패 시 전체 로그인 불가 | Critical |
| `JwtAuthenticationService` | Redis 블랙리스트 조회 | Redis 장애 시 토큰 검증 불가 | High |
| `RateLimitFilter` | Redis Rate Limit 저장소 | Redis 장애 시 요청 처리 불가 | High |

### 1.2 장애 시나리오

**시나리오 1: SSO 서버 장애**
- Keycloak 또는 LDAP 서버 다운
- 해당 테넌트 전체 로그인 불가
- 에러 로그만 발생, 복구 메커니즘 없음

**시나리오 2: Redis 장애**
- JWT 블랙리스트 조회 실패
- 로그아웃한 토큰이 여전히 유효하게 처리될 가능성
- Rate Limit 동작 불가 -> DDoS 취약

**시나리오 3: Relay 서버 지연**
- gRPC 호출 5초 타임아웃
- 동시 로그인 요청 증가 시 스레드 풀 고갈
- 전체 서비스 응답 지연

## 2. 데이터베이스 연결 관련

### 2.1 현재 설정

```yaml
hikari:
  connection-timeout: 30000  # 30초
  maximum-pool-size: 20
```

### 2.2 위험 요소

- DB 장애 시 30초간 요청 대기 -> 스레드 고갈 가능
- 슬로우 쿼리 모니터링은 있으나 차단 메커니즘 없음
- 커넥션 풀 고갈 시 cascading failure 발생 가능

## 3. Redis 단일 장애점 (SPOF)

### 3.1 현재 설정

```yaml
spring.data.redis:
  host: 192.168.0.57
  port: 6379
  # Sentinel/Cluster 미설정
```

### 3.2 위험 요소

- 단일 노드 구성으로 SPOF 존재
- 장애 시 JWT 블랙리스트, Rate Limit 모두 동작 불가
- 자동 장애 복구(failover) 메커니즘 없음

## 4. 계정 잠금 로직 경쟁 조건

### 4.1 현재 코드

```java
// AuthService.java
user.recordLoginFailure();  // 실패 횟수 증가
userRepository.save(user);  // DB 저장
```

### 4.2 위험 요소

- 동시 로그인 시도 시 race condition 발생 가능
- 실패 횟수가 정확히 기록되지 않을 수 있음
- 분산 환경에서 동시성 제어 부재

## 5. 보안 설정 문제

### 5.1 현재 설정

```java
// SecurityConfig.java
.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
```

### 5.2 위험 요소

- 모든 엔드포인트 공개 (JWT 필터에 의존)
- Admin API (`/admin/**`)도 permitAll 상태
- Actuator 엔드포인트 노출 가능

## 6. 외부 호출 타임아웃 불일치

| 호출 대상 | 현재 타임아웃 | 권장 타임아웃 |
|----------|-------------|--------------|
| DB 쿼리 | 30초 | 5초 |
| Redis | 3초 | 1초 |
| Relay gRPC | 5초 | 3초 |
| Keycloak | 설정 없음 | 5초 |
| LDAP | 설정 없음 | 5초 |

## 7. 에러 처리 일관성 부재

### 7.1 현재 상황

- `IllegalArgumentException` - 인증 실패
- `UnsupportedOperationException` - 지원하지 않는 SSO 타입
- `IllegalStateException` - SSO 설정 부재
- gRPC는 별도 에러 처리

### 7.2 문제점

- 도메인별 커스텀 예외 클래스 부재
- 에러 코드 체계 미흡
- 클라이언트가 에러 유형 구분 어려움

## 8. 위험도별 정리

### Critical (즉시 조치 필요)

1. SSO Provider 서킷브레이커 미적용
2. Redis 장애 시 Fallback 처리 없음
3. Admin API 인증 필터 부재

### High (운영 전 조치 필요)

1. DB 커넥션 타임아웃 과도 (30초)
2. Redis 단일 노드 구성
3. 계정 잠금 동시성 제어 부재

### Medium (운영 중 개선)

1. Actuator 엔드포인트 보안
2. 에러 처리 체계화
3. 외부 호출 타임아웃 표준화
