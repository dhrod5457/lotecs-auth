package lotecs.auth.exception.tenant;

import lotecs.auth.exception.AuthErrorCode;
import lotecs.auth.exception.AuthValidationException;

/**
 * 테넌트 데이터 검증 실패 시 발생하는 예외.
 * HTTP 400 Bad Request로 매핑됩니다.
 */
public class TenantValidationException extends AuthValidationException {

    private static final long serialVersionUID = 1L;

    public TenantValidationException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public TenantValidationException(AuthErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static TenantValidationException idRequired() {
        return new TenantValidationException(AuthErrorCode.ATH_TENANT_ID_REQUIRED);
    }

    public static TenantValidationException nameRequired() {
        return new TenantValidationException(AuthErrorCode.ATH_TENANT_NAME_REQUIRED);
    }

    public static TenantValidationException codeRequired() {
        return new TenantValidationException(AuthErrorCode.ATH_TENANT_CODE_REQUIRED);
    }

    public static TenantValidationException domainRequired() {
        return new TenantValidationException(AuthErrorCode.ATH_TENANT_DOMAIN_REQUIRED);
    }

    public static TenantValidationException themeRequired() {
        return new TenantValidationException(AuthErrorCode.ATH_TENANT_THEME_REQUIRED);
    }

    public static TenantValidationException planRequired() {
        return new TenantValidationException(AuthErrorCode.ATH_TENANT_PLAN_REQUIRED);
    }

    public static TenantValidationException levelInvalid() {
        return new TenantValidationException(AuthErrorCode.ATH_TENANT_LEVEL_INVALID);
    }
}
