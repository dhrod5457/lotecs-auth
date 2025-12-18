package lotecs.auth.exception.permission;

import lotecs.auth.exception.AuthErrorCode;
import lotecs.auth.exception.AuthValidationException;

/**
 * 권한 데이터 검증 실패 시 발생하는 예외.
 * HTTP 400 Bad Request로 매핑됩니다.
 */
public class PermissionValidationException extends AuthValidationException {

    private static final long serialVersionUID = 1L;

    public PermissionValidationException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public PermissionValidationException(AuthErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static PermissionValidationException nameRequired() {
        return new PermissionValidationException(AuthErrorCode.ATH_PERMISSION_NAME_REQUIRED);
    }

    public static PermissionValidationException resourceRequired() {
        return new PermissionValidationException(AuthErrorCode.ATH_PERMISSION_RESOURCE_REQUIRED);
    }

    public static PermissionValidationException actionRequired() {
        return new PermissionValidationException(AuthErrorCode.ATH_PERMISSION_ACTION_REQUIRED);
    }

    public static PermissionValidationException idRequired() {
        return new PermissionValidationException(AuthErrorCode.ATH_PERMISSION_ID_REQUIRED);
    }

    public static PermissionValidationException tenantRequired() {
        return new PermissionValidationException(AuthErrorCode.ATH_PERMISSION_TENANT_REQUIRED);
    }
}
