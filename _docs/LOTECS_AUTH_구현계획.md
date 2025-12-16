---
parallel:
  enabled: true
  version: "1.0"
  run_id: null
  status_dir: "services/lotecs-auth/_docs/parallel-runs"

  # Phase 1: 필수 안정성 설정
  max_concurrent: 3  # 최대 동시 실행 agent 수
  on_failure: continue  # continue | stop

  timeout:
    per_task: 3600  # 초 (1시간)
    total: 14400    # 초 (4시간)
    action: fail    # fail | warn

  notifications:
    enabled: true
    events:
      - task_failed
      - task_timeout
      - level_completed
      - run_completed

groups:
  - id: group-1
    name: "환경 준비"
  - id: group-2
    name: "Domain 레이어"
  - id: group-3
    name: "SSO Provider"
  - id: group-4
    name: "Application 레이어"
  - id: group-5
    name: "Presentation 레이어"
  - id: group-6
    name: "테스트"
  - id: group-7
    name: "배포"

tasks:
  # Phase 1: 환경 준비
  - id: task-1-1
    title: "DB 스키마 생성"
    group: group-1
    dependencies: []
    files:
      - "services/lotecs-auth/_docs/LOTECS_AUTH_구현계획.md#21-oracle-스키마-생성"
    priority: 1
    status: pending
    agent_id: null
    started_at: null
    completed_at: null
    error: null
    output_file: null
    timeout: null
    retry_count: 0

  - id: task-1-2
    title: "테이블 생성"
    group: group-1
    dependencies: ["task-1-1"]
    files:
      - "services/lotecs-auth/_docs/LOTECS_AUTH_구현계획.md#22-테이블-정의"
    priority: 1
    status: pending
    agent_id: null
    started_at: null
    completed_at: null
    error: null
    output_file: null
    timeout: null
    retry_count: 0

  - id: task-1-3
    title: "프로젝트 구조 생성"
    group: group-1
    dependencies: ["task-1-2"]
    files:
      - "services/lotecs-auth/lotecs-auth-backend"
      - "services/lotecs-auth/lotecs-auth-grpc-api"
    priority: 1
    status: pending
    agent_id: null
    started_at: null
    completed_at: null
    error: null
    output_file: null
    timeout: null
    retry_count: 0

  # Phase 2: Domain 레이어
  - id: task-2-1
    title: "Domain 모델 작성"
    group: group-2
    dependencies: ["task-1-3"]
    files:
      - "services/lotecs-auth/lotecs-auth-backend/src/main/java/lotecs/auth/domain/model/User.java"
      - "services/lotecs-auth/lotecs-auth-backend/src/main/java/lotecs/auth/domain/model/Role.java"
      - "services/lotecs-auth/lotecs-auth-backend/src/main/java/lotecs/auth/domain/model/Permission.java"
      - "services/lotecs-auth/lotecs-auth-backend/src/main/java/lotecs/auth/domain/model/TenantSsoConfig.java"
      - "services/lotecs-auth/lotecs-auth-backend/src/main/java/lotecs/auth/domain/model/ExternalUserMapping.java"
      - "services/lotecs-auth/lotecs-auth-backend/src/main/java/lotecs/auth/domain/sso/SsoType.java"
    priority: 2
    status: pending
    agent_id: null
    started_at: null
    completed_at: null
    error: null
    output_file: null
    timeout: null
    retry_count: 0

  - id: task-2-2
    title: "Repository 인터페이스 작성"
    group: group-2
    dependencies: ["task-2-1"]
    files:
      - "services/lotecs-auth/lotecs-auth-backend/src/main/java/lotecs/auth/domain/repository"
    priority: 2
    status: pending
    agent_id: null
    started_at: null
    completed_at: null
    error: null
    output_file: null
    timeout: null
    retry_count: 0

  - id: task-2-3
    title: "MyBatis Mapper 작성"
    group: group-2
    dependencies: ["task-2-2"]
    files:
      - "services/lotecs-auth/lotecs-auth-backend/src/main/resources/mybatis/mapper"
      - "services/lotecs-auth/lotecs-auth-backend/src/main/java/lotecs/auth/infrastructure/persistence/mybatis"
    priority: 2
    status: pending
    agent_id: null
    started_at: null
    completed_at: null
    error: null
    output_file: null
    timeout: null
    retry_count: 0

  # Phase 3: SSO Provider
  - id: task-3-1
    title: "RelayClient 구현 (gRPC)"
    group: group-3
    dependencies: ["task-2-1"]
    files:
      - "services/lotecs-auth/lotecs-auth-backend/src/main/java/lotecs/auth/infrastructure/relay/RelayClient.java"
      - "services/lotecs-auth/lotecs-auth-grpc-api/src/main/proto/auth_service.proto"
    priority: 3
    status: pending
    agent_id: null
    started_at: null
    completed_at: null
    error: null
    output_file: null
    timeout: null
    retry_count: 0

  - id: task-3-2
    title: "KeycloakSsoProvider 구현"
    group: group-3
    dependencies: ["task-2-1"]
    files:
      - "services/lotecs-auth/lotecs-auth-backend/src/main/java/lotecs/auth/infrastructure/sso/KeycloakSsoProvider.java"
      - "services/lotecs-auth/lotecs-auth-backend/src/main/java/lotecs/auth/infrastructure/sso/LdapSsoProvider.java"
    priority: 3
    status: pending
    agent_id: null
    started_at: null
    completed_at: null
    error: null
    output_file: null
    timeout: null
    retry_count: 0

  - id: task-3-3
    title: "SsoProviderFactory 구현"
    group: group-3
    dependencies: ["task-3-1", "task-3-2"]
    files:
      - "services/lotecs-auth/lotecs-auth-backend/src/main/java/lotecs/auth/infrastructure/sso/SsoProviderFactory.java"
      - "services/lotecs-auth/lotecs-auth-backend/src/main/java/lotecs/auth/domain/sso/SsoProvider.java"
    priority: 3
    status: pending
    agent_id: null
    started_at: null
    completed_at: null
    error: null
    output_file: null
    timeout: null
    retry_count: 0

  # Phase 4: Application 레이어
  - id: task-4-1
    title: "TokenService 구현"
    group: group-4
    dependencies: ["task-2-3"]
    files:
      - "services/lotecs-auth/lotecs-auth-backend/src/main/java/lotecs/auth/application/service/TokenService.java"
    priority: 4
    status: pending
    agent_id: null
    started_at: null
    completed_at: null
    error: null
    output_file: null
    timeout: null
    retry_count: 0

  - id: task-4-2
    title: "UserSyncService 구현"
    group: group-4
    dependencies: ["task-2-3", "task-4-1"]
    files:
      - "services/lotecs-auth/lotecs-auth-backend/src/main/java/lotecs/auth/application/service/UserSyncService.java"
    priority: 4
    status: pending
    agent_id: null
    started_at: null
    completed_at: null
    error: null
    output_file: null
    timeout: null
    retry_count: 0

  - id: task-4-3
    title: "AuthService 구현"
    group: group-4
    dependencies: ["task-3-3", "task-4-2"]
    files:
      - "services/lotecs-auth/lotecs-auth-backend/src/main/java/lotecs/auth/application/service/AuthService.java"
    priority: 4
    status: pending
    agent_id: null
    started_at: null
    completed_at: null
    error: null
    output_file: null
    timeout: null
    retry_count: 0

  # Phase 5: Presentation 레이어
  - id: task-5-1
    title: "REST API 구현"
    group: group-5
    dependencies: ["task-4-3"]
    files:
      - "services/lotecs-auth/lotecs-auth-backend/src/main/java/lotecs/auth/presentation/controller/AuthController.java"
      - "services/lotecs-auth/lotecs-auth-backend/src/main/java/lotecs/auth/presentation/controller/AdminController.java"
      - "services/lotecs-auth/lotecs-auth-backend/src/main/java/lotecs/auth/presentation/controller/SsoAdminController.java"
    priority: 5
    status: pending
    agent_id: null
    started_at: null
    completed_at: null
    error: null
    output_file: null
    timeout: null
    retry_count: 0

  - id: task-5-2
    title: "gRPC 서비스 구현"
    group: group-5
    dependencies: ["task-4-3"]
    files:
      - "services/lotecs-auth/lotecs-auth-backend/src/main/java/lotecs/auth/presentation/grpc/AuthGrpcServiceImpl.java"
    priority: 5
    status: pending
    agent_id: null
    started_at: null
    completed_at: null
    error: null
    output_file: null
    timeout: null
    retry_count: 0

  # Phase 6: 테스트
  - id: task-6-1
    title: "단위 테스트 작성"
    group: group-6
    dependencies: ["task-5-1", "task-5-2"]
    files:
      - "services/lotecs-auth/lotecs-auth-backend/src/test/java"
    priority: 6
    status: pending
    agent_id: null
    started_at: null
    completed_at: null
    error: null
    output_file: null
    timeout: null
    retry_count: 0

  - id: task-6-2
    title: "통합 테스트 작성"
    group: group-6
    dependencies: ["task-6-1"]
    files:
      - "services/lotecs-auth/lotecs-auth-backend/src/test/java"
    priority: 6
    status: pending
    agent_id: null
    started_at: null
    completed_at: null
    error: null
    output_file: null
    timeout: null
    retry_count: 0

  - id: task-6-3
    title: "SSO 연동 테스트"
    group: group-6
    dependencies: ["task-6-2"]
    files:
      - "services/lotecs-auth/lotecs-auth-backend/src/test/java"
    priority: 6
    status: pending
    agent_id: null
    started_at: null
    completed_at: null
    error: null
    output_file: null
    timeout: null
    retry_count: 0

  # Phase 7: 배포
  - id: task-7-1
    title: "Dev 환경 배포"
    group: group-7
    dependencies: ["task-6-3"]
    files:
      - "services/lotecs-auth/lotecs-auth-backend/src/main/resources/application-dev.yml"
    priority: 7
    status: pending
    agent_id: null
    started_at: null
    completed_at: null
    error: null
    output_file: null
    timeout: null
    retry_count: 0

  - id: task-7-2
    title: "Prod 환경 배포"
    group: group-7
    dependencies: ["task-7-1"]
    files:
      - "services/lotecs-auth/lotecs-auth-backend/src/main/resources/application-prod.yml"
    priority: 7
    status: pending
    agent_id: null
    started_at: null
    completed_at: null
    error: null
    output_file: null
    timeout: null
    retry_count: 0
