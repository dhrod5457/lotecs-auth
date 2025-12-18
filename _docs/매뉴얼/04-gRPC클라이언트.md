# gRPC 클라이언트 사용

LOTECS Auth의 gRPC API를 사용하여 인증/인가 기능을 호출하는 방법을 설명한다.

## 클라이언트 주입

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

---

## 사용자 조회

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

---

## 토큰 검증

```java
public boolean validateToken(String accessToken) {
    ValidateTokenRequest request = ValidateTokenRequest.newBuilder()
        .setAccessToken(accessToken)
        .build();

    ValidateTokenResponse response = authServiceClient.validateToken(request);
    return response.getValid();
}
```

---

## 권한 체크 (gRPC)

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

---

## 사용자 역할 조회

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

## gRPC 클라이언트 설정

### application.yml

```yaml
grpc:
  client:
    auth-service:
      address: static://${AUTH_SERVICE_HOST:localhost}:${AUTH_SERVICE_GRPC_PORT:50053}
      negotiationType: ${GRPC_NEGOTIATION_TYPE:plaintext}
```

### 환경변수

| 변수 | 설명 | 기본값 |
|------|------|--------|
| AUTH_SERVICE_HOST | Auth gRPC 서버 호스트 | localhost |
| AUTH_SERVICE_GRPC_PORT | Auth gRPC 서버 포트 | 50053 |
| GRPC_NEGOTIATION_TYPE | 통신 방식 (plaintext/tls) | plaintext |
