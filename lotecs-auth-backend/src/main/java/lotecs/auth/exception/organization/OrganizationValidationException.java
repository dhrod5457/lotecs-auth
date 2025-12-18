package lotecs.auth.exception.organization;

import lotecs.auth.exception.AuthErrorCode;
import lotecs.auth.exception.AuthValidationException;

/**
 * 조직 데이터 검증 실패 시 발생하는 예외.
 * HTTP 400 Bad Request로 매핑됩니다.
 */
public class OrganizationValidationException extends AuthValidationException {

    private static final long serialVersionUID = 1L;

    public OrganizationValidationException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public OrganizationValidationException(AuthErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static OrganizationValidationException idRequired() {
        return new OrganizationValidationException(AuthErrorCode.ATH_ORGANIZATION_ID_REQUIRED);
    }

    public static OrganizationValidationException codeRequired() {
        return new OrganizationValidationException(AuthErrorCode.ATH_ORGANIZATION_CODE_REQUIRED);
    }

    public static OrganizationValidationException nameRequired() {
        return new OrganizationValidationException(AuthErrorCode.ATH_ORGANIZATION_NAME_REQUIRED);
    }

    public static OrganizationValidationException typeRequired() {
        return new OrganizationValidationException(AuthErrorCode.ATH_ORGANIZATION_TYPE_REQUIRED);
    }

    public static OrganizationValidationException levelInvalid() {
        return new OrganizationValidationException(AuthErrorCode.ATH_ORGANIZATION_LEVEL_INVALID);
    }

    public static OrganizationValidationException tenantRequired() {
        return new OrganizationValidationException(AuthErrorCode.ATH_ORGANIZATION_TENANT_REQUIRED);
    }
}