---

# LOTECS Auth Service 구현 계획서

> 작성일: 2025-12-15
> 버전: 1.0.0
> 서비스명: lotecs-auth

---

## 1. 개요

### 1.1 서비스 목적

LOTECS 플랫폼의 중앙 인증 서비스로, 다음 기능을 제공:
- JWT 기반 사용자 인증/인가
- 멀티테넌트 사용자 관리
- 하이브리드 SSO 지원 (lotecs-relay 위임 + 직접 SSO)
- 역할/권한 기반 접근 제어

### 1.2 서비스 정보

| 항목 | 내용 |
|------|------|
| 서비스명 | lotecs-auth |
| 포트 | REST: 8084, gRPC: 9092 |
| 배포 위치 | 192.168.0.57 |
| DB | Oracle (lotecs_auth 스키마) |
| 프레임워크 | Spring Boot 3.2.5, LOTECS Framework |

---

## 2. 데이터베이스 설계

### 2.1 Oracle 스키마 생성

```sql
-- lotecs_auth 사용자 생성
CREATE USER lotecs_auth IDENTIFIED BY lotecs9240
  DEFAULT TABLESPACE USERS
  TEMPORARY TABLESPACE TEMP
  QUOTA UNLIMITED ON USERS;

GRANT CONNECT, RESOURCE TO lotecs_auth;
GRANT CREATE SESSION, CREATE TABLE, CREATE VIEW, CREATE SEQUENCE TO lotecs_auth;
```

