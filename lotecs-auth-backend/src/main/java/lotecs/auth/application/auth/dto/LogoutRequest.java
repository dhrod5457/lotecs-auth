package lotecs.auth.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutRequest {

    @NotBlank(message = "accessToken is required")
    private String accessToken;

    @NotBlank(message = "userId is required")
    private String userId;
}
