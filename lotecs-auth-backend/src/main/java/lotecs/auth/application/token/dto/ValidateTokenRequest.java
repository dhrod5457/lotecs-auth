package lotecs.auth.application.token.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ValidateTokenRequest {

    @NotBlank(message = "accessToken is required")
    private String accessToken;
}
