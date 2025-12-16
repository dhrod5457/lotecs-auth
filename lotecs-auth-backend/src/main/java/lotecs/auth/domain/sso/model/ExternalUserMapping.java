package lotecs.auth.domain.sso.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalUserMapping {

    private String mappingId;

    @NotBlank
    private String tenantId;

    @NotBlank
    private String userId;

    @NotBlank
    private String externalUserId;

    @NotBlank
    private String externalSystem;

    private LocalDateTime lastSyncedAt;

    private LocalDateTime createdAt;

    /**
     * 새 외부 사용자 매핑 생성
     */
    public static ExternalUserMapping create(String tenantId, String userId, String externalUserId, String externalSystem) {
        return ExternalUserMapping.builder()
                .mappingId(UUID.randomUUID().toString())
                .tenantId(tenantId)
                .userId(userId)
                .externalUserId(externalUserId)
                .externalSystem(externalSystem)
                .lastSyncedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 동기화 시간 갱신
     */
    public void updateSyncTime() {
        this.lastSyncedAt = LocalDateTime.now();
    }
}
