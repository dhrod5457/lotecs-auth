# lotecs-auth 서비스 아키텍처 분석

## 1. 서비스 개요

lotecs-auth는 LOTECS 플랫폼의 인증/인가 서비스로, 멀티테넌시 환경에서 다양한 SSO 방식을 지원하는 엔터프라이즈급 인증 서비스입니다.

### 기술 스택

- Java 21 + Spring Boot 3.x
- Oracle DB (ATH_ 접두사 스키마)
- Redis (JWT 토큰 블랙리스트, Rate Limit)
- gRPC (마이크로서비스 간 통신)
- MyBatis (DB 매핑)
- Lombok + MapStruct (보일러플레이트 감소)

### 포트 구성

- HTTP: 8000 (REST API, /api/v1/ath)
- gRPC: 9110 (마이크로서비스 통신)

## 2. 패키지 구조

```
lotecs.auth
├── presentation (프레젠테이션 계층)
│   ├── auth/AuthController - 인증 API
│   ├── user/AdminController - 사용자 관리 API
│   ├── sso/SsoAdminController - SSO 설정 API
│   └── grpc/service/auth/AuthGrpcServiceImpl - gRPC 서비스
├── application (애플리케이션 계층)
│   ├── auth/
│   │   ├── service/AuthService - 핵심 인증 로직
│   │   └── dto/ - 인증 관련 DTO
│   ├── user/
│   │   ├── service/UserService - 사용자 관리
│   │   ├── service/UserSyncService - 외부 SSO 사용자 동기화
│   │   ├── service/RoleStatusService - 역할 상태 관리
│   │   └── mapper/UserDtoMapper, RoleStatusDtoMapper
│   ├── sso/
│   │   ├── service/SsoConfigService - SSO 설정 관리
│   │   └── mapper/SsoConfigDtoMapper
│   └── tenant/
│       ├── service/TenantService - 테넌트 관리
│       └── mapper/TenantDtoMapper
├── domain (도메인 계층)
│   ├── user/
│   │   ├── model/User, Role, Permission, RoleStatus
│   │   ├── model/UserStatus - 사용자 상태 enum
│   │   ├── model/RolePermission, UserRole - 매핑 엔티티
│   │   └── repository/ - 인터페이스
│   ├── sso/
│   │   ├── SsoProvider - SSO 제공자 인터페이스
│   │   ├── SsoType - enum (INTERNAL, RELAY, KEYCLOAK, LDAP, EXTERNAL)
│   │   ├── SsoAuthRequest/SsoAuthResult
│   │   ├── model/TenantSsoConfig, ExternalUserMapping
│   │   └── repository/
│   ├── tenant/
│   │   ├── model/Tenant, SiteStatus
│   │   └── repository/TenantRepository
│   ├── token/model/RefreshToken
│   └── organization/
│       ├── model/Organization, UserOrganization
│       └── repository/
└── infrastructure (인프라 계층)
    ├── config/
    │   ├── SecurityConfig - Spring Security 설정
    │   ├── WebConfig - CORS 설정
    │   └── RelayClientConfig - Relay gRPC 클라이언트
    ├── persistence/ - 저장소 구현 + MyBatis Mapper
    └── sso/ - SSO 제공자 구현
        ├── SsoProviderFactory
        ├── RelaySsoProvider
        ├── KeycloakSsoProvider
        └── LdapSsoProvider
```

### 레이어별 책임

| 레이어 | 책임 | 주요 클래스 |
|--------|------|-------------|
| Presentation | HTTP/gRPC 요청 처리, 응답 변환 | AuthController, AdminController, AuthGrpcServiceImpl |
| Application | 비즈니스 로직 조정, DTO 변환 | AuthService, UserService, SsoConfigService |
| Domain | 핵심 비즈니스 로직, 엔티티 | User, Role, SsoProvider, SsoAuthRequest/Result |
| Infrastructure | DB 접근, 외부 시스템 연동 | RepositoryImpl, SSO Provider 구현체, MyBatis Mapper |

## 3. 외부 시스템 연동

### 3.1 데이터베이스 (Oracle)

- 테이블 접두사: `ATH_` (Authentication)
- 주요 테이블:
  - ATH_USERS, ATH_ROLES, ATH_PERMISSIONS
  - ATH_USER_ROLES, ATH_ROLE_PERMISSIONS
  - ATH_TENANT_SSO_CONFIG, ATH_EXTERNAL_USER_MAPPING
  - ATH_ORGANIZATIONS, ATH_USER_ORGANIZATIONS
  - ATH_ROLE_STATUS, ATH_TENANTS

**Connection Pool (HikariCP)**

| 설정 | 값 |
|------|-----|
| max-pool-size | 20 |
| min-idle | 5 |
| connection-timeout | 30s |
| idle-timeout | 600s |

### 3.2 Redis

**용도 1: JWT 토큰 블랙리스트**

```yaml
lotecs.jwt.blacklist:
  enabled: true
  storage: redis
  redis.key-prefix: "lotecs:jwt:blacklist:"
  redis.ttl-buffer-seconds: 300
```

**용도 2: Rate Limiting**

