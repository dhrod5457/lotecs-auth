package lotecs.auth.infrastructure.relay;

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
public class RelayAuthResponse {

    private boolean success;
    private String externalUserId;
    private String username;
    private String email;
    private String fullName;
    private String userType;
    private String department;
    private List<String> roles;
    private Map<String, Object> additionalData;
    private String errorCode;
    private String errorMessage;

    public static RelayAuthResponse success(
            String externalUserId,
            String username,
            String fullName,
            String userType,
            String department,
            List<String> roles,
            Map<String, Object> additionalData
    ) {
        return RelayAuthResponse.builder()
                .success(true)
                .externalUserId(externalUserId)
                .username(username)
                .fullName(fullName)
                .userType(userType)
                .department(department)
                .roles(roles)
                .additionalData(additionalData)
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