### 2.2 테이블 정의

#### auth_users (사용자)

```sql
CREATE TABLE auth_users (
    user_id VARCHAR2(50) PRIMARY KEY,
    tenant_id VARCHAR2(50) NOT NULL,
    username VARCHAR2(50) NOT NULL,
    password VARCHAR2(255) NOT NULL,
    email VARCHAR2(255),
    phone_number VARCHAR2(50),
    full_name VARCHAR2(100),
    status VARCHAR2(20) NOT NULL,
    account_non_locked NUMBER(1) DEFAULT 1,
    credentials_non_expired NUMBER(1) DEFAULT 1,
    enabled NUMBER(1) DEFAULT 1,
    last_login_at TIMESTAMP,
    last_login_ip VARCHAR2(50),
    failed_login_attempts NUMBER DEFAULT 0,
    locked_at TIMESTAMP,
    password_changed_at TIMESTAMP,
    created_by VARCHAR2(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR2(50),
    updated_at TIMESTAMP,
    CONSTRAINT uk_auth_users_username UNIQUE (tenant_id, username),
    CONSTRAINT ck_auth_users_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED'))
);

CREATE INDEX idx_auth_users_tenant ON auth_users(tenant_id);
CREATE INDEX idx_auth_users_email ON auth_users(email);
CREATE INDEX idx_auth_users_status ON auth_users(status);
```

#### auth_roles (역할)

```sql
CREATE TABLE auth_roles (
    role_id VARCHAR2(50) PRIMARY KEY,
    tenant_id VARCHAR2(50) NOT NULL,
    role_name VARCHAR2(50) NOT NULL,
    display_name VARCHAR2(100),
    description VARCHAR2(500),
    priority NUMBER DEFAULT 0,
    created_by VARCHAR2(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR2(50),
    updated_at TIMESTAMP,
    CONSTRAINT uk_auth_roles_name UNIQUE (tenant_id, role_name)
);

CREATE INDEX idx_auth_roles_tenant ON auth_roles(tenant_id);
```