```yaml
lotecs.ratelimit:
  enabled: true
  redis:
    host: 192.168.0.57
    port: 6379
    bucket-expiration-minutes: 10
```

### 3.3 SSO 연동 (4가지 방식)

| 타입 | 구현체 | 연동 방식 | 의존성 |
|------|--------|----------|--------|
| INTERNAL | AuthService | DB 직접 조회 + BCrypt | - |
| RELAY | RelaySsoProvider | gRPC | relay-grpc-sdk |
| KEYCLOAK | KeycloakSsoProvider | REST API | keycloak-admin-client:23.0.3 |
| LDAP | LdapSsoProvider | LDAP Bind | apache-directory-api:2.1.5 |

## 4. API 명세

### 4.1 AuthController (`/auth`)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/auth/login` | 로그인 |
| POST | `/auth/logout` | 로그아웃 |
| POST | `/auth/refresh` | 토큰 갱신 |
| POST | `/auth/validate` | 토큰 검증 |

### 4.2 AdminController (`/admin`)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/admin/users` | 사용자 생성 |
| GET | `/admin/users/{userId}` | 사용자 조회 |
| PUT | `/admin/users/{userId}` | 사용자 수정 |
| DELETE | `/admin/users/{userId}` | 사용자 삭제 |
| GET | `/admin/users` | 사용자 목록 |

### 4.3 SsoAdminController (`/admin/sso`)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/admin/sso/config/{tenantId}` | SSO 설정 조회 |
| PUT | `/admin/sso/config/{tenantId}` | SSO 설정 수정 |
| POST | `/admin/sso/test` | SSO 연결 테스트 |

### 4.4 gRPC AuthService

```protobuf
service AuthService {
  rpc Login(LoginRequest) returns (LoginResponse);
  rpc Logout(LogoutRequest) returns (LogoutResponse);
  rpc RefreshToken(RefreshTokenRequest) returns (RefreshTokenResponse);
  rpc ValidateToken(ValidateTokenRequest) returns (ValidateTokenResponse);
  rpc GetUserById(GetUserByIdRequest) returns (UserResponse);
  rpc CreateUser(CreateUserRequest) returns (UserResponse);
  rpc UpdateUser(UpdateUserRequest) returns (UserResponse);
  rpc DeleteUser(DeleteUserRequest) returns (DeleteUserResponse);
  rpc CheckPermission(PermissionCheckRequest) returns (PermissionCheckResponse);
}
```

## 5. LOTECS Framework 모듈 적용 현황

| 모듈 | 상태 | 용도 |
|------|------|------|
| tenant | 적용 | 멀티테넌시, 행 레벨 보안 |
| jwt | 적용 | 토큰 발급, 검증, 블랙리스트 |
| logging | 적용 | Loki 로그 전송 |
| crypto | 적용 | AES, RSA, BCRYPT |
| ratelimit | 적용 | 100 req/min 기본값 |
| exception | 적용 | 표준 에러 응답 |
| web | 적용 | CORS, 공통 응답 포맷 |
| mybatis | 적용 | 느린 쿼리 모니터링 |
| util | 적용 | 유틸리티 |
| cache | 미적용 | Redis 블랙리스트만 사용 |
| circuit-breaker | 미적용 | - |
| i18n | 미적용 | - |
| message | 미적용 | - |
| storage | 미적용 | - |
| batch | 미적용 | - |

## 6. 주요 설정

### JWT 설정

```yaml
lotecs.jwt:
  access-token-validity-seconds: 900      # 15분
  refresh-token-validity-seconds: 604800  # 7일
  refresh-threshold-seconds: 300          # 토큰 갱신 기준
  issuer: "lotecs-platform"
  audience: "lotecs-services"
```

### 테넌트 설정

```yaml
lotecs.tenant:
  enabled: true
  default-tenant-id: SYSTEM
  mybatis:
    strategy: ROW_LEVEL
    column-name: tenant_id
```

### Rate Limit 설정

```yaml
lotecs.ratelimit:
  enabled: true
  default-policy:
    capacity: 100
    refill-period-minutes: 1
```

## 7. 서비스 흐름

### 로그인 흐름

```
POST /auth/login (LoginRequest)
    |
AuthController.login()
    |
AuthService.login()
    ├── SSO 설정 조회 (DB)
    ├── switch(SsoType)
    │   ├── INTERNAL: authenticateInternal()
    │   │   ├── 사용자 조회 (DB)
    │   │   ├── BCrypt 검증
    │   │   └── 계정 상태 확인
    │   │
    │   └── RELAY/KEYCLOAK/LDAP: authenticateExternal()
    │       ├── SsoProvider.authenticate()
    │       └── UserSyncService.syncUserFromExternal()
    │
    ├── User.recordLoginSuccess(ip)
    ├── JwtAuthenticationService.loginWithClaims()
    └── LoginResponse 반환
```

### 토큰 검증 흐름

```
POST /auth/validate
    |
AuthService.validate(accessToken)
    ├── JwtAuthenticationService.validateToken()
    │   ├── Redis 블랙리스트 확인
    │   ├── JWT 서명 검증
    │   └── 만료 시간 확인
    │
    └── ValidateTokenResponse
```
