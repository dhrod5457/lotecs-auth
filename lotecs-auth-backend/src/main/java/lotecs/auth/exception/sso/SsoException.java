package lotecs.auth.exception.sso;

import lotecs.auth.exception.AuthErrorCode;
import lotecs.auth.exception.AuthException;

/**
 * SSO 관련 예외의 기본 클래스.
 */
public class SsoException extends AuthException {

    private static final long serialVersionUID = 1L;

    public SsoException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public SsoException(AuthErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public SsoException(AuthErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public SsoException(AuthErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
