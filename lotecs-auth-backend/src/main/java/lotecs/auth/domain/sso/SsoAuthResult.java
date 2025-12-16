package lotecs.auth.domain.sso;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoAuthResult {

    private boolean success;
    private String externalUserId;
    private String username;
    private String email;
    private String fullName;
    private List<String> roles;
    private Map<String, Object> additionalData;
    private String errorCode;
    private String errorMessage;

    public static SsoAuthResult success(
            String externalUserId,
            String username,
            String email,
            String fullName,
            List<String> roles,
            Map<String, Object> additionalData
    ) {
        return SsoAuthResult.builder()
                .success(true)
                .externalUserId(externalUserId)
                .username(username)
                .email(email)
                .fullName(fullName)
                .roles(roles)
                .additionalData(additionalData)
                .build();
    }

    public static SsoAuthResult failure(String errorCode, String errorMessage) {
        return SsoAuthResult.builder()
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }
}
