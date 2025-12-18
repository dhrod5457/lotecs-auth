# SSO 연동

LOTECS Auth는 다양한 SSO(Single Sign-On) 프로토콜을 지원한다. 테넌트별로 SSO 설정을 다르게 구성할 수 있다.

## 지원 SSO 유형

| SSO 유형 | 설명 | 사용 사례 |
|----------|------|-----------|
| INTERNAL | 내부 인증 (DB 기반) | 기본 인증, SSO 없이 사용 시 |
| KEYCLOAK | Keycloak OIDC | Keycloak 서버 연동 |
| LDAP | LDAP/Active Directory | 기업 AD 연동 |
| JWT_SSO | JWT 토큰 기반 SSO | 외부 시스템 JWT 토큰 검증 |
| CAS | CAS 프로토콜 | 대학교 CAS 서버 연동 |
| REST_TOKEN | REST API 토큰 인증 | 시스템 토큰 + 사용자 검증 방식 |
| HTTP_FORM | HTTP Form 기반 인증 | GET 요청 기반 SSO |

---

## SSO 설정 구조

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

---

## SSO 유형별 설정

### INTERNAL (기본)

내부 DB 인증을 사용하며, SSO 관련 설정이 필요 없다.

```yaml
# application.yml 별도 설정 불필요
```

### KEYCLOAK

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

### JWT_SSO

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

### CAS

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

### REST_TOKEN

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

### HTTP_FORM

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

---

## Fallback 인증

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

---

## SSO 인증 API

### 로그인 요청

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

### 응답

```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 900
}
```

---

## SSO 연동 시 주의사항

1. **비밀키 관리**: `jwtSecretKey`, `ssoClientSecret` 등은 환경변수로 관리
2. **타임아웃 설정**: 외부 SSO 서버 응답 지연을 고려하여 적절한 타임아웃 설정
3. **Fallback 전략**: 중요 서비스는 `fallbackEnabled=true` 권장
4. **사용자 동기화**: `userSyncEnabled=true` 설정 시 SSO 사용자 정보 자동 동기화
5. **역할 매핑**: `roleMappingEnabled=true` 설정 시 SSO 역할을 내부 역할에 매핑

---

## SSO Admin API

테넌트별 SSO 설정을 관리하는 REST API를 제공한다.

### SSO 설정 조회

```http
GET /admin/sso/config/{tenantId} HTTP/1.1
Authorization: Bearer {admin_token}
```

**응답**:
```json
{
    "code": "SUCCESS",
    "data": {
        "tenantId": "TENANT_001",
        "ssoType": "KEYCLOAK",
        "ssoEnabled": true,
        "relayEndpoint": "https://relay.example.com",
        "relayTimeoutMs": 5000,
        "ssoServerUrl": "https://keycloak.example.com",
        "ssoRealm": "my-realm",
        "ssoClientId": "my-client",
        "userSyncEnabled": true,
        "roleMappingEnabled": true
    }
}
```

### SSO 설정 수정

```http
PUT /admin/sso/config/{tenantId} HTTP/1.1
Authorization: Bearer {admin_token}
Content-Type: application/json

{
    "ssoType": "KEYCLOAK",
    "ssoEnabled": true,
    "ssoServerUrl": "https://keycloak.example.com",
    "ssoRealm": "my-realm",
    "ssoClientId": "new-client-id",
    "userSyncEnabled": true,
    "roleMappingEnabled": false
}
```

### SSO 연결 테스트

```http
POST /admin/sso/test?tenantId=TENANT_001 HTTP/1.1
Authorization: Bearer {admin_token}
```

**응답**:
```json
{
    "code": "SUCCESS",
    "data": {
        "success": true,
        "message": "SSO connection test successful"
    }
}
```

---

## Relay 서버 연동

LOTECS Auth는 Relay 서버와 연동하여 조직 정보와 사용자-조직 매핑을 동기화할 수 있다.

### Relay 서버 역할

```
┌─────────────┐     gRPC      ┌─────────────┐     동기화     ┌─────────────┐
│   Relay     │──────────────>│ LOTECS Auth │<──────────────>│   Client    │
│   Server    │               │   Service   │                │   Service   │
└─────────────┘               └─────────────┘                └─────────────┘
      │                              │
      │ - 사용자 인증 (SSO)          │ - 조직 정보 저장
      │ - 조직 정보 제공             │ - 사용자-조직 매핑
      │ - 사용자 정보 제공           │ - JWT 토큰 발급
      │                              │
```

### Relay 설정

```yaml
# application.yml
auth:
  sso:
    relay:
      enabled: true
      default-timeout-ms: 5000
      keepalive-time-seconds: 30
      keepalive-timeout-seconds: 10
```

| 설정 | 설명 | 기본값 |
|------|------|--------|
| enabled | Relay SSO 활성화 | true |
| default-timeout-ms | 기본 타임아웃 (ms) | 5000 |
| keepalive-time-seconds | Keep-alive 시간 (초) | 30 |
| keepalive-timeout-seconds | Keep-alive 타임아웃 (초) | 10 |

### 조직 동기화 API (gRPC)

Relay 서버에서 조직 정보를 동기화할 때 사용하는 gRPC API:

**OrganizationService**

| RPC | 설명 |
|-----|------|
| GetOrganization | 조직 조회 |
| ListOrganizations | 조직 목록 조회 |
| SyncOrganization | 조직 동기화 (생성/수정) |
| DeleteOrganization | 조직 삭제 |

**UserOrganizationService**

| RPC | 설명 |
|-----|------|
| GetUserOrganizations | 사용자의 조직 목록 조회 |
| GetPrimaryUserOrganization | 사용자의 주 소속 조회 |
| SyncUserOrganization | 사용자-조직 매핑 동기화 |
| DeleteUserOrganization | 사용자-조직 매핑 삭제 |

### 조직 동기화 흐름

```
1. Relay -> Auth: SyncOrganization(조직 정보)
2. Auth: 조직 존재 여부 확인
   - 없으면 신규 생성
   - 있으면 정보 업데이트
3. Auth -> Relay: 동기화 결과 반환

4. Relay -> Auth: SyncUserOrganization(사용자-조직 매핑)
5. Auth: 매핑 존재 여부 확인
   - 없으면 신규 생성
   - 있으면 정보 업데이트
6. Auth -> Relay: 동기화 결과 반환
```

### 조직 데이터 구조

```json
{
    "organizationId": "ORG_001",
    "tenantId": "TENANT_001",
    "organizationCode": "DEPT_IT",
    "organizationName": "IT 부서",
    "organizationType": "DEPARTMENT",
    "parentOrganizationId": "ORG_ROOT",
    "orgLevel": 2,
    "displayOrder": 1,
    "active": true
}
```

### 사용자-조직 매핑 데이터 구조

```json
{
    "userId": "USER_001",
    "organizationId": "ORG_001",
    "tenantId": "TENANT_001",
    "roleId": "ROLE_001",
    "isPrimary": true,
    "position": "팀장",
    "startDate": "2024-01-01",
    "endDate": null,
    "active": true
}
```
