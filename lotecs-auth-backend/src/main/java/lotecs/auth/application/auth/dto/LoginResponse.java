package lotecs.auth.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lotecs.auth.application.user.dto.UserDto;
import lotecs.auth.domain.sso.SsoType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private UserDto user;
    private SsoType ssoType;
}
