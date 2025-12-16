package lotecs.auth.domain.sso;

import java.util.List;

public record SsoAuthResult(
        boolean success,
        String externalUserId,
        String username,
        String email,
        String fullName,
        List<String> roles,
        String errorCode,
        String errorMessage
) {

    public static SsoAuthResult success(
            String externalUserId,
            String username,
            String email,
            String fullName,
            List<String> roles
    ) {
        return new SsoAuthResult(
                true,
                externalUserId,
                username,
                email,
                fullName,
                roles,
                null,
                null
        );
    }

    public static SsoAuthResult failure(String errorCode, String errorMessage) {
        return new SsoAuthResult(
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
