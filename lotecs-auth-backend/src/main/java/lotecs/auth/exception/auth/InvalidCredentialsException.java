package lotecs.auth.exception.auth;

import lotecs.auth.exception.AuthErrorCode;
import lotecs.auth.exception.AuthException;

/**
 * 자격 증명이 유효하지 않을 때 발생하는 예외.
 * HTTP 401 Unauthorized로 매핑됩니다.
 */
public class InvalidCredentialsException extends AuthException {

    private static final long serialVersionUID = 1L;

    public InvalidCredentialsException() {
        super(AuthErrorCode.ATH_CREDENTIALS_INVALID);
    }

    public InvalidCredentialsException(String message) {
        super(AuthErrorCode.ATH_CREDENTIALS_INVALID, message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(AuthErrorCode.ATH_CREDENTIALS_INVALID, message, cause);
    }

    public static InvalidCredentialsException passwordMismatch() {
        return new InvalidCredentialsException(AuthErrorCode.ATH_CREDENTIALS_PASSWORD_MISMATCH.getMessage());
    }

    public static InvalidCredentialsException accountNotActive() {
        return new InvalidCredentialsException(AuthErrorCode.ATH_ACCOUNT_NOT_ACTIVE.getMessage());
    }
}
