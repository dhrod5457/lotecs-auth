package lotecs.auth.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LogoutRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotBlank(message = "tenantId is required")
    private String tenantId;
}
