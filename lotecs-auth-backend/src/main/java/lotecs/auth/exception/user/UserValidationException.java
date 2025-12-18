package lotecs.auth.exception.user;

import lotecs.auth.exception.AuthErrorCode;
import lotecs.auth.exception.AuthValidationException;

/**
 * 사용자 데이터 검증 실패 시 발생하는 예외.
 * HTTP 400 Bad Request로 매핑됩니다.
 */
public class UserValidationException extends AuthValidationException {

    private static final long serialVersionUID = 1L;

    public UserValidationException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public UserValidationException(AuthErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static UserValidationException usernameRequired() {
        return new UserValidationException(AuthErrorCode.ATH_USER_USERNAME_REQUIRED);
    }

    public static UserValidationException passwordRequired() {
        return new UserValidationException(AuthErrorCode.ATH_USER_PASSWORD_REQUIRED);
    }

    public static UserValidationException emailInvalid() {
        return new UserValidationException(AuthErrorCode.ATH_USER_EMAIL_INVALID);
    }

    public static UserValidationException tenantRequired() {
        return new UserValidationException(AuthErrorCode.ATH_USER_TENANT_REQUIRED);
    }
}
