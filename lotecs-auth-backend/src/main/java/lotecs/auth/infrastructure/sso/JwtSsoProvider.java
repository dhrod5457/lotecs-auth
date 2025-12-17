package lotecs.auth.infrastructure.sso;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.sso.SsoAuthRequest;
import lotecs.auth.domain.sso.SsoAuthResult;
import lotecs.auth.domain.sso.SsoProvider;
import lotecs.auth.domain.sso.SsoType;
import lotecs.auth.domain.sso.model.TenantSsoConfig;
import lotecs.auth.domain.sso.repository.TenantSsoConfigRepository;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 토큰 기반 SSO 제공자.
 * 자체 JWT를 생성하여 외부 SSO 서버와 연동한다.
 */
@Slf4j
@Component("jwt_sso")
public class JwtSsoProvider implements SsoProvider {

    private final TenantSsoConfigRepository ssoConfigRepository;
    private final Map<String, SecretKey> keyCache = new HashMap<>();

    public JwtSsoProvider(TenantSsoConfigRepository ssoConfigRepository) {
        this.ssoConfigRepository = ssoConfigRepository;
        log.debug("JwtSsoProvider initialized");
    }

    @Override
    public SsoAuthResult authenticate(SsoAuthRequest request) {
        log.debug("JWT SSO authentication for user: {} in tenant: {}", request.getUsername(), request.getTenantId());

        try {
            TenantSsoConfig config = ssoConfigRepository.findByTenantId(request.getTenantId())
                    .orElseThrow(() -> new IllegalStateException("SSO configuration not found for tenant: " + request.getTenantId()));

            if (config.getJwtSecretKey() == null || config.getJwtSecretKey().isBlank()) {
                log.error("JWT secret key is not configured for tenant: {}", request.getTenantId());
                return SsoAuthResult.failure("CONFIG_ERROR", "JWT secret key is not configured");
            }

            // SSO 토큰이 있으면 검증
            if (request.getSsoToken() != null && !request.getSsoToken().isBlank()) {
                return validateToken(request.getSsoToken(), config);
            }

            // SSO 토큰이 없으면 JWT 생성 후 반환 (로그인 흐름)
            String token = createJwtToken(request, config);
            log.info("JWT token created for user: {} in tenant: {}", request.getUsername(), request.getTenantId());

            return SsoAuthResult.success(
                    request.getUsername(),
                    request.getUsername(),
                    null,
                    null,
                    Collections.emptyList(),
                    Map.of("accessToken", token)
            );

        } catch (IllegalStateException e) {
            log.error("Configuration error: {}", e.getMessage());
            return SsoAuthResult.failure("CONFIG_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("JWT SSO authentication failed for user {} in tenant {}: {}",
                    request.getUsername(), request.getTenantId(), e.getMessage(), e);
            return SsoAuthResult.failure("AUTH_ERROR", "JWT 인증 실패: " + e.getMessage());
        }
    }

    private SsoAuthResult validateToken(String ssoToken, TenantSsoConfig config) {
        try {
            SecretKey key = getOrCreateSignKey(config.getTenantId(), config.getJwtSecretKey());

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(ssoToken)
                    .getPayload();

            String userId = claims.get("userId", String.class);
            String userName = claims.get("userName", String.class);
            String userType = claims.get("userType", String.class);
            String department = claims.get("department", String.class);
            String email = claims.get("email", String.class);

            // 만료 확인
            Date expiration = claims.getExpiration();
            if (expiration != null && expiration.before(new Date())) {
                return SsoAuthResult.failure("TOKEN_EXPIRED", "JWT 토큰이 만료되었습니다");
            }

            log.debug("JWT validation successful for user: {}", userId);
            return SsoAuthResult.success(
                    userId,
                    userName,
                    email,
                    userName,
                    Collections.emptyList(),
                    Map.of("userType", userType != null ? userType : "", "department", department != null ? department : "")
            );

        } catch (Exception e) {
            log.error("JWT validation failed", e);
            return SsoAuthResult.failure("TOKEN_INVALID", "JWT 토큰 검증 실패: " + e.getMessage());
        }
    }

    private String createJwtToken(SsoAuthRequest request, TenantSsoConfig config) {
        SecretKey key = getOrCreateSignKey(config.getTenantId(), config.getJwtSecretKey());

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", request.getUsername());
        if (config.getJwtAgentId() != null) {
            claims.put("serviceId", config.getJwtAgentId());
        }

        if (request.getExtraParams() != null) {
            claims.putAll(request.getExtraParams());
        }

        Date now = new Date();
        int expirationSeconds = config.getJwtExpirationSeconds() != null ? config.getJwtExpirationSeconds() : 3600;
        Date expiration = new Date(now.getTime() + (expirationSeconds * 1000L));

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    private SecretKey getOrCreateSignKey(String tenantId, String secretKey) {
        return keyCache.computeIfAbsent(tenantId, k ->
                Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public String buildLoginUrl(String callbackUrl) {
        // 기본 구현 - 실제 사용 시 테넌트별 설정 필요
        String encodedCallback = URLEncoder.encode(callbackUrl, StandardCharsets.UTF_8);
        return "/sso/jwt/login?service=" + encodedCallback;
    }

    @Override
    public String buildLogoutUrl(String callbackUrl) {
        String encodedCallback = URLEncoder.encode(callbackUrl, StandardCharsets.UTF_8);
        return "/sso/jwt/logout?service=" + encodedCallback;
    }

    @Override
    public SsoType getSsoType() {
        return SsoType.JWT_SSO;
    }
}
