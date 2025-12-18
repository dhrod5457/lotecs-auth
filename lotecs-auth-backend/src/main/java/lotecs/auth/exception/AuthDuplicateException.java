package lotecs.auth.exception;

/**
 * 리소스가 이미 존재할 때 발생하는 예외의 기본 클래스.
 * HTTP 409 Conflict로 매핑됩니다.
 */
public class AuthDuplicateException extends AuthException {

    private static final long serialVersionUID = 1L;

    public AuthDuplicateException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public AuthDuplicateException(AuthErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public AuthDuplicateException(AuthErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
