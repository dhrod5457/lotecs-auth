# JWT 인증 연동

LOTECS Auth의 JWT 인증을 연동하는 방법을 설명한다.

## SecurityConfig 설정

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

---

## 인증된 사용자 정보 접근

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

---

## JWT 필터 제외 경로 추가

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
