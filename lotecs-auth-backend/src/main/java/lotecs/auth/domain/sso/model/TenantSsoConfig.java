package lotecs.auth.domain.sso.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lotecs.auth.domain.sso.SsoType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantSsoConfig {

    @NotBlank
    private String tenantId;

    @NotNull
    private SsoType ssoType;

    @NotNull
    @Builder.Default
    private Boolean ssoEnabled = false;

    private String relayEndpoint;

    private Integer relayTimeoutMs;

    private String ssoServerUrl;

    private String ssoRealm;

    private String ssoClientId;

    private String ssoClientSecret;

    @NotNull
    @Builder.Default
    private Boolean userSyncEnabled = false;

    @NotNull
    @Builder.Default
    private Boolean roleMappingEnabled = false;

    private String additionalConfig;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public boolean isRoleMappingEnabled() {
        return Boolean.TRUE.equals(this.roleMappingEnabled);
    }

    public boolean isUserSyncEnabled() {
        return Boolean.TRUE.equals(this.userSyncEnabled);
    }

    public String getRelayEndpoint() {
        return this.relayEndpoint;
    }
}