#### auth_permissions (권한)

```sql
CREATE TABLE auth_permissions (
    permission_id VARCHAR2(50) PRIMARY KEY,
    permission_code VARCHAR2(100) NOT NULL UNIQUE,
    description VARCHAR2(500),
    resource_type VARCHAR2(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_auth_permissions_code ON auth_permissions(permission_code);
```

#### auth_user_roles (사용자-역할 매핑)

```sql
CREATE TABLE auth_user_roles (
    user_id VARCHAR2(50) NOT NULL,
    role_id VARCHAR2(50) NOT NULL,
    tenant_id VARCHAR2(50) NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by VARCHAR2(50),
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_auth_ur_user FOREIGN KEY (user_id) REFERENCES auth_users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_auth_ur_role FOREIGN KEY (role_id) REFERENCES auth_roles(role_id) ON DELETE CASCADE
);

CREATE INDEX idx_auth_user_roles_user ON auth_user_roles(user_id);
CREATE INDEX idx_auth_user_roles_role ON auth_user_roles(role_id);
```

#### auth_role_permissions (역할-권한 매핑)

```sql
CREATE TABLE auth_role_permissions (
    role_id VARCHAR2(50) NOT NULL,
    permission_id VARCHAR2(50) NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_auth_rp_role FOREIGN KEY (role_id) REFERENCES auth_roles(role_id) ON DELETE CASCADE,
    CONSTRAINT fk_auth_rp_perm FOREIGN KEY (permission_id) REFERENCES auth_permissions(permission_id) ON DELETE CASCADE
);

CREATE INDEX idx_auth_rp_role ON auth_role_permissions(role_id);
```

#### auth_refresh_tokens (리프레시 토큰)

```sql
CREATE TABLE auth_refresh_tokens (
    token_id VARCHAR2(50) PRIMARY KEY,
    user_id VARCHAR2(50) NOT NULL,
    tenant_id VARCHAR2(50) NOT NULL,
    refresh_token VARCHAR2(500) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_auth_rt_user FOREIGN KEY (user_id) REFERENCES auth_users(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_auth_rt_user ON auth_refresh_tokens(user_id);
CREATE INDEX idx_auth_rt_token ON auth_refresh_tokens(refresh_token);
CREATE INDEX idx_auth_rt_expires ON auth_refresh_tokens(expires_at);
```

#### auth_tenant_sso_config (SSO 설정)

```sql
CREATE TABLE auth_tenant_sso_config (
    tenant_id VARCHAR2(50) PRIMARY KEY,
    sso_type VARCHAR2(20) NOT NULL,  -- INTERNAL/RELAY/KEYCLOAK/LDAP/EXTERNAL
    sso_enabled NUMBER(1) DEFAULT 1,

    -- RELAY 타입 설정 (학교 테넌트)
    relay_endpoint VARCHAR2(500),     -- lotecs-relay gRPC 주소
    relay_timeout_ms NUMBER DEFAULT 5000,

    -- 직접 SSO 설정 (일반 기업)
    sso_server_url VARCHAR2(500),
    sso_realm VARCHAR2(100),
    sso_client_id VARCHAR2(100),
    sso_client_secret VARCHAR2(500),

    -- 동기화 설정
    user_sync_enabled NUMBER(1) DEFAULT 1,
    role_mapping_enabled NUMBER(1) DEFAULT 1,

    -- 추가 설정 (JSON)
    additional_config CLOB,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT ck_auth_sso_type CHECK (sso_type IN ('INTERNAL', 'RELAY', 'KEYCLOAK', 'LDAP', 'EXTERNAL'))
);

CREATE INDEX idx_auth_sso_type ON auth_tenant_sso_config(sso_type);

COMMENT ON TABLE auth_tenant_sso_config IS '테넌트별 SSO 설정';
COMMENT ON COLUMN auth_tenant_sso_config.sso_type IS 'INTERNAL: 자체인증, RELAY: lotecs-relay위임, KEYCLOAK/LDAP/EXTERNAL: 직접SSO';
```

#### auth_external_user_mapping (외부 사용자 매핑)

```sql
CREATE TABLE auth_external_user_mapping (
    mapping_id VARCHAR2(50) PRIMARY KEY,
    tenant_id VARCHAR2(50) NOT NULL,
    user_id VARCHAR2(50) NOT NULL,  -- lotecs-auth user_id
    external_user_id VARCHAR2(200) NOT NULL,  -- 외부 시스템 user_id (학번, 직번)
    external_system VARCHAR2(50) NOT NULL,  -- RELAY/KEYCLOAK/LDAP
    last_synced_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_auth_ext_user FOREIGN KEY (user_id) REFERENCES auth_users(user_id) ON DELETE CASCADE,
    CONSTRAINT uk_auth_ext_mapping UNIQUE (tenant_id, external_user_id, external_system)
);

CREATE INDEX idx_auth_ext_user ON auth_external_user_mapping(user_id);
CREATE INDEX idx_auth_ext_external ON auth_external_user_mapping(external_user_id);

COMMENT ON TABLE auth_external_user_mapping IS '외부 시스템 사용자 ID 매핑 (학번↔user_id)';
```

