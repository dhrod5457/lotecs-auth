package lotecs.auth.exception.sso;

import lotecs.auth.exception.AuthErrorCode;

/**
 * SSO 서버 오류가 발생했을 때 발생하는 예외.
 * HTTP 502 Bad Gateway로 매핑됩니다.
 */
public class SsoServerException extends SsoException {

    private static final long serialVersionUID = 1L;

    public SsoServerException() {
        super(AuthErrorCode.ATH_SSO_SERVER_ERROR);
    }

    public SsoServerException(String message) {
        super(AuthErrorCode.ATH_SSO_SERVER_ERROR, message);
    }

    public SsoServerException(String message, Throwable cause) {
        super(AuthErrorCode.ATH_SSO_SERVER_ERROR, message, cause);
    }

    public SsoServerException(Throwable cause) {
        super(AuthErrorCode.ATH_SSO_SERVER_ERROR, cause);
    }

    public static SsoServerException withStatusCode(int statusCode) {
        return new SsoServerException("SSO 서버가 상태 코드 " + statusCode + "를 반환했습니다.");
    }

    public static SsoServerException invalidResponse(String reason) {
        return new SsoServerException("SSO 응답이 유효하지 않습니다: " + reason);
    }
}
