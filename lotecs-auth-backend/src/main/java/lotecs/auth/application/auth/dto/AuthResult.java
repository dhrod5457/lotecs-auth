package lotecs.auth.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lotecs.auth.domain.user.model.User;

import java.util.Map;

/**
 * 인증 결과 홀더 (User + additionalData).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResult {

    private User user;
    private Map<String, Object> additionalData;

    public static AuthResult of(User user) {
        return AuthResult.builder()
                .user(user)
                .build();
    }

    public static AuthResult of(User user, Map<String, Object> additionalData) {
        return AuthResult.builder()
                .user(user)
                .additionalData(additionalData)
                .build();
    }
}