### 2.3 초기 데이터

```sql
-- 세종대 (RELAY)
INSERT INTO auth_tenant_sso_config (
    tenant_id, sso_type, sso_enabled,
    relay_endpoint, relay_timeout_ms,
    user_sync_enabled, role_mapping_enabled
) VALUES (
    'sejong', 'RELAY', 1,
    'sejong-server.university.ac.kr:9090', 5000,
    1, 1
);

-- 기본 테넌트 (INTERNAL)
INSERT INTO auth_tenant_sso_config (
    tenant_id, sso_type, sso_enabled
) VALUES (
    'default', 'INTERNAL', 1
);

COMMIT;
```

---

## 3. 프로젝트 구조

```
lotecs-auth/
├── lotecs-auth-backend/
│   ├── src/main/java/lotecs/auth/
│   │   ├── LotecsAuthApplication.java
│   │   ├── presentation/
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── AdminController.java
│   │   │   │   └── SsoAdminController.java
│   │   │   └── grpc/
│   │   │       └── AuthGrpcServiceImpl.java
│   │   ├── application/
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── TokenService.java
│   │   │   │   ├── UserService.java
│   │   │   │   ├── UserSyncService.java
│   │   │   │   └── SsoConfigService.java
│   │   │   └── dto/
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   ├── Role.java
│   │   │   │   ├── Permission.java
│   │   │   │   ├── TenantSsoConfig.java
│   │   │   │   └── ExternalUserMapping.java
│   │   │   ├── repository/
│   │   │   └── sso/
│   │   │       ├── SsoProvider.java
│   │   │       ├── SsoType.java
│   │   │       ├── SsoAuthRequest.java
│   │   │       └── SsoAuthResult.java
│   │   ├── infrastructure/
│   │   │   ├── persistence/mybatis/
│   │   │   ├── relay/
│   │   │   │   └── RelayClient.java
│   │   │   ├── sso/
│   │   │   │   ├── KeycloakSsoProvider.java
│   │   │   │   ├── LdapSsoProvider.java
│   │   │   │   └── SsoProviderFactory.java
│   │   │   └── config/
│   │   └── config/
│   │       ├── SecurityConfig.java
│   │       ├── JwtConfig.java
│   │       ├── GrpcConfig.java
│   │       └── SsoConfig.java
│   └── src/main/resources/
│       ├── application.yml
│       ├── application-dev.yml
│       ├── application-prod.yml
│       └── mybatis/mapper/
├── lotecs-auth-grpc-api/
│   └── src/main/proto/
│       └── auth_service.proto
└── build.gradle
```

---

## 4. SSO 연동 설계

### 4.1 SSO Type 정의

```java
// domain/sso/SsoType.java
public enum SsoType {
    INTERNAL,   // 자체 DB 인증
    RELAY,      // lotecs-relay 위임 (학교)
    KEYCLOAK,   // Keycloak 직접 연동
    LDAP,       // LDAP 직접 연동
    EXTERNAL    // 외부 API 직접 연동
}
```

### 4.2 Relay Client (gRPC)

```java
// infrastructure/relay/RelayClient.java
@Component
@RequiredArgsConstructor
@Slf4j
public class RelayClient {

    private final Map<String, ManagedChannel> channelCache = new ConcurrentHashMap<>();

    /**
     * lotecs-relay 인증 호출
     *
     * @param relayEndpoint lotecs-relay gRPC 주소 (예: sejong-server:9090)
     */
    public RelayAuthResponse authenticate(String relayEndpoint, RelayAuthRequest request) {

        ManagedChannel channel = getOrCreateChannel(relayEndpoint);
        AuthServiceGrpc.AuthServiceBlockingStub stub = AuthServiceGrpc.newBlockingStub(channel);

        try {
            LoginRequest grpcRequest = LoginRequest.newBuilder()
                .setUsername(request.getUsername())
                .setPassword(request.getPassword())
                .setTenantId(request.getTenantId())
                .build();

            LoginResponse grpcResponse = stub
                .withDeadlineAfter(5, TimeUnit.SECONDS)
                .login(grpcRequest);

            return RelayAuthResponse.builder()
                .success(true)
                .externalUserId(grpcResponse.getUser().getUserId())
                .username(grpcResponse.getUser().getUsername())
                .email(grpcResponse.getUser().getEmail())
                .fullName(grpcResponse.getUser().getFullName())
                .roles(grpcResponse.getUser().getRolesList())
                .build();

        } catch (StatusRuntimeException e) {
            log.error("[RELAY] gRPC 호출 실패: endpoint={}, status={}",
                relayEndpoint, e.getStatus(), e);

            return RelayAuthResponse.builder()
                .success(false)
                .errorCode("RELAY_ERROR")
                .errorMessage(e.getStatus().getDescription())
                .build();
        }
    }

    private ManagedChannel getOrCreateChannel(String endpoint) {
        return channelCache.computeIfAbsent(endpoint, key -> {
            String[] parts = endpoint.split(":");
            String host = parts[0];
            int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 9090;

            return ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        });
    }

    @PreDestroy
    public void shutdown() {
        channelCache.values().forEach(ManagedChannel::shutdown);
    }
}
```

