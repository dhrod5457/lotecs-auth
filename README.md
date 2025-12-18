# LOTECS Auth Service

LOTECS 플랫폼의 인증/인가 서비스. JWT 기반 토큰 인증, 멀티테넌시, SSO 연동, 역할/권한 관리 기능을 제공한다.

## 모듈 구조

```
lotecs-auth/
├── lotecs-auth-backend/      # Spring Boot 백엔드 서비스
├── lotecs-auth-grpc-api/     # Protocol Buffers 정의 및 gRPC 코드 생성
├── lotecs-auth-grpc-sdk/     # 다른 서비스에서 사용할 gRPC 클라이언트 SDK
└── _sql/                     # 데이터베이스 스키마 및 초기 데이터
```

## 기술 스택

- Java 21
- Spring Boot 3.x
- Oracle Database
- Redis (토큰 블랙리스트, 세션 관리)
- gRPC (서비스 간 통신)
- MyBatis (ORM)

## 주요 기능

### 인증 (Authentication)
- 로그인/로그아웃
- JWT Access Token / Refresh Token 발급
- 토큰 갱신 및 검증
- 토큰 블랙리스트 (Redis)

### SSO 연동
- Keycloak 연동
- LDAP 연동
- JWT SSO 연동
- CAS 연동
- REST Token 연동
- HTTP Form 연동

### 사용자 관리
- 사용자 CRUD
- 계정 잠금/해제
- 비밀번호 변경

### 역할/권한 관리
- 역할(Role) CRUD
- 권한(Permission) CRUD
- 역할-권한 매핑
- 사용자-역할 할당

### 멀티테넌시
- 테넌트 CRUD
- 테넌트별 SSO 설정
- 테넌트별 사용자/역할/권한 격리

### 조직 관리
- 조직 계층 구조
- 사용자-조직 매핑
- gRPC를 통한 외부 시스템과 조직 동기화

## API

### REST API

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | /auth/login | 로그인 |
| POST | /auth/logout | 로그아웃 |
| POST | /auth/refresh | 토큰 갱신 |
| POST | /auth/validate | 토큰 검증 |
| POST | /admin/users | 사용자 생성 |
| GET | /admin/users/{userId} | 사용자 조회 |
| PUT | /admin/users/{userId} | 사용자 수정 |
| DELETE | /admin/users/{userId} | 사용자 삭제 |
| GET | /admin/users | 사용자 목록 |
| GET | /admin/sso/config/{tenantId} | SSO 설정 조회 |
| PUT | /admin/sso/config/{tenantId} | SSO 설정 수정 |
| POST | /admin/sso/test | SSO 연결 테스트 |

### gRPC API

서비스 정의는 `lotecs-auth-grpc-api/src/main/proto/auth_service.proto` 참조.

**AuthService**
- Login, Logout, RefreshToken, ValidateToken
- GetUserById, GetUserByUsername, GetUsers
- CreateUser, UpdateUser, DeleteUser
- CheckPermission
- AssignRole, AssignRoles, RevokeRole
- LockUser, UnlockUser, ChangePassword

**TenantService**
- GetTenant, GetTenantBySiteCode, ListTenants
- CreateTenant, UpdateTenant, DeleteTenant
- PublishTenant, UnpublishTenant

**RoleService**
- GetRole, GetRoleByName, ListRoles
- CreateRole, UpdateRole, DeleteRole
- GetUserRoles

**PermissionService**
- GetPermission, ListPermissions
- CreatePermission, UpdatePermission, DeletePermission
- GetRolePermissions, AssignPermissionsToRole, RevokePermissionFromRole

**OrganizationService**
- GetOrganization, ListOrganizations
- SyncOrganization, DeleteOrganization

**UserOrganizationService**
- GetUserOrganizations, GetPrimaryUserOrganization
- SyncUserOrganization, DeleteUserOrganization

**RoleStatusService**
- GetRoleStatus, ListRoleStatuses
- CreateRoleStatus, UpdateRoleStatus, DeleteRoleStatus

## LOTECS Framework 모듈

이 서비스는 다음 LOTECS 공통 모듈을 사용한다:

