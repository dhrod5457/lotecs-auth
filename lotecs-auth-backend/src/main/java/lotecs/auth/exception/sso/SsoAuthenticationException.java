package lotecs.auth.exception.sso;

import lotecs.auth.exception.AuthErrorCode;

/**
 * SSO 인증에 실패했을 때 발생하는 예외.
 * HTTP 401 Unauthorized로 매핑됩니다.
 */
public class SsoAuthenticationException extends SsoException {

    private static final long serialVersionUID = 1L;

    public SsoAuthenticationException() {
        super(AuthErrorCode.ATH_SSO_AUTH_FAILED);
    }

    public SsoAuthenticationException(String message) {
        super(AuthErrorCode.ATH_SSO_AUTH_FAILED, message);
    }

    public SsoAuthenticationException(String message, Throwable cause) {
        super(AuthErrorCode.ATH_SSO_AUTH_FAILED, message, cause);
    }

    public static SsoAuthenticationException withReason(String reason) {
        return new SsoAuthenticationException(
            AuthErrorCode.ATH_SSO_AUTH_FAILED.getMessage() + ": " + reason
        );
    }
}