### 4.3 Auth Service 메인 로직

```java
// application/service/AuthService.java
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final TenantSsoConfigRepository ssoConfigRepository;
    private final RelayClient relayClient;
    private final SsoProviderFactory ssoProviderFactory;
    private final UserSyncService userSyncService;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {

        // 1. SSO 설정 조회
        TenantSsoConfig ssoConfig = ssoConfigRepository
            .findByTenantId(request.getTenantId())
            .orElseGet(() -> getDefaultInternalConfig(request.getTenantId()));

        log.info("[AUTH] 로그인 시도: tenant={}, ssoType={}",
            request.getTenantId(), ssoConfig.getSsoType());

        User user;

        // 2. SSO 타입별 분기
        switch (ssoConfig.getSsoType()) {
            case INTERNAL:
                user = authenticateInternal(request);
                break;

            case RELAY:
                user = authenticateViaRelay(request, ssoConfig);
                break;

            case KEYCLOAK:
            case LDAP:
            case EXTERNAL:
                user = authenticateDirect(request, ssoConfig);
                break;

            default:
                throw new UnsupportedSsoTypeException(ssoConfig.getSsoType());
        }

        // 3. 로그인 정보 업데이트
        user.updateLoginInfo(request.getIpAddress());
        userRepository.save(user);

        // 4. JWT 발급
        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        log.info("[AUTH-001] 로그인 성공: userId={}, tenant={}, ssoType={}, ip={}",
            user.getUserId(), user.getTenantId(), ssoConfig.getSsoType(), request.getIpAddress());

        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(900)
            .user(UserDto.from(user))
            .build();
    }

    private User authenticateInternal(LoginRequest request) {
        User user = userRepository
            .findByUsernameAndTenantId(request.getUsername(), request.getTenantId())
            .orElseThrow(() -> new UserNotFoundException());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return user;
    }

    private User authenticateViaRelay(LoginRequest request, TenantSsoConfig ssoConfig) {

        log.info("[AUTH] lotecs-relay 호출: endpoint={}", ssoConfig.getRelayEndpoint());

        RelayAuthRequest relayRequest = RelayAuthRequest.builder()
            .tenantId(request.getTenantId())
            .username(request.getUsername())
            .password(request.getPassword())
            .build();

        RelayAuthResponse relayResponse = relayClient.authenticate(
            ssoConfig.getRelayEndpoint(),
            relayRequest
        );

        if (!relayResponse.isSuccess()) {
            log.warn("[AUTH] relay 인증 실패: {}", relayResponse.getErrorMessage());
            throw new AuthenticationFailedException(relayResponse.getErrorCode());
        }

        return userSyncService.syncUserFromRelay(relayResponse, ssoConfig);
    }

    private User authenticateDirect(LoginRequest request, TenantSsoConfig ssoConfig) {
        // Keycloak/LDAP 직접 SSO 처리
        // ... 생략
    }
}
```

### 4.4 User Sync Service

