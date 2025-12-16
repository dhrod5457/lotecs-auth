package lotecs.auth.domain.user.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자-역할 매핑 도메인 모델 (ATH_USER_ROLES)
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {

    private String userId;
    private String roleId;
    private String tenantId;

    private String statusCode;
    private LocalDateTime statusChangedAt;
    private String statusChangedBy;
    private String statusReason;

    private LocalDateTime validFrom;
    private LocalDateTime validUntil;

    private LocalDateTime assignedAt;
    private String assignedBy;
    private LocalDateTime revokedAt;
    private String revokedBy;

    /**
     * 역할 할당 생성
     */
    public static UserRole assign(String userId, String roleId, String tenantId, String assignedBy) {
        return UserRole.builder()
                .userId(userId)
                .roleId(roleId)
                .tenantId(tenantId)
                .statusCode("ACTIVE")
                .assignedAt(LocalDateTime.now())
                .assignedBy(assignedBy)
                .build();
    }

    /**
     * 역할 활성 여부 확인
     */
    public boolean isActive() {
        if (revokedAt != null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        if (validFrom != null && now.isBefore(validFrom)) {
            return false;
        }
        if (validUntil != null && now.isAfter(validUntil)) {
            return false;
        }
        return true;
    }

    /**
     * 역할 회수
     */
    public void revoke(String revokedBy) {
        this.revokedAt = LocalDateTime.now();
        this.revokedBy = revokedBy;
    }

    /**
     * 상태 변경
     */
    public void changeStatus(String statusCode, String reason, String changedBy) {
        this.statusCode = statusCode;
        this.statusReason = reason;
        this.statusChangedAt = LocalDateTime.now();
        this.statusChangedBy = changedBy;
    }
}
