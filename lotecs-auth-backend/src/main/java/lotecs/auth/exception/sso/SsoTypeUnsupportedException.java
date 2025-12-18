package lotecs.auth.exception.sso;

import lotecs.auth.exception.AuthErrorCode;

/**
 * SSO 타입이 지원되지 않거나 더 이상 사용되지 않을 때 발생하는 예외.
 * HTTP 400 Bad Request로 매핑됩니다.
 */
public class SsoTypeUnsupportedException extends SsoException {

    private static final long serialVersionUID = 1L;

    public SsoTypeUnsupportedException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public SsoTypeUnsupportedException(AuthErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static SsoTypeUnsupportedException unsupported(String ssoType) {
        return new SsoTypeUnsupportedException(
            AuthErrorCode.ATH_SSO_TYPE_UNSUPPORTED,
            AuthErrorCode.ATH_SSO_TYPE_UNSUPPORTED.getMessage() + ": " + ssoType
        );
    }

    public static SsoTypeUnsupportedException deprecated(String ssoType, String recommendedTypes) {
        return new SsoTypeUnsupportedException(
            AuthErrorCode.ATH_SSO_TYPE_DEPRECATED,
            ssoType + " SSO 타입은 더 이상 지원하지 않습니다. " + recommendedTypes + "로 마이그레이션하세요."
        );
    }

    public static SsoTypeUnsupportedException deprecated(String ssoType) {
        return new SsoTypeUnsupportedException(
            AuthErrorCode.ATH_SSO_TYPE_DEPRECATED,
            ssoType + " SSO 타입은 더 이상 지원하지 않습니다. JWT_SSO, CAS, REST_TOKEN, 또는 HTTP_FORM으로 마이그레이션하세요."
        );
    }

    public static SsoTypeUnsupportedException internal() {
        return new SsoTypeUnsupportedException(
            AuthErrorCode.ATH_SSO_TYPE_UNSUPPORTED,
            "INTERNAL SSO 타입은 SsoProviderFactory에서 지원하지 않습니다."
        );
    }

    public static SsoTypeUnsupportedException internalNotSupported() {
        return new SsoTypeUnsupportedException(
            AuthErrorCode.ATH_SSO_TYPE_UNSUPPORTED,
            "INTERNAL SSO 타입은 SsoProviderFactory에서 지원하지 않습니다."
        );
    }
}