```java
// application/service/UserSyncService.java
@Service
@RequiredArgsConstructor
@Slf4j
public class UserSyncService {

    private final UserRepository userRepository;
    private final ExternalUserMappingRepository mappingRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public User syncUserFromRelay(RelayAuthResponse relayResponse, TenantSsoConfig ssoConfig) {

        // 1. 외부 사용자 매핑 조회
        Optional<ExternalUserMapping> mappingOpt = mappingRepository.findByExternalUserId(
            ssoConfig.getTenantId(),
            relayResponse.getExternalUserId(),
            "RELAY"
        );

        User user;

        if (mappingOpt.isPresent()) {
            // 기존 사용자 업데이트
            user = userRepository.findById(mappingOpt.get().getUserId())
                .orElseThrow();

            user.updateFromExternal(
                relayResponse.getEmail(),
                relayResponse.getFullName()
            );

            log.info("[SYNC] 기존 사용자 업데이트: userId={}, externalId={}",
                user.getUserId(), relayResponse.getExternalUserId());

        } else {
            // 신규 사용자 생성
            user = User.createFromExternal(
                ssoConfig.getTenantId(),
                relayResponse.getUsername(),
                relayResponse.getEmail(),
                relayResponse.getFullName()
            );
            user = userRepository.save(user);

            // 외부 매핑 생성
            ExternalUserMapping mapping = ExternalUserMapping.builder()
                .mappingId(UUID.randomUUID().toString())
                .tenantId(ssoConfig.getTenantId())
                .userId(user.getUserId())
                .externalUserId(relayResponse.getExternalUserId())
                .externalSystem("RELAY")
                .lastSyncedAt(LocalDateTime.now())
                .build();

            mappingRepository.save(mapping);

            log.info("[SYNC] 신규 사용자 생성: userId={}, externalId={}",
                user.getUserId(), relayResponse.getExternalUserId());
        }

        // 2. 역할 동기화
        if (ssoConfig.isRoleMappingEnabled()) {
            syncUserRoles(user, relayResponse.getRoles());
        }

        return user;
    }

    private void syncUserRoles(User user, List<String> externalRoles) {
        if (externalRoles == null || externalRoles.isEmpty()) {
            return;
        }

        List<Role> roles = externalRoles.stream()
            .map(roleName -> roleRepository.findByRoleName(user.getTenantId(), roleName))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

        user.updateRoles(roles);
    }
}
```

---

## 5. API 설계

### 5.1 REST API (Port: 8084)

#### 인증 API

**POST /api/auth/login** - 로그인
```json
Request:
{
  "username": "admin",
  "password": "password",
  "tenantId": "sejong",
  "ipAddress": "192.168.0.100"
}

Response:
{
  "code": "SUCCESS",
  "data": {
    "accessToken": "eyJhbGci...",
    "refreshToken": "eyJhbGci...",
    "expiresIn": 900,
    "user": {
      "userId": "user-001",
      "username": "20181234",
      "email": "student@sejong.ac.kr",
      "fullName": "홍길동",
      "roles": ["STUDENT"],
      "ssoType": "RELAY"
    }
  }
}
```

**POST /api/auth/refresh** - 토큰 갱신
**POST /api/auth/logout** - 로그아웃
**POST /api/auth/validate** - 토큰 검증

#### 사용자 관리 API

**POST /api/admin/users** - 사용자 생성
**GET /api/admin/users/{userId}** - 사용자 조회
**PUT /api/admin/users/{userId}** - 사용자 수정
**DELETE /api/admin/users/{userId}** - 사용자 삭제
**GET /api/admin/users** - 사용자 목록

#### SSO 관리 API

**GET /api/admin/sso/config/{tenantId}** - SSO 설정 조회
**PUT /api/admin/sso/config/{tenantId}** - SSO 설정 수정
**POST /api/admin/sso/test** - SSO 연결 테스트

### 5.2 gRPC API (Port: 9092)

```protobuf
syntax = "proto3";

package lotecs.auth.grpc;

service AuthService {
  rpc Login(LoginRequest) returns (LoginResponse);
  rpc Logout(LogoutRequest) returns (LogoutResponse);
  rpc RefreshToken(RefreshTokenRequest) returns (RefreshTokenResponse);
  rpc ValidateToken(ValidateTokenRequest) returns (ValidateTokenResponse);

  rpc GetUserById(GetUserByIdRequest) returns (UserResponse);
  rpc GetUserByUsername(GetUserByUsernameRequest) returns (UserResponse);
  rpc GetUsers(GetUsersRequest) returns (GetUsersResponse);

  rpc CreateUser(CreateUserRequest) returns (UserResponse);
  rpc UpdateUser(UpdateUserRequest) returns (UserResponse);
  rpc DeleteUser(DeleteUserRequest) returns (DeleteUserResponse);

  rpc CheckPermission(PermissionCheckRequest) returns (PermissionCheckResponse);
}

message LoginRequest {
  string username = 1;
  string password = 2;
  string tenant_id = 3;
  string ip_address = 4;
}

message LoginResponse {
  string access_token = 1;
  string refresh_token = 2;
  int32 expires_in = 3;
  UserInfo user = 4;
  string sso_type = 5;
}

message UserInfo {
  string user_id = 1;
  string tenant_id = 2;
  string username = 3;
  string email = 4;
  string full_name = 5;
  string status = 6;
  repeated string roles = 7;
  repeated string permissions = 8;
  string external_user_id = 9;
  string external_system = 10;
}
```

