package lotecs.auth.infrastructure.relay;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelayAuthResponse {

    private boolean success;
    private String externalUserId;
    private String username;
    private String email;
    private String fullName;
    private List<String> roles;
    private String errorCode;
    private String errorMessage;

    public static RelayAuthResponse success(
            String externalUserId,
            String username,
            String fullName,
            List<String> roles
    ) {
        return RelayAuthResponse.builder()
                .success(true)
                .externalUserId(externalUserId)
                .username(username)
                .fullName(fullName)
                .roles(roles)
                .build();
    }

    public static RelayAuthResponse failure(String errorCode, String errorMessage) {
        return RelayAuthResponse.builder()
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }
}
