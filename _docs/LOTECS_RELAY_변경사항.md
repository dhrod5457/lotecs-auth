# LOTECS Relay 변경사항

## 개요

lotecs-auth 서비스의 Hybrid SSO 아키텍처를 지원하기 위한 lotecs-relay 변경사항을 정의합니다.

학교 테넌트의 인증 요청은 lotecs-auth에서 lotecs-relay로 gRPC 호출을 통해 위임됩니다.

## 아키텍처 흐름

```
클라이언트 → lotecs-auth (로그인 요청, tenant_id=SEJONG)
              ↓ (SSO 타입 확인: RELAY)
              ↓ (gRPC 호출)
           lotecs-relay (학교별 인스턴스)
              ↓ (SSO Provider 실행: CAS/LDAP/etc)
           외부 SSO 시스템 (학교 인증 서버)
              ↓ (인증 결과)
           lotecs-relay → lotecs-auth
              ↓ (사용자 동기화 + JWT 생성)
              ↓
           클라이언트 (JWT 토큰 반환)
```

## 변경 범위

### 1. 변경 불필요 항목

lotecs-relay는 이미 다음 기능을 제공하고 있어 **추가 변경이 필요하지 않습니다**:

- gRPC 인증 API (`auth.proto`)
- `AuthGrpcService` 구현
- SSO Provider 구현체 (CAS, RestToken, JWT, HttpForm)
- 로그인 시도 추적 (LoginAttemptTracker)
- JWT 토큰 생성

### 2. 검토 필요 항목

다음 항목들은 현재 구현을 검토하고 필요 시 확장합니다:

| 항목 | 현재 상태 | 필요 여부 | 비고 |
|------|----------|----------|------|
| gRPC API UserInfo 필드 | user_id, user_name, user_type, department, roles | 검토 필요 | email, phone 필드 추가 고려 |
| tenant_id 전달 방식 | 환경변수 (`TENANT_ID`) | 유지 | lotecs-relay는 단일 테넌트 |
| gRPC 엔드포인트 설정 | 고정 포트 (8180) | 설정 추가 | lotecs-auth 접근 허용 |
| 응답 형식 | LoginResponse | 유지 | 현재 형식 적합 |

## 상세 변경사항

### 1. gRPC API 필드 확장 (선택사항)

**현재 UserInfo 구조** (`relay-core/src/main/proto/auth.proto:43`):

```protobuf
message UserInfo {
  string user_id = 1;
  string user_name = 2;
  string user_type = 3;       // STUDENT, PROFESSOR, STAFF
  string department = 4;
  repeated string roles = 5;
}
```

**lotecs-auth 요구사항**:
- email, phone 필드가 필요할 수 있음
- external_user_id 매핑을 위한 원본 ID

**변경 옵션**:

#### 옵션 A: UserInfo 확장 (권장)

```protobuf
message UserInfo {
  string user_id = 1;
  string user_name = 2;
  string user_type = 3;
  string department = 4;
  repeated string roles = 5;
  string email = 6;           // 추가
  string phone = 7;           // 추가
  string external_id = 8;     // 추가: 학번/직번 등 원본 ID
}
```

**변경 파일**:
- `relay-core/src/main/proto/auth.proto`
- `relay-module-auth/src/main/java/lotecs/relay/module/auth/AuthUserInfo.java`
- `relay-module-auth/src/main/java/lotecs/relay/module/auth/AuthGrpcService.java` (toProtoUserInfo 메서드)

**AuthUserInfo 확장**:

```java
// relay-module-auth/src/main/java/lotecs/relay/module/auth/AuthUserInfo.java
public record AuthUserInfo(
    String userId,
    String userName,
    String userType,
    String department,
    List<String> roles,
    String email,        // 추가
    String phone,        // 추가
    String externalId    // 추가
) {
    // 기존 생성자는 유지 (하위 호환성)
    public AuthUserInfo(String userId, String userName, String userType,
                        String department, List<String> roles) {
        this(userId, userName, userType, department, roles, null, null, null);
    }
}
```

**AuthGrpcService 수정**:

```java
// relay-module-auth/src/main/java/lotecs/relay/module/auth/AuthGrpcService.java:186
private UserInfo toProtoUserInfo(AuthUserInfo userInfo) {
    UserInfo.Builder builder = UserInfo.newBuilder()
            .setUserId(userInfo.userId())
            .setUserName(userInfo.userName() != null ? userInfo.userName() : "")
            .setUserType(userInfo.userType() != null ? userInfo.userType() : "")
            .setDepartment(userInfo.department() != null ? userInfo.department() : "")
            .addAllRoles(userInfo.roles() != null ? userInfo.roles() : java.util.Collections.emptyList());

    // 추가 필드 (nullable)
    if (userInfo.email() != null) {
        builder.setEmail(userInfo.email());
    }
    if (userInfo.phone() != null) {
        builder.setPhone(userInfo.phone());
    }
    if (userInfo.externalId() != null) {
        builder.setExternalId(userInfo.externalId());
    }

    return builder.build();
}
```

#### 옵션 B: 현재 구조 유지 (간단)

현재 구조를 유지하고, lotecs-auth에서 추가 정보가 필요한 경우 별도 API 호출로 조회.

**권장**: 옵션 A (UserInfo 확장) - 네트워크 호출 최소화

### 2. Tenant별 AuthProvider 구현 확인

각 테넌트 모듈이 `TenantAuthProvider` 인터페이스를 올바르게 구현하는지 확인합니다.

**확인 대상**:
- `relay-tenant-sejong`: SejongAuthProvider
- `relay-tenant-sample`: SampleAuthProvider

**필수 구현 메서드**:
```java
public interface TenantAuthProvider {
    AuthResult authenticate(String userId, String password, String ipAddress);
    AuthUserInfo getUserInfo(String userId);
}
```

**email, phone 필드 제공 여부 확인**:
- 세종대 View/테이블에서 email, phone 조회 가능 여부
- 불가능하면 lotecs-auth에서 해당 필드를 optional로 처리

### 3. gRPC 서버 설정

lotecs-relay의 gRPC 서버가 lotecs-auth의 요청을 받을 수 있도록 설정을 확인합니다.

**현재 설정** (`relay-service/src/main/resources/application.yml`):

```yaml
grpc:
  server:
    port: 8180
```

**변경 불필요** - 현재 설정 유지.

lotecs-auth에서는 테넌트별로 `relay_endpoint` 설정으로 접근합니다:

```yaml
# lotecs-auth의 auth_tenant_sso_config 테이블
relay_endpoint: "192.168.0.57:8180"  # 세종대 relay 서버
```

### 4. 네트워크 및 방화벽 설정

**lotecs-auth → lotecs-relay gRPC 통신 허용**:

```bash
# lotecs-relay 서버에서 방화벽 규칙 추가
# lotecs-auth 서버 IP: 예시 192.168.0.58
sudo firewall-cmd --permanent --add-rich-rule='rule family="ipv4" source address="192.168.0.58/32" port protocol="tcp" port="8180" accept'
sudo firewall-cmd --reload
```

또는 Docker Compose 네트워크 설정:

```yaml
# lotecs-relay의 docker-compose.yml
services:
  relay-service:
    networks:
      - lotecs-network

networks:
  lotecs-network:
    external: true
```

### 5. 설정 파일 확인

**relay-service/src/main/resources/application.yml**:

```yaml
# 기존 설정 유지
lotecs:
  tenant:
    id: ${TENANT_ID:SEJONG}
  jwt:
    secret: ${JWT_SECRET_KEY}  # lotecs-auth와 동일한 키 사용 (중요!)
    access-token-validity-seconds: 3600
    refresh-token-validity-seconds: 604800

grpc:
  server:
    port: 8180

relay:
  auth:
    enabled: true
    exclude-paths:
      - /grpc.health.v1.Health/*
```

**중요**: `JWT_SECRET_KEY`는 lotecs-auth와 동일해야 합니다.

### 6. 로깅 강화

lotecs-auth 호출을 추적하기 위한 로깅 추가:

**AuthGrpcService 로깅 강화**:

