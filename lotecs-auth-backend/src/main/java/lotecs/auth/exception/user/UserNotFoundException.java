package lotecs.auth.exception.user;

import lotecs.auth.exception.AuthErrorCode;
import lotecs.auth.exception.AuthResourceNotFoundException;

/**
 * 사용자를 찾을 수 없을 때 발생하는 예외.
 * HTTP 404 Not Found로 매핑됩니다.
 */
public class UserNotFoundException extends AuthResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public UserNotFoundException() {
        super(AuthErrorCode.ATH_USER_NOT_FOUND);
    }

    public UserNotFoundException(String identifier) {
        super(AuthErrorCode.ATH_USER_NOT_FOUND,
              AuthErrorCode.ATH_USER_NOT_FOUND.getMessage() + ": " + identifier);
    }

    public static UserNotFoundException byId(String userId) {
        return new UserNotFoundException(userId);
    }

    public static UserNotFoundException byUsername(String username, String tenantId) {
        return new UserNotFoundException("username=" + username + ", tenantId=" + tenantId);
    }

    public static UserNotFoundException byUsername(String username) {
        return new UserNotFoundException("username=" + username);
    }
}
