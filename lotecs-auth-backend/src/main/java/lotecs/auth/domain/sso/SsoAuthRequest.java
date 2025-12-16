package lotecs.auth.domain.sso;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SsoAuthRequest {

    @NotBlank
    private String tenantId;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String ipAddress;
}