| 모듈 | 용도 |
|------|------|
| lotecs-core | 핵심 유틸리티 |
| lotecs-jwt-core | JWT 토큰 생성/검증 |
| lotecs-jwt-web | JWT 웹 필터 |
| lotecs-jwt-redis | JWT 블랙리스트 (Redis) |
| lotecs-cache-spring-boot-starter | 캐시 |
| lotecs-crypto-spring-boot-starter | 암호화 (비밀번호 해싱) |
| lotecs-logging-spring-boot-starter | 로깅 (Loki, 파일, Slack) |
| lotecs-tenant-spring-boot-starter | 멀티테넌시 |
| lotecs-mybatis-spring-boot-starter | MyBatis 설정 |
| lotecs-exception-spring-boot-starter | 예외 처리 |
| lotecs-web-spring-boot-starter | 웹 공통 설정 |
| lotecs-ratelimit-spring-boot-starter | API Rate Limit |
| lotecs-grpc-core | gRPC 유틸리티 |

## 빌드 및 실행

### 빌드

```bash
./gradlew clean build
```

### 로컬 실행

```bash
./gradlew :lotecs-auth-backend:bootRun
```

### Docker 실행

```bash
docker build -t lotecs-auth .
docker run -p 8000:8000 -p 50053:50053 lotecs-auth
```

## 설정

### 서버 포트

| 포트 | 용도 |
|------|------|
| 8000 | HTTP REST API |
| 50053 | gRPC API |

### 환경변수

| 변수 | 설명 | 기본값 |
|------|------|--------|
| SPRING_PROFILES_ACTIVE | 활성 프로파일 | local |
| AUTH_DB_PASSWORD | DB 비밀번호 | lotecs9240 |
| REDIS_HOST | Redis 호스트 | 192.168.0.57 |
| REDIS_PORT | Redis 포트 | 6379 |
| REDIS_PASSWORD | Redis 비밀번호 | (없음) |
| LOTECS_JWT_SECRET | JWT 서명 키 | (필수) |
| CRYPTO_MASTER_KEY | 암호화 마스터 키 | (기본값) |
| LOKI_URL | Loki 서버 URL | http://192.168.0.57:3100 |
| RATE_LIMIT_ENABLED | Rate Limit 활성화 | true |

### 프로파일

- `local`: 로컬 개발 환경
- `dev`: 개발 서버
- `prod`: 운영 환경

## 데이터베이스

### 스키마 생성

```bash
# 1. 스키마 생성 (DBA 권한)
sqlplus system/password@192.168.0.57:1521/xepdb1 @_sql/01_create_schema.sql

# 2. 테이블 생성
sqlplus lotecs_auth/lotecs9240@192.168.0.57:1521/xepdb1 @_sql/02_create_tables.sql

# 3. 초기 데이터
sqlplus lotecs_auth/lotecs9240@192.168.0.57:1521/xepdb1 @_sql/03_initial_data.sql
```

### 주요 테이블

| 테이블 | 설명 |
|--------|------|
| ATH_TENANT | 테넌트 정보 |
| ATH_TENANT_SSO_CONFIG | 테넌트별 SSO 설정 |
| ATH_USERS | 사용자 정보 |
| ATH_ROLES | 역할 정보 |
| ATH_USER_ROLES | 사용자-역할 매핑 |
| ATH_PERMISSIONS | 권한 정보 |
| ATH_ROLE_PERMISSIONS | 역할-권한 매핑 |
| ATH_ROLE_STATUS | 역할 상태 코드 |
| ATH_ORGANIZATION | 조직 정보 |
| ATH_USER_ORGANIZATION | 사용자-조직 매핑 |
| ATH_EXTERNAL_USER_MAPPING | 외부 시스템 사용자 매핑 |
| ATH_REFRESH_TOKENS | Refresh Token 저장 |

## gRPC SDK 사용법

### 의존성 추가

```groovy
dependencies {
    implementation 'lotecs.auth:lotecs-auth-grpc-sdk:1.0.0-SNAPSHOT'
}
```

### 클라이언트 설정

```yaml
grpc:
  client:
    auth-service:
      address: static://localhost:50053
      negotiationType: plaintext
```

### 사용 예시

```java
@Service
@RequiredArgsConstructor
public class MyService {

    private final AuthGrpcClient authClient;

    public boolean checkPermission(String userId, String tenantId, String permission) {
        return authClient.checkPermission(userId, tenantId, permission);
    }
}
```

## 테스트

```bash
# 전체 테스트
./gradlew test

# 특정 모듈 테스트
./gradlew :lotecs-auth-backend:test
```
