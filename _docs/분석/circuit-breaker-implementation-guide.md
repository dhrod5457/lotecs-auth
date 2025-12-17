# 서킷브레이커 및 Fallback 구현 가이드

## 1. 개요

lotecs-auth 서비스에서 서킷브레이커와 Fallback 처리가 필요한 지점과 구현 방안을 정리합니다.

## 2. 서킷브레이커 적용 대상

### 2.1 SSO Provider 호출

| 클래스 | 메서드 | 폴백 전략 |
|--------|--------|----------|
| `RelaySsoProvider` | `authenticate()` | 에러 응답 + 알림 |
| `KeycloakSsoProvider` | `authenticate()` | 에러 응답 + 알림 |
| `LdapSsoProvider` | `authenticate()` | 에러 응답 + 알림 |

### 2.2 Redis 호출

| 기능 | 폴백 전략 |
|------|----------|
| JWT 블랙리스트 조회 | Fail-Open (기본값 false - 토큰 허용) |
| Rate Limit 체크 | Graceful Degradation (기본값 true - 요청 허용) |

## 3. 권장 설정

### 3.1 application.yml 추가 설정

```yaml
lotecs:
  circuit-breaker:
    enabled: true
    instances:
      relay-sso:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 60s
        sliding-window-size: 10
        permitted-number-of-calls-in-half-open-state: 3
        record-exceptions:
          - java.io.IOException
          - java.util.concurrent.TimeoutException
          - io.grpc.StatusRuntimeException
      keycloak-sso:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 60s
        sliding-window-size: 10
        permitted-number-of-calls-in-half-open-state: 3
        record-exceptions:
          - java.io.IOException
          - javax.ws.rs.ProcessingException
      ldap-sso:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 60s
        sliding-window-size: 10
        permitted-number-of-calls-in-half-open-state: 3
        record-exceptions:
          - org.apache.directory.api.ldap.model.exception.LdapException
      redis:
        failure-rate-threshold: 30
        wait-duration-in-open-state: 30s
        sliding-window-size: 20
        permitted-number-of-calls-in-half-open-state: 5

  fallback:
    enabled: true
    strategies:
      jwt-blacklist:
        type: fail-open
        default-value: false
        log-level: WARN
      rate-limit:
        type: graceful-degradation
        default-value: true
        log-level: WARN
```

## 4. 구현 예시

### 4.1 RelaySsoProvider 서킷브레이커 적용

```java
@Component("relay")
@RequiredArgsConstructor
public class RelaySsoProvider implements SsoProvider {

    private final RelayAuthClient relayAuthClient;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Override
    public SsoAuthResult authenticate(SsoAuthRequest request) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("relay-sso");

        return circuitBreaker.executeSupplier(() -> {
            // 실제 Relay 호출
            return doAuthenticate(request);
        });
    }

    private SsoAuthResult doAuthenticate(SsoAuthRequest request) {
        // 기존 구현
    }
}
```

### 4.2 어노테이션 기반 적용 (lotecs-circuit-breaker 모듈 사용 시)

```java
@Component("relay")
@RequiredArgsConstructor
public class RelaySsoProvider implements SsoProvider {

    private final RelayAuthClient relayAuthClient;

    @Override
    @CircuitBreaker(name = "relay-sso", fallbackMethod = "fallbackAuthenticate")
    public SsoAuthResult authenticate(SsoAuthRequest request) {
        return doAuthenticate(request);
    }

    private SsoAuthResult fallbackAuthenticate(SsoAuthRequest request, Exception e) {
        log.warn("[SSO] Relay SSO 폴백 발생: {}", e.getMessage());
        throw new SsoUnavailableException("SSO 서버 연결 실패. 잠시 후 다시 시도해주세요.");
    }
}
```

### 4.3 Redis Fallback 구현

```java
@Service
public class JwtBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public boolean isBlacklisted(String token) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("redis");

        try {
            return circuitBreaker.executeSupplier(() ->
                Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token))
            );
        } catch (Exception e) {
            // Fail-Open: Redis 장애 시 토큰 허용
            log.warn("[JWT] Redis 블랙리스트 조회 실패, Fail-Open 적용: {}", e.getMessage());
            return false;
        }
    }
}
```

