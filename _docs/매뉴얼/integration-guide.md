# LOTECS Auth 연동 가이드

새로운 MSA 서비스에서 LOTECS Auth 서비스와 연동하는 방법을 설명한다.

## 목차

1. [개요](#개요)
2. [인증 흐름](#인증-흐름)
3. [의존성 추가](#의존성-추가)
4. [설정](#설정)
5. [JWT 인증 연동](#jwt-인증-연동)
6. [SSO 연동](#sso-연동)
7. [gRPC 클라이언트 사용](#grpc-클라이언트-사용)
8. [권한 체크](#권한-체크)
9. [멀티테넌시](#멀티테넌시)
10. [트러블슈팅](#트러블슈팅)

---

## 개요

LOTECS Auth는 JWT 기반 토큰 인증을 제공하는 중앙 인증 서비스다. 다른 MSA 서비스는 다음 두 가지 방식으로 연동할 수 있다:

| 연동 방식 | 용도 | 통신 |
|-----------|------|------|
| JWT 토큰 검증 | HTTP 요청의 인증/인가 처리 | 토큰 자체 검증 (Auth 서비스 호출 불필요) |
| gRPC SDK | 사용자/역할/권한 조회, 토큰 발급 등 | gRPC (포트 9110) |

### JWT 토큰 구조

```json
{
  "sub": "username",
  "userId": "사용자 UUID",
  "tenantId": "테넌트 ID",
  "username": "사용자명",
  "email": "이메일",
  "roles": "ROLE_ADMIN,ROLE_USER",
  "authorities": ["MENU_VIEW", "USER_EDIT", "REPORT_EXPORT"],
  "iat": 1234567890,
  "exp": 1234568790
}
```

- **Access Token 유효기간**: 15분 (900초)
- **Refresh Token 유효기간**: 7일 (604800초)

---

## 인증 흐름

### 1. 로그인 및 토큰 발급

```
┌─────────┐      ┌─────────────┐      ┌─────────────┐
│ Client  │      │ Auth Service│      │   Redis     │
└────┬────┘      └──────┬──────┘      └──────┬──────┘
     │                  │                    │
     │ POST /auth/login │                    │
     │─────────────────>│                    │
     │                  │                    │
     │  Access Token +  │                    │
     │  Refresh Token   │                    │
     │<─────────────────│                    │
     │                  │                    │
```

### 2. API 요청 (다른 서비스)

```
┌─────────┐      ┌─────────────┐      ┌─────────────┐
│ Client  │      │ Your Service│      │   Redis     │
└────┬────┘      └──────┬──────┘      └──────┬──────┘
     │                  │                    │
     │ GET /api/xxx     │                    │
     │ Authorization:   │                    │
     │ Bearer {token}   │                    │
     │─────────────────>│                    │
     │                  │                    │
     │                  │ 블랙리스트 확인     │
     │                  │───────────────────>│
     │                  │<───────────────────│
     │                  │                    │
     │                  │ JWT 서명 검증       │
     │                  │ (로컬 처리)         │
     │                  │                    │
     │   Response       │                    │
     │<─────────────────│                    │
     │                  │                    │
```

### 3. 토큰 갱신

```
┌─────────┐      ┌─────────────┐
│ Client  │      │ Auth Service│
└────┬────┘      └──────┬──────┘
     │                  │
     │ POST /auth/refresh
     │ Refresh Token    │
     │─────────────────>│
     │                  │
     │ New Access Token │
     │<─────────────────│
     │                  │
```

---

## 의존성 추가

### build.gradle.kts

```kotlin
dependencies {
    // JWT 인증 (필수)
    implementation("lotecs:lotecs-auth-spring-boot-starter")

    // gRPC 클라이언트 SDK (Auth API 호출 시)
    implementation("lotecs.auth:lotecs-auth-grpc-sdk:1.0.0-SNAPSHOT")

    // 멀티테넌시 (필요 시)
    implementation("lotecs:lotecs-tenant-spring-boot-starter")
}
```

### build.gradle (Groovy)

```groovy
dependencies {
    // JWT 인증 (필수)
    implementation 'lotecs:lotecs-auth-spring-boot-starter'

    // gRPC 클라이언트 SDK (Auth API 호출 시)
    implementation 'lotecs.auth:lotecs-auth-grpc-sdk:1.0.0-SNAPSHOT'

    // 멀티테넌시 (필요 시)
    implementation 'lotecs:lotecs-tenant-spring-boot-starter'
}
```

### 포함되는 모듈

`lotecs-auth-spring-boot-starter`는 다음 모듈을 포함한다:

| 모듈 | 기능 |
|------|------|
| lotecs-jwt-core | JWT 토큰 생성/검증 |
| lotecs-jwt-web | HTTP 요청 JWT 필터 |
| lotecs-jwt-redis | 토큰 블랙리스트 (Redis) |
| lotecs-security-web | 보안 필터 |

---

## 설정

### application.yml

```yaml
# JWT 설정
lotecs:
  jwt:
    enabled: true
    secret: ${LOTECS_JWT_SECRET}  # Auth 서비스와 동일한 키 사용 (필수)
    issuer: "lotecs-platform"
    audience: "lotecs-services"
    clock-skew-seconds: 30

    # 블랙리스트 (로그아웃 토큰 차단)
    blacklist:
      enabled: true
      storage: redis
      redis:
        key-prefix: "lotecs:jwt:blacklist:"

    # 웹 필터 설정
    web:
      enabled: true
      filter:
        exclude-paths:  # JWT 검증 제외 경로
          - /api/public/**
          - /actuator/**
          - /health
        token-expired-header:
          enabled: true
          header-name: X-Token-Expired

      # JWT에서 권한 추출
      authority-claim:
        enabled: true
        claim-key: "authorities"
        merge-with-roles: true

# gRPC 클라이언트 설정 (Auth API 호출 시)
grpc:
  client:
    auth-service:
      address: static://${AUTH_SERVICE_HOST:localhost}:${AUTH_SERVICE_GRPC_PORT:9110}
      negotiationType: ${GRPC_NEGOTIATION_TYPE:plaintext}

# Redis 설정 (블랙리스트용)
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}

# 멀티테넌시 설정 (필요 시)
lotecs:
  tenant:
    enabled: true
    default-tenant-id: SYSTEM
    web:
      enabled: true
      filter:
        order: 1
      resolver:
        header:
          enabled: true
          header-name: X-Tenant-Id
```

### 환경변수

| 변수 | 설명 | 필수 |
|------|------|------|
| LOTECS_JWT_SECRET | JWT 서명 키 (Auth 서비스와 동일) | O |
| REDIS_HOST | Redis 호스트 | O |
| REDIS_PORT | Redis 포트 | - |
| AUTH_SERVICE_HOST | Auth gRPC 서버 호스트 | gRPC 사용 시 |
| AUTH_SERVICE_GRPC_PORT | Auth gRPC 서버 포트 (기본: 9110) | - |

---

## JWT 인증 연동

### SecurityConfig 설정

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            );

        // JWT 필터는 lotecs-jwt-web에서 자동 등록됨
        return http.build();
    }
}
```

### 인증된 사용자 정보 접근

```java
@RestController
@RequestMapping("/api")
public class MyController {

    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(Authentication authentication) {
        // JWT 클레임에서 추출된 사용자 정보
        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;

        return Map.of(
            "userId", token.getUserId(),
            "username", token.getName(),
            "tenantId", token.getTenantId(),
            "authorities", token.getAuthorities()
        );
    }

    // 또는 @AuthenticationPrincipal 사용
    @GetMapping("/profile")
    public UserDto getProfile(@AuthenticationPrincipal JwtUserDetails userDetails) {
        return UserDto.builder()
            .userId(userDetails.getUserId())
            .username(userDetails.getUsername())
            .tenantId(userDetails.getTenantId())
            .build();
    }
}
```

### JWT 필터 제외 경로 추가

특정 경로를 JWT 검증에서 제외하려면:

```yaml
lotecs:
  jwt:
    web:
      filter:
        exclude-paths:
          - /api/public/**
          - /api/webhook/**
          - /health
          - /actuator/**
```

---

## SSO 연동

LOTECS Auth는 다양한 SSO(Single Sign-On) 프로토콜을 지원한다. 테넌트별로 SSO 설정을 다르게 구성할 수 있다.

### 지원 SSO 유형

| SSO 유형 | 설명 | 사용 사례 |
|----------|------|-----------|
| INTERNAL | 내부 인증 (DB 기반) | 기본 인증, SSO 없이 사용 시 |
| KEYCLOAK | Keycloak OIDC | Keycloak 서버 연동 |
| LDAP | LDAP/Active Directory | 기업 AD 연동 |
| JWT_SSO | JWT 토큰 기반 SSO | 외부 시스템 JWT 토큰 검증 |
| CAS | CAS 프로토콜 | 대학교 CAS 서버 연동 |
| REST_TOKEN | REST API 토큰 인증 | 시스템 토큰 + 사용자 검증 방식 |
| HTTP_FORM | HTTP Form 기반 인증 | GET 요청 기반 SSO |

### SSO 설정 구조

`ATH_TENANT_SSO_CONFIG` 테이블에서 테넌트별 SSO 설정을 관리한다.

```java
TenantSsoConfig ssoConfig = TenantSsoConfig.builder()
    .tenantId("TENANT_001")
    .ssoType(SsoType.JWT_SSO)
    .ssoEnabled(true)
    .ssoServerUrl("https://sso.example.com")
    // SSO 유형별 추가 설정
    .build();
```

### SSO 유형별 설정

#### INTERNAL (기본)

내부 DB 인증을 사용하며, SSO 관련 설정이 필요 없다.

```yaml
# application.yml 별도 설정 불필요
```

#### KEYCLOAK

```java
TenantSsoConfig.builder()
    .ssoType(SsoType.KEYCLOAK)
    .ssoEnabled(true)
    .ssoServerUrl("https://keycloak.example.com")
    .ssoClientId("my-client")
    .ssoClientSecret("client-secret")
    .ssoRealm("my-realm")
    .build();
```

| 필드 | 설명 | 필수 |
|------|------|------|
| ssoServerUrl | Keycloak 서버 URL | O |
| ssoClientId | 클라이언트 ID | O |
| ssoClientSecret | 클라이언트 시크릿 | O |
| ssoRealm | Realm 이름 | O |

#### JWT_SSO

외부 시스템에서 발급한 JWT 토큰을 검증한다.

```java
TenantSsoConfig.builder()
    .ssoType(SsoType.JWT_SSO)
    .ssoEnabled(true)
    .ssoServerUrl("https://sso.example.com")
    .jwtSecretKey("your-secret-key-min-32-chars")
    .jwtAgentId("agent-001")
    .jwtExpirationSeconds(3600)
    .loginEndpoint("/sso/login")
    .logoutEndpoint("/sso/logout")
    .build();
```

| 필드 | 설명 | 필수 |
|------|------|------|
| jwtSecretKey | JWT 서명 키 (최소 32자) | O |
| jwtAgentId | 에이전트 ID | - |
| jwtExpirationSeconds | 토큰 유효기간 (초) | - |
| loginEndpoint | 로그인 엔드포인트 | - |
| logoutEndpoint | 로그아웃 엔드포인트 | - |

#### CAS

CAS 프로토콜 기반 인증을 제공한다.

```java
TenantSsoConfig.builder()
    .ssoType(SsoType.CAS)
    .ssoEnabled(true)
    .ssoServerUrl("https://cas.example.edu")
    .casValidateEndpoint("/serviceValidate")
    .casServiceUrl("https://myapp.example.com/callback")
    .loginEndpoint("/login")
    .logoutEndpoint("/logout")
    .readTimeoutMs(5000)
    .build();
```

| 필드 | 설명 | 필수 |
|------|------|------|
| ssoServerUrl | CAS 서버 URL | O |
| casValidateEndpoint | 티켓 검증 엔드포인트 | O |
| casServiceUrl | 서비스 콜백 URL | O |
| loginEndpoint | 로그인 엔드포인트 | - |
| logoutEndpoint | 로그아웃 엔드포인트 | - |
| readTimeoutMs | HTTP 타임아웃 (ms) | - |

**CAS 인증 흐름**:
```
1. 사용자 -> 서비스: 접근 요청
2. 서비스 -> CAS: 로그인 페이지 리다이렉트
3. 사용자 -> CAS: 로그인
4. CAS -> 서비스: 서비스 티켓과 함께 콜백
5. 서비스 -> CAS: 티켓 검증 (serviceValidate)
6. CAS -> 서비스: 사용자 정보 (XML)
```

#### REST_TOKEN

시스템 토큰을 획득한 후 사용자 검증을 수행하는 방식이다.

```java
TenantSsoConfig.builder()
    .ssoType(SsoType.REST_TOKEN)
    .ssoEnabled(true)
    .ssoServerUrl("https://api.example.com")
    .ssoClientId("client-id")
    .ssoClientSecret("client-secret")
    .restTokenEndpoint("/oauth/token")
    .restConnectEndpoint("/api/user/verify")
    .restCreateState("CREATE_STATE_VALUE")
    .restVerifyState("VERIFY_STATE_VALUE")
    .readTimeoutMs(10000)
    .additionalConfig("""
        {
            "userDivisionMapping": {
                "A": "STUDENT",
                "B": "STAFF"
            },
            "userDivisionPrefix": {
                "STUDENT": "S_",
                "STAFF": "E_"
            }
        }
        """)
    .build();
```

| 필드 | 설명 | 필수 |
|------|------|------|
| ssoServerUrl | API 서버 URL | O |
| ssoClientId | 클라이언트 ID | O |
| ssoClientSecret | 클라이언트 시크릿 | O |
| restTokenEndpoint | 토큰 발급 엔드포인트 | O |
| restConnectEndpoint | 사용자 검증 엔드포인트 | O |
| restCreateState | 토큰 요청 상태값 | - |
| restVerifyState | 검증 요청 상태값 | - |
| additionalConfig | 추가 설정 (JSON) | - |

**REST_TOKEN 인증 흐름**:
```
1. 서비스 -> SSO서버: 시스템 토큰 요청 (client credentials)
2. SSO서버 -> 서비스: 시스템 토큰 발급
3. 서비스 -> SSO서버: 사용자 검증 요청 (시스템 토큰 + 사용자 정보)
4. SSO서버 -> 서비스: 검증 결과 및 사용자 정보
```

#### HTTP_FORM

HTTP GET 요청을 통한 폼 기반 인증이다.

```java
TenantSsoConfig.builder()
    .ssoType(SsoType.HTTP_FORM)
    .ssoEnabled(true)
    .ssoServerUrl("https://auth.example.com")
    .httpFormConfirmEndpoint("/confirm")
    .httpFormIdParam("userId")
    .httpFormPasswordParam("userPw")
    .httpFormEncodePassword(true)
    .readTimeoutMs(5000)
    .build();
```

| 필드 | 설명 | 필수 |
|------|------|------|
| ssoServerUrl | 인증 서버 URL | O |
| httpFormConfirmEndpoint | 인증 확인 엔드포인트 | O |
| httpFormIdParam | 사용자 ID 파라미터명 | O |
| httpFormPasswordParam | 비밀번호 파라미터명 | O |
| httpFormEncodePassword | 비밀번호 인코딩 여부 | - |
| readTimeoutMs | HTTP 타임아웃 (ms) | - |

### Fallback 인증

SSO 서버 장애 시 내부 인증으로 폴백할 수 있다.

```java
TenantSsoConfig.builder()
    .ssoType(SsoType.JWT_SSO)
    .ssoEnabled(true)
    // ... SSO 설정 ...
    .fallbackEnabled(true)           // Fallback 활성화
    .fallbackPasswordRequired(true)  // 비밀번호 필수 여부
    .build();
```

| 필드 | 설명 | 기본값 |
|------|------|--------|
| fallbackEnabled | Fallback 활성화 여부 | false |
| fallbackPasswordRequired | Fallback 시 비밀번호 필수 | true |

**Fallback 동작**:
1. SSO 인증 시도
2. SSO 실패 또는 타임아웃 발생
3. `fallbackEnabled=true`면 내부 DB 인증 시도
4. `fallbackPasswordRequired=true`면 비밀번호 검증, false면 사용자 존재 여부만 확인

### SSO 인증 API

#### 로그인 요청

```http
POST /auth/login HTTP/1.1
Content-Type: application/json
X-Tenant-Id: TENANT_001

{
    "username": "user@example.com",
    "password": "password123",
    "ssoToken": "external-sso-token"  // JWT_SSO, CAS 등에서 사용
}
```

#### 응답

```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 900
}
```

### SSO 연동 시 주의사항

1. **비밀키 관리**: `jwtSecretKey`, `ssoClientSecret` 등은 환경변수로 관리
2. **타임아웃 설정**: 외부 SSO 서버 응답 지연을 고려하여 적절한 타임아웃 설정
3. **Fallback 전략**: 중요 서비스는 `fallbackEnabled=true` 권장
4. **사용자 동기화**: `userSyncEnabled=true` 설정 시 SSO 사용자 정보 자동 동기화
5. **역할 매핑**: `roleMappingEnabled=true` 설정 시 SSO 역할을 내부 역할에 매핑

---

## gRPC 클라이언트 사용

### 클라이언트 주입

```java
@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthServiceClient authServiceClient;
    private final PermissionServiceClient permissionServiceClient;
    private final RoleServiceClient roleServiceClient;

    // ...
}
```

### 사용자 조회

```java
public UserDto getUserById(String userId) {
    GetUserByIdRequest request = GetUserByIdRequest.newBuilder()
        .setUserId(userId)
        .build();

    UserResponse response = authServiceClient.getUserById(request);

    return UserDto.builder()
        .userId(response.getUserId())
        .username(response.getUsername())
        .email(response.getEmail())
        .tenantId(response.getTenantId())
        .build();
}
```

### 토큰 검증

```java
public boolean validateToken(String accessToken) {
    ValidateTokenRequest request = ValidateTokenRequest.newBuilder()
        .setAccessToken(accessToken)
        .build();

    ValidateTokenResponse response = authServiceClient.validateToken(request);
    return response.getValid();
}
```

### 권한 체크 (gRPC)

```java
public boolean checkPermission(String userId, String tenantId, String permissionCode) {
    PermissionCheckRequest request = PermissionCheckRequest.newBuilder()
        .setUserId(userId)
        .setTenantId(tenantId)
        .setPermissionCode(permissionCode)
        .build();

    PermissionCheckResponse response = authServiceClient.checkPermission(request);
    return response.getHasPermission();
}
```

### 사용자 역할 조회

```java
public List<RoleDto> getUserRoles(String userId, String tenantId) {
    GetUserRolesRequest request = GetUserRolesRequest.newBuilder()
        .setUserId(userId)
        .setTenantId(tenantId)
        .build();

    GetUserRolesResponse response = roleServiceClient.getUserRoles(request);

    return response.getRolesList().stream()
        .map(role -> RoleDto.builder()
            .roleId(role.getRoleId())
            .roleName(role.getRoleName())
            .roleCode(role.getRoleCode())
            .build())
        .toList();
}
```

---

## 권한 체크

### 어노테이션 기반 (권장)

JWT의 `authorities` 클레임에 포함된 권한으로 접근 제어:

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    // 특정 권한 필요
    @PreAuthorize("hasAuthority('USER_VIEW')")
    @GetMapping
    public List<UserDto> getUsers() {
        // ...
    }

    // 여러 권한 중 하나
    @PreAuthorize("hasAnyAuthority('USER_EDIT', 'USER_ADMIN')")
    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable String id, @RequestBody UserDto dto) {
        // ...
    }

    // 역할 기반
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        // ...
    }

    // 복합 조건
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_DELETE')")
    @DeleteMapping("/batch")
    public void deleteUsers(@RequestBody List<String> ids) {
        // ...
    }

    // 사용자 본인 확인
    @PreAuthorize("#userId == authentication.principal.userId or hasRole('ADMIN')")
    @GetMapping("/{userId}/profile")
    public UserProfileDto getProfile(@PathVariable String userId) {
        // ...
    }
}
```

### 프로그래밍 방식

```java
@Service
@RequiredArgsConstructor
public class ReportService {

    private final AuthServiceClient authServiceClient;

    public ReportDto generateReport(String reportType) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtAuthenticationToken token = (JwtAuthenticationToken) auth;

        // 1. 로컬 권한 체크 (JWT 클레임 기반)
        boolean hasLocalAuthority = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("REPORT_GENERATE"));

        // 2. gRPC를 통한 실시간 권한 체크 (최신 권한 확인 필요 시)
        boolean hasRemotePermission = authServiceClient.checkPermission(
            token.getUserId(),
            token.getTenantId(),
            "REPORT_GENERATE"
        );

        if (!hasLocalAuthority && !hasRemotePermission) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        // 보고서 생성 로직
        return generateReportInternal(reportType);
    }
}
```

### 권한 체크 방식 비교

| 방식 | 장점 | 단점 | 사용 시점 |
|------|------|------|-----------|
| JWT 클레임 (@PreAuthorize) | 빠름 (네트워크 호출 없음) | 토큰 발급 후 권한 변경 미반영 | 일반적인 경우 |
| gRPC 실시간 조회 | 항상 최신 권한 | 네트워크 지연 | 중요한 작업, 권한 변경 즉시 반영 필요 시 |

---

## 멀티테넌시

### 테넌트 ID 전달

클라이언트는 요청 시 `X-Tenant-Id` 헤더로 테넌트를 지정한다:

```http
GET /api/users HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
X-Tenant-Id: TENANT_001
```

### 테넌트 컨텍스트 접근

```java
@Service
public class DataService {

    public List<DataDto> getData() {
        // 현재 테넌트 ID 조회
        String tenantId = TenantContextHolder.getTenantId();

        // 테넌트별 데이터 조회
        return dataRepository.findByTenantId(tenantId);
    }
}
```

### 테넌트 검증

JWT의 `tenantId`와 요청 헤더의 `X-Tenant-Id`가 일치하는지 자동 검증된다. 불일치 시 403 Forbidden 응답.

### gRPC 호출 시 테넌트 전달

```java
public List<UserDto> getTenantUsers() {
    String tenantId = TenantContextHolder.getTenantId();

    GetUsersRequest request = GetUsersRequest.newBuilder()
        .setTenantId(tenantId)
        .setPageSize(100)
        .build();

    return authServiceClient.getUsers(request)
        .getUsersList()
        .stream()
        .map(this::toDto)
        .toList();
}
```

---

## 트러블슈팅

### 1. JWT 검증 실패

**증상**: 401 Unauthorized 응답

**원인 및 해결**:

| 원인 | 해결 |
|------|------|
| JWT Secret 불일치 | `LOTECS_JWT_SECRET` 환경변수가 Auth 서비스와 동일한지 확인 |
| 토큰 만료 | Access Token 갱신 (POST /auth/refresh) |
| 토큰 블랙리스트 등록 | 재로그인 필요 |
| Redis 연결 실패 | Redis 연결 상태 확인 |

**디버깅**:
```yaml
logging:
  level:
    lotecs.security: DEBUG
    lotecs.jwt: DEBUG
```

### 2. 권한 없음 (403 Forbidden)

**증상**: 인증은 성공했으나 403 응답

**확인 사항**:
1. JWT에 필요한 권한이 포함되어 있는지 확인
2. `@PreAuthorize` 표현식 확인
3. 역할과 권한 구분 (`hasRole` vs `hasAuthority`)

**JWT 클레임 확인**:
```java
// JWT 디코딩하여 authorities 확인
// https://jwt.io 에서 토큰 디코딩
```

### 3. gRPC 연결 실패

**증상**: UNAVAILABLE 에러

**확인 사항**:
```bash
# Auth 서비스 gRPC 포트 확인
nc -zv {AUTH_SERVICE_HOST} 9110

# 설정 확인
grpc.client.auth-service.address=static://host:9110
```

### 4. 테넌트 불일치

**증상**: 403 Forbidden (Tenant mismatch)

**원인**: JWT의 tenantId와 X-Tenant-Id 헤더 불일치

**해결**: 요청 헤더의 테넌트 ID가 로그인한 사용자의 테넌트와 일치하는지 확인

### 5. 토큰 만료 감지

응답 헤더에 `X-Token-Expired: true`가 포함되면 토큰이 곧 만료됨을 의미한다. 클라이언트는 이 헤더를 감지하여 토큰을 미리 갱신할 수 있다.

```javascript
// 클라이언트 예시
axios.interceptors.response.use(response => {
    if (response.headers['x-token-expired'] === 'true') {
        // 토큰 갱신 로직
        refreshToken();
    }
    return response;
});
```

---

## 참고

- [LOTECS Auth README](../README.md)
- [gRPC Proto 정의](../lotecs-auth-grpc-api/src/main/proto/auth_service.proto)
- [lotecs-framework-common Security 모듈](../../../../lotecs-framework-common/security/)
