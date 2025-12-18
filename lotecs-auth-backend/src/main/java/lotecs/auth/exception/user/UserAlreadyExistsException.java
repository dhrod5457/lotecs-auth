package lotecs.auth.exception.user;

import lotecs.auth.exception.AuthDuplicateException;
import lotecs.auth.exception.AuthErrorCode;

/**
 * 사용자가 이미 존재할 때 발생하는 예외.
 * HTTP 409 Conflict로 매핑됩니다.
 */
public class UserAlreadyExistsException extends AuthDuplicateException {

    private static final long serialVersionUID = 1L;

    public UserAlreadyExistsException() {
        super(AuthErrorCode.ATH_USER_ALREADY_EXISTS);
    }

    public UserAlreadyExistsException(String username) {
        super(AuthErrorCode.ATH_USER_ALREADY_EXISTS,
              AuthErrorCode.ATH_USER_ALREADY_EXISTS.getMessage() + ": " + username);
    }

    public static UserAlreadyExistsException byUsername(String username) {
        return new UserAlreadyExistsException(username);
    }
}
