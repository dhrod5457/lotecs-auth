package lotecs.auth.domain.token.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 리프레시 토큰 도메인 모델 (ATH_REFRESH_TOKENS)
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    private String tokenId;
    private String userId;
    private String tenantId;
    private String tokenHash;
    private String tokenFamily;

    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime revokedAt;
    private String revokedReason;

    private String ipAddress;
    private String userAgent;
    private String deviceId;

    private LocalDateTime lastUsedAt;
    private int usedCount;

    /**
     * 새 리프레시 토큰 생성
     */
    public static RefreshToken create(String userId, String tenantId, String tokenHash, int expirationDays,
                                       String ipAddress, String userAgent, String deviceId) {
        LocalDateTime now = LocalDateTime.now();
        return RefreshToken.builder()
                .tokenId(UUID.randomUUID().toString())
                .userId(userId)
                .tenantId(tenantId)
                .tokenHash(tokenHash)
                .tokenFamily(UUID.randomUUID().toString())
                .issuedAt(now)
                .expiresAt(now.plusDays(expirationDays))
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .deviceId(deviceId)
                .usedCount(0)
                .build();
    }

    /**
     * 토큰 유효성 확인
     */
    public boolean isValid() {
        if (revokedAt != null) {
            return false;
        }
        return LocalDateTime.now().isBefore(expiresAt);
    }

    /**
     * 토큰 만료 여부 확인
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * 토큰 회수 여부 확인
     */
    public boolean isRevoked() {
        return revokedAt != null;
    }

    /**
     * 토큰 사용 기록
     */
    public void recordUsage() {
        this.lastUsedAt = LocalDateTime.now();
        this.usedCount++;
    }

    /**
     * 토큰 회수
     */
    public void revoke(String reason) {
        this.revokedAt = LocalDateTime.now();
        this.revokedReason = reason;
    }

    /**
     * 토큰 갱신 (Rotation)
     */
    public RefreshToken rotate(String newTokenHash, int expirationDays, String ipAddress, String userAgent) {
        this.revoke("TOKEN_ROTATED");
        return RefreshToken.builder()
                .tokenId(UUID.randomUUID().toString())
                .userId(this.userId)
                .tenantId(this.tenantId)
                .tokenHash(newTokenHash)
                .tokenFamily(this.tokenFamily)
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(expirationDays))
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .deviceId(this.deviceId)
                .usedCount(0)
                .build();
    }
}
