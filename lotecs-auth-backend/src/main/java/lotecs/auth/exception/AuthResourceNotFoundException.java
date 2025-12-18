package lotecs.auth.exception;

/**
 * 리소스를 찾을 수 없을 때 발생하는 예외의 기본 클래스.
 * HTTP 404 Not Found로 매핑됩니다.
 */
public class AuthResourceNotFoundException extends AuthException {

    private static final long serialVersionUID = 1L;

    public AuthResourceNotFoundException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public AuthResourceNotFoundException(AuthErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public AuthResourceNotFoundException(AuthErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
