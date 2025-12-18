package lotecs.auth.exception.role;

import lotecs.auth.exception.AuthErrorCode;
import lotecs.auth.exception.AuthValidationException;

/**
 * 역할 데이터 검증 실패 시 발생하는 예외.
 * HTTP 400 Bad Request로 매핑됩니다.
 */
public class RoleValidationException extends AuthValidationException {

    private static final long serialVersionUID = 1L;

    public RoleValidationException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public RoleValidationException(AuthErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static RoleValidationException nameRequired() {
        return new RoleValidationException(AuthErrorCode.ATH_ROLE_NAME_REQUIRED);
    }

    public static RoleValidationException tenantRequired() {
        return new RoleValidationException(AuthErrorCode.ATH_ROLE_TENANT_REQUIRED);
    }

    public static RoleValidationException priorityInvalid() {
        return new RoleValidationException(AuthErrorCode.ATH_ROLE_PRIORITY_INVALID);
    }

    public static RoleValidationException idRequired() {
        return new RoleValidationException(AuthErrorCode.ATH_ROLE_ID_REQUIRED);
    }
}