```java
// relay-module-auth/src/main/java/lotecs/relay/module/auth/AuthGrpcService.java:29
@Override
public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
    String clientInfo = extractClientInfo();  // gRPC metadata에서 client 정보 추출
    log.info("Login request from lotecs-auth - user: {}, client: {}",
             request.getUserId(), clientInfo);

    // ... 기존 로직

    if (!authResult.success()) {
        log.warn("Authentication failed from lotecs-auth - user: {}, reason: {}, client: {}",
                 request.getUserId(), authResult.errorCode(), clientInfo);
        // ...
    }

    log.info("Authentication successful from lotecs-auth - user: {}, client: {}",
             userInfo.userId(), clientInfo);
    // ...
}

private String extractClientInfo() {
    // gRPC Context에서 클라이언트 정보 추출
    return "lotecs-auth";  // 또는 metadata에서 추출
}
```

## 테스트

### 1. 단독 테스트

**lotecs-relay gRPC API 직접 호출 테스트**:

```bash
# grpcurl 사용
grpcurl -plaintext \
  -d '{
    "user_id": "20190001",
    "password": "test1234",
    "ip_address": "127.0.0.1"
  }' \
  localhost:8180 \
  lotecs.auth.v1.AuthService/Login
```

**예상 응답**:
```json
{
  "success": true,
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": "3600",
  "userInfo": {
    "userId": "20190001",
    "userName": "홍길동",
    "userType": "STUDENT",
    "department": "컴퓨터공학과",
    "roles": ["ROLE_USER", "ROLE_STUDENT"],
    "email": "hong@sejong.ac.kr",
    "phone": "010-1234-5678",
    "externalId": "20190001"
  }
}
```

### 2. 통합 테스트

**lotecs-auth에서 호출 테스트**:

lotecs-auth 구현 완료 후 다음 시나리오 테스트:

1. lotecs-auth에 세종대 테넌트 로그인 요청
2. lotecs-auth가 relay_endpoint로 gRPC 호출
3. lotecs-relay가 세종대 SSO 시스템 호출
4. 인증 성공 응답 확인
5. lotecs-auth에서 사용자 동기화 확인 (auth_users, auth_external_user_mapping)

### 3. 성능 테스트

**gRPC 채널 재사용 확인**:
- lotecs-auth의 RelayClient가 채널을 올바르게 캐싱하는지
- 동시 요청 처리 성능 (100 req/s)

**타임아웃 테스트**:
- lotecs-relay 응답 지연 시 lotecs-auth의 타임아웃 동작 확인
- 권장 타임아웃: 5초 (relay_timeout_ms=5000)

## 배포

### 1. 배포 순서

```
1. lotecs-relay 업데이트 (UserInfo 필드 추가, 선택사항)
   ↓
2. lotecs-relay 재배포 (무중단 배포 가능)
   ↓
3. lotecs-auth 배포
   ↓
4. 통합 테스트
```

### 2. 무중단 배포

**lotecs-relay는 기존 API와 호환되므로 무중단 배포 가능**:

```bash
# lotecs-relay 서버에서
cd ~/lotecs-relay
./gradlew :relay-service:bootJar -Ptenant=sejong

# Docker 재시작 (기존 연결 유지)
docker-compose up -d --no-deps relay-service
```

### 3. 롤백 계획

**lotecs-auth에서 relay 호출 실패 시**:
- Circuit Breaker 미적용 시점에는 에러 응답 반환
- 사용자에게 "인증 서버 일시 장애" 메시지 표시

**lotecs-relay 롤백**:
- 이전 Docker 이미지로 롤백
```bash
docker-compose down
docker tag lotecs-relay:sejong-backup lotecs-relay:sejong
docker-compose up -d
```

## 모니터링

### 1. 메트릭 수집

**lotecs-relay gRPC 호출 메트릭**:
- `/metrics` 엔드포인트에서 gRPC 호출 수, 응답 시간, 에러율 확인
- Prometheus에서 수집

**주요 메트릭**:
```
grpc_server_handled_total{grpc_method="Login", grpc_service="lotecs.auth.v1.AuthService"}
grpc_server_handling_seconds{grpc_method="Login"}
```

### 2. 로그 모니터링

**lotecs-auth 호출 로그 패턴**:
```
2025-12-15 10:00:00 INFO  AuthGrpcService - Login request from lotecs-auth - user: 20190001
2025-12-15 10:00:01 INFO  AuthGrpcService - Authentication successful from lotecs-auth - user: 20190001
```

**Promtail 설정에 추가**:
```yaml
- job_name: lotecs-relay
  static_configs:
    - targets:
        - localhost
      labels:
        job: lotecs-relay
        tenant: sejong
        __path__: /var/log/lotecs-relay/*.log
```

