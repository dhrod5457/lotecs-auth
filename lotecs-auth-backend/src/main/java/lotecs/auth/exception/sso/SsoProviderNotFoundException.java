package lotecs.auth.exception.sso;

import lotecs.auth.exception.AuthErrorCode;

/**
 * SSO 프로바이더를 찾을 수 없을 때 발생하는 예외.
 * HTTP 500 Internal Server Error로 매핑됩니다.
 */
public class SsoProviderNotFoundException extends SsoException {

    private static final long serialVersionUID = 1L;

    public SsoProviderNotFoundException() {
        super(AuthErrorCode.ATH_SSO_PROVIDER_NOT_FOUND);
    }

    public SsoProviderNotFoundException(String ssoType) {
        super(AuthErrorCode.ATH_SSO_PROVIDER_NOT_FOUND,
              AuthErrorCode.ATH_SSO_PROVIDER_NOT_FOUND.getMessage() + ": " + ssoType);
    }

    public static SsoProviderNotFoundException forType(String ssoType) {
        return new SsoProviderNotFoundException(ssoType);
    }
}
