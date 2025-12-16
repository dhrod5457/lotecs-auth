package lotecs.auth.application.token.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshResult {

    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
}