## 5. Fallback 전략 상세

### 5.1 전략 유형

| 전략 | 설명 | 적용 대상 |
|------|------|----------|
| Fail-Open | 장애 시 기본값으로 허용 | JWT 블랙리스트 |
| Fail-Close | 장애 시 기본값으로 거부 | 보안 중요 검증 |
| Graceful Degradation | 장애 시 기능 비활성화 | Rate Limit |
| Alternative Path | 장애 시 대체 경로 사용 | 하이브리드 인증 |

### 5.2 SSO 폴백 시나리오

**시나리오 1: 단순 에러 응답**
```java
private SsoAuthResult fallbackAuthenticate(SsoAuthRequest request, Exception e) {
    throw new SsoUnavailableException("SSO_UNAVAILABLE",
        "SSO 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.");
}
```

**시나리오 2: INTERNAL로 폴백 (하이브리드 인증)**
```java
private SsoAuthResult fallbackAuthenticate(SsoAuthRequest request, Exception e) {
    log.warn("[SSO] 외부 SSO 실패, INTERNAL 인증 시도: {}", e.getMessage());

    // 외부 사용자 매핑에서 내부 사용자 찾기
    ExternalUserMapping mapping = externalUserMappingRepository
        .findByExternalUserId(request.getUsername())
        .orElse(null);

    if (mapping != null && mapping.getInternalPassword() != null) {
        // 내부 비밀번호로 인증 시도
        return authenticateInternal(request);
    }

    throw new SsoUnavailableException("SSO_UNAVAILABLE_NO_FALLBACK",
        "SSO 서버에 연결할 수 없으며, 대체 인증 방법이 없습니다.");
}
```

## 6. 모니터링 및 알림

### 6.1 서킷브레이커 상태 모니터링

```java
@Component
@RequiredArgsConstructor
public class CircuitBreakerEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void onStateTransition(CircuitBreakerOnStateTransitionEvent event) {
        String circuitBreakerName = event.getCircuitBreakerName();
        CircuitBreaker.StateTransition transition = event.getStateTransition();

        log.warn("[CircuitBreaker] {} 상태 변경: {} -> {}",
            circuitBreakerName,
            transition.getFromState(),
            transition.getToState());

        if (transition.getToState() == CircuitBreaker.State.OPEN) {
            notificationService.sendAlert(
                String.format("[ALERT] %s 서킷브레이커 OPEN", circuitBreakerName)
            );
        }
    }
}
```

### 6.2 메트릭 수집

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,circuitbreakers
  health:
    circuitbreakers:
      enabled: true
```

## 7. 구현 우선순위

| 순위 | 대상 | 이유 |
|------|------|------|
| 1 | RelaySsoProvider | gRPC 호출, 네트워크 장애 빈번 |
| 2 | Redis 블랙리스트 | 모든 토큰 검증에 사용 |
| 3 | KeycloakSsoProvider | REST API 호출 |
| 4 | LdapSsoProvider | 레거시 시스템 연동 |
| 5 | Redis Rate Limit | 부가 기능 |

## 8. 테스트 전략

### 8.1 서킷브레이커 테스트

```java
@Test
void shouldOpenCircuitAfterFailures() {
    // Given
    CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("relay-sso");

    // When: 10번 실패 발생
    for (int i = 0; i < 10; i++) {
        try {
            circuitBreaker.executeRunnable(() -> {
                throw new RuntimeException("Connection failed");
            });
        } catch (Exception ignored) {}
    }

    // Then: 서킷 OPEN 상태
    assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
}
```

### 8.2 Fallback 테스트

```java
@Test
void shouldFallbackWhenRedisUnavailable() {
    // Given
    when(redisTemplate.hasKey(anyString())).thenThrow(new RedisConnectionException("Connection refused"));

    // When
    boolean result = jwtBlacklistService.isBlacklisted("test-token");

    // Then: Fail-Open으로 false 반환
    assertThat(result).isFalse();
}
```
