package lotecs.auth.exception;

/**
 * 입력값 검증 실패 시 발생하는 예외의 기본 클래스.
 * HTTP 400 Bad Request로 매핑됩니다.
 */
public class AuthValidationException extends AuthException {

    private static final long serialVersionUID = 1L;

    public AuthValidationException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public AuthValidationException(AuthErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public AuthValidationException(AuthErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
