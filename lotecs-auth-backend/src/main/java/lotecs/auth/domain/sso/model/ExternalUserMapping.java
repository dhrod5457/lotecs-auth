package lotecs.auth.domain.sso.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalUserMapping {

    private Long mappingId;

    @NotBlank
    private String tenantId;

    @NotNull
    private Long userId;

    @NotBlank
    private String externalUserId;

    @NotBlank
    private String externalSystem;

    private LocalDateTime lastSyncedAt;

    private LocalDateTime createdAt;
}
