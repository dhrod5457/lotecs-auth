package lotecs.auth.exception.permission;

import lotecs.auth.exception.AuthErrorCode;
import lotecs.auth.exception.AuthResourceNotFoundException;

/**
 * 권한을 찾을 수 없을 때 발생하는 예외.
 * HTTP 404 Not Found로 매핑됩니다.
 */
public class PermissionNotFoundException extends AuthResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public PermissionNotFoundException() {
        super(AuthErrorCode.ATH_PERMISSION_NOT_FOUND);
    }

    public PermissionNotFoundException(String permissionId) {
        super(AuthErrorCode.ATH_PERMISSION_NOT_FOUND,
              AuthErrorCode.ATH_PERMISSION_NOT_FOUND.getMessage() + ": " + permissionId);
    }

    public static PermissionNotFoundException byId(String permissionId) {
        return new PermissionNotFoundException(permissionId);
    }

    public static PermissionNotFoundException byName(String permissionName, String tenantId) {
        return new PermissionNotFoundException("permissionName=" + permissionName + ", tenantId=" + tenantId);
    }
}
