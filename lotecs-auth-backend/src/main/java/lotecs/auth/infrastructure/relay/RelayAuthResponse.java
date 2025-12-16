package lotecs.auth.infrastructure.relay;

import java.util.List;

public record RelayAuthResponse(
    boolean success,
    String externalUserId,
    String username,
    String email,
    String fullName,
    List<String> roles,
    String errorCode,
    String errorMessage
) {
    public static RelayAuthResponse success(
        String externalUserId,
        String username,
        String fullName,
        List<String> roles
    ) {
        return new RelayAuthResponse(
            true,
            externalUserId,
            username,
            null,
            fullName,
            roles,
            null,
            null
        );
    }

    public static RelayAuthResponse failure(String errorCode, String errorMessage) {
        return new RelayAuthResponse(
            false,
            null,
            null,
            null,
            null,
            null,
            errorCode,
            errorMessage
        );
    }
}
