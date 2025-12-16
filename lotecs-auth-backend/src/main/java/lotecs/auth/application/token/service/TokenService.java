package lotecs.auth.application.token.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.application.token.dto.TokenRefreshResult;
import lotecs.auth.domain.token.model.RefreshToken;
import lotecs.auth.domain.token.repository.RefreshTokenRepository;
import lotecs.auth.domain.user.model.Role;
import lotecs.auth.domain.user.model.User;
import lotecs.auth.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${lotecs.jwt.secret}")
    private String jwtSecret;

    @Value("${lotecs.jwt.access-token-validity-seconds}")
    private Long accessTokenValiditySeconds;

    @Value("${lotecs.jwt.refresh-token-validity-seconds}")
    private Long refreshTokenValiditySeconds;

    @Value("${lotecs.jwt.issuer:lotecs-auth}")
    private String issuer;

    /**
     * JWT Access Token 생성 (15분)
     */
    public String generateAccessToken(User user) {
        log.debug("Generating access token for user: {}", user.getUsername());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (accessTokenValiditySeconds * 1000));

        String roles = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.joining(","));

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        String token = Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getUserId())
                .claim("tenantId", user.getTenantId())
                .claim("username", user.getUsername())
                .claim("roles", roles)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();

        log.debug("Access token generated successfully for user: {}", user.getUsername());
        return token;
    }

    /**
     * Refresh Token 생성 및 DB 저장 (7일)
     */
    @Transactional
    public String generateRefreshToken(User user) {
        log.debug("Generating refresh token for user: {}", user.getUsername());

        // 기존 Refresh Token 삭제
        refreshTokenRepository.deleteByUserId(user.getUserId());

        // 새 Refresh Token 생성
        String tokenValue = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenValiditySeconds);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getUserId())
                .token(tokenValue)
                .expiresAt(expiresAt)
                .createdAt(LocalDateTime.now())
                .build();

        refreshTokenRepository.save(refreshToken);

        log.debug("Refresh token generated and saved for user: {}", user.getUsername());
        return tokenValue;
    }

    /**
     * Access Token 검증 및 User 정보 반환
     */
    public User validateAccessToken(String token) {
        log.debug("Validating access token");

        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Long userId = claims.get("userId", Long.class);
            String tenantId = claims.get("tenantId", String.class);
            String username = claims.get("username", String.class);
            String rolesStr = claims.get("roles", String.class);

            User user = User.builder()
                    .userId(userId)
                    .tenantId(tenantId)
                    .username(username)
                    .build();

            if (rolesStr != null && !rolesStr.isEmpty()) {
                String[] roleNames = rolesStr.split(",");
                for (String roleName : roleNames) {
                    user.getRoles().add(Role.builder().roleName(roleName).build());
                }
            }

            log.debug("Access token validated successfully for user: {}", username);
            return user;

        } catch (Exception e) {
            log.error("Failed to validate access token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid access token", e);
        }
    }

    /**
     * Refresh Token으로 새 Access Token 발급
     */
    @Transactional
    public TokenRefreshResult refreshToken(String refreshTokenValue) {
        log.debug("Refreshing token with refresh token");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> {
                    log.error("Refresh token not found");
                    return new IllegalArgumentException("Invalid refresh token");
                });

        if (refreshToken.isExpired()) {
            log.error("Refresh token expired");
            refreshTokenRepository.deleteByUserId(refreshToken.getUserId());
            throw new IllegalArgumentException("Refresh token expired");
        }

        // User 정보 조회
        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> {
                    log.error("User not found for userId: {}", refreshToken.getUserId());
                    return new IllegalArgumentException("User not found");
                });

        // 새 Access Token 생성
        String newAccessToken = generateAccessToken(user);

        log.debug("Token refreshed successfully");

        return TokenRefreshResult.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshTokenValue)
                .expiresIn(accessTokenValiditySeconds)
                .build();
    }
}
