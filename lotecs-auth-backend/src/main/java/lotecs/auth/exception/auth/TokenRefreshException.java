package lotecs.auth.exception.auth;

import lotecs.auth.exception.AuthErrorCode;
import lotecs.auth.exception.AuthException;

/**
 * 토큰 갱신에 실패했을 때 발생하는 예외.
 * HTTP 401 Unauthorized로 매핑됩니다.
 */
public class TokenRefreshException extends AuthException {

    private static final long serialVersionUID = 1L;

    public TokenRefreshException() {
        super(AuthErrorCode.ATH_TOKEN_REFRESH_FAILED);
    }

    public TokenRefreshException(String message) {
        super(AuthErrorCode.ATH_TOKEN_REFRESH_FAILED, message);
    }

    public TokenRefreshException(String message, Throwable cause) {
        super(AuthErrorCode.ATH_TOKEN_REFRESH_FAILED, message, cause);
    }
}