## 주의사항

### 1. JWT Secret Key 동기화

**lotecs-auth와 lotecs-relay는 동일한 JWT Secret Key를 사용해야 합니다**:

- lotecs-auth: `lotecs.jwt.secret-key`
- lotecs-relay: `lotecs.jwt.secret`

**동일하지 않으면 발생하는 문제**:
- lotecs-relay가 생성한 JWT를 lotecs-auth가 검증할 수 없음
- 다른 MSA 서비스에서 토큰 검증 실패

### 2. 단일 테넌트 아키텍처

lotecs-relay는 학교별로 별도 인스턴스로 배포되므로:
- `TENANT_ID` 환경변수로 테넌트 고정
- lotecs-auth에서는 테넌트별 relay_endpoint 설정 필요

### 3. gRPC 연결 안정성

**연결 풀 관리**:
- lotecs-auth의 RelayClient는 gRPC 채널을 캐싱
- 장시간 미사용 시 연결 끊김 가능 → keepalive 설정 권장

**gRPC Keepalive 설정** (lotecs-auth RelayClient):
```java
ManagedChannel channel = ManagedChannelBuilder
    .forTarget(relayEndpoint)
    .usePlaintext()
    .keepAliveTime(30, TimeUnit.SECONDS)
    .keepAliveTimeout(10, TimeUnit.SECONDS)
    .build();
```

### 4. 보안

**gRPC TLS 적용 (권장)**:
- 프로덕션 환경에서는 gRPC over TLS 사용 권장
- lotecs-relay에 TLS 인증서 설정

```yaml
# relay-service application.yml
grpc:
  server:
    port: 8180
    security:
      enabled: true
      certificate-chain: classpath:server.crt
      private-key: classpath:server.key
```

## 체크리스트

### 배포 전 확인사항

- [ ] UserInfo 필드 확장 여부 결정 (email, phone, external_id)
- [ ] AuthUserInfo record 수정 (필드 추가 시)
- [ ] AuthGrpcService.toProtoUserInfo 메서드 수정
- [ ] JWT_SECRET_KEY가 lotecs-auth와 동일한지 확인
- [ ] gRPC 포트(8180) 방화벽 허용
- [ ] 테넌트별 AuthProvider가 email, phone 제공하는지 확인
- [ ] grpcurl로 단독 테스트 완료
- [ ] 로깅 레벨 설정 (INFO 이상)
- [ ] Prometheus 메트릭 수집 설정

### 배포 후 확인사항

- [ ] lotecs-auth에서 relay 호출 성공 확인
- [ ] 사용자 동기화 확인 (auth_users, auth_external_user_mapping)
- [ ] JWT 토큰이 다른 MSA 서비스에서 검증되는지 확인
- [ ] gRPC 메트릭 정상 수집 확인
- [ ] 로그에 lotecs-auth 호출 기록 확인
- [ ] 타임아웃 시나리오 테스트 (relay 응답 지연)

## 추가 고려사항

### 1. Circuit Breaker (Phase 4)

향후 gRPC SDK에 Circuit Breaker 적용 시:
- lotecs-auth의 RelayClient에 Resilience4j Circuit Breaker 적용
- relay 장애 시 빠른 실패 (Fail-Fast)
- Fallback: 캐시된 사용자 정보 또는 에러 응답

### 2. 사용자 정보 캐시

**relay 부하 감소를 위한 캐시**:
- getUserInfo 메서드 결과를 lotecs-auth에서 캐싱
- TTL: 5분
- 캐시 무효화: 사용자 정보 변경 이벤트 수신 시

### 3. Audit Log

**lotecs-auth 호출 추적**:
- relay에서 lotecs-auth의 호출을 audit log에 기록
- 누가, 언제, 어떤 사용자를 인증했는지 추적

## 참고 문서

- lotecs-relay README: `/lotecs-framework/services/lotecs-relay/README.md`
- auth.proto: `/lotecs-relay/relay-core/src/main/proto/auth.proto`
- LOTECS_AUTH_구현계획.md: lotecs-auth 서비스 구현 가이드
- AUTH_SERVICE_분리계획.md: 전체 아키텍처 설계
