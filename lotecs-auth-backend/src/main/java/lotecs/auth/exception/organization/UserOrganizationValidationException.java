package lotecs.auth.exception.organization;

import lotecs.auth.exception.AuthErrorCode;
import lotecs.auth.exception.AuthValidationException;

/**
 * 사용자-조직 매핑 데이터 검증 실패 시 발생하는 예외.
 * HTTP 400 Bad Request로 매핑됩니다.
 */
public class UserOrganizationValidationException extends AuthValidationException {

    private static final long serialVersionUID = 1L;

    public UserOrganizationValidationException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public UserOrganizationValidationException(AuthErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static UserOrganizationValidationException userRequired() {
        return new UserOrganizationValidationException(AuthErrorCode.ATH_USER_ORG_USER_REQUIRED);
    }

    public static UserOrganizationValidationException organizationRequired() {
        return new UserOrganizationValidationException(AuthErrorCode.ATH_USER_ORG_ORG_REQUIRED);
    }

    public static UserOrganizationValidationException dateInvalid() {
        return new UserOrganizationValidationException(AuthErrorCode.ATH_USER_ORG_DATE_INVALID);
    }

    public static UserOrganizationValidationException tenantRequired() {
        return new UserOrganizationValidationException(AuthErrorCode.ATH_USER_ORG_TENANT_REQUIRED);
    }
}