---

## 6. 설정

### 6.1 application.yml

```yaml
server:
  port: 8084

grpc:
  server:
    port: 9092
    max-inbound-message-size: 4MB

spring:
  application:
    name: lotecs-auth

  datasource:
    driver-class-name: oracle.jdbc.driver.OracleDriver
    url: jdbc:oracle:thin:@//192.168.0.57:1521/xepdb1
    username: lotecs_auth
    password: ${AUTH_DB_PASSWORD:lotecs9240}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000

  data:
    redis:
      host: ${REDIS_HOST:192.168.0.57}
      port: ${REDIS_PORT:6379}

mybatis:
  mapper-locations: classpath:mybatis/mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true

lotecs:
  jwt:
    enabled: true
    secret: ${LOTECS_JWT_SECRET}
    access-token-validity-seconds: 900
    refresh-token-validity-seconds: 604800

  tenant:
    enabled: true
    default-tenant-id: SYSTEM

  logging:
    enabled: true
    service-name: lotecs-auth
    environment: ${SPRING_PROFILES_ACTIVE:local}

  cache:
    enabled: true
    type: redis

  ratelimit:
    enabled: true
    default-policy:
      capacity: 100
      refill-tokens: 100
      refill-period-minutes: 1

# SSO 설정
auth:
  sso:
    relay:
      enabled: true
      default-timeout-ms: 5000
    keycloak:
      enabled: true

logging:
  level:
    lotecs.auth: DEBUG
    lotecs.framework: INFO
```

---

## 7. 빌드 및 배포

### 7.1 의존성 (build.gradle)

```gradle
dependencies {
    // LOTECS Framework
    implementation project(':lotecs-framework-common:core:lotecs-core')
    implementation project(':lotecs-framework-common:security:lotecs-jwt-core')
    implementation project(':lotecs-framework-common:security:lotecs-jwt-web')
    implementation project(':lotecs-framework-common:cache:lotecs-cache-spring-boot-starter')
    implementation project(':lotecs-framework-common:crypto:lotecs-crypto-spring-boot-starter')
    implementation project(':lotecs-framework-common:logging:lotecs-logging-spring-boot-starter')
    implementation project(':lotecs-framework-common:tenant:lotecs-tenant-spring-boot-starter')
    implementation project(':lotecs-framework-common:mybatis:lotecs-mybatis-spring-boot-starter')
    implementation project(':lotecs-framework-common:exception:lotecs-exception-spring-boot-starter')
    implementation project(':lotecs-framework-common:web:lotecs-web-spring-boot-starter')
    implementation project(':lotecs-framework-common:ratelimit:lotecs-ratelimit-spring-boot-starter')

    // Spring Boot
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'

    // gRPC
    implementation 'net.devh:grpc-server-spring-boot-starter:2.15.0.RELEASE'
    implementation 'net.devh:grpc-client-spring-boot-starter:2.15.0.RELEASE'

    // MyBatis
    implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3'

    // Database
    runtimeOnly 'com.oracle.database.jdbc:ojdbc8'

    // JWT
    implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'

    // Lombok
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'

    // Test
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation project(':lotecs-framework-common:test:lotecs-test-spring-boot-starter')
}
```

### 7.2 빌드

```bash
./gradlew :lotecs-auth-backend:bootJar
```

### 7.3 배포

```bash
cd /app/lotecs-auth
java -jar lotecs-auth-backend-1.0.0.jar --spring.profiles.active=prod
```

---

## 8. 구현 일정

### Phase 1: 환경 준비 (1일)
- [ ] DB 스키마 생성
- [ ] 테이블 생성
- [ ] 프로젝트 생성

### Phase 2: Domain 레이어 (2일)
- [ ] Domain 모델 작성
- [ ] Repository 인터페이스
- [ ] MyBatis Mapper

### Phase 3: SSO Provider (2일)
- [ ] RelayClient 구현
- [ ] KeycloakSsoProvider 구현
- [ ] SsoProviderFactory 구현

### Phase 4: Application 레이어 (2일)
- [ ] AuthService 구현
- [ ] UserSyncService 구현
- [ ] TokenService 구현

### Phase 5: Presentation 레이어 (1일)
- [ ] REST API 구현
- [ ] gRPC 서비스 구현

### Phase 6: 테스트 (2일)
- [ ] 단위 테스트
- [ ] 통합 테스트
- [ ] SSO 연동 테스트

### Phase 7: 배포 (1일)
- [ ] Dev 환경 배포
- [ ] Prod 환경 배포

**총 예상 기간: 11일 (약 2주)**
