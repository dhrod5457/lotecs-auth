package lotecs.auth.domain.sso;

import jakarta.validation.constraints.NotBlank;

public record SsoAuthRequest(
        @NotBlank String tenantId,
        @NotBlank String username,
        @NotBlank String password,
        String ipAddress
) {
}
