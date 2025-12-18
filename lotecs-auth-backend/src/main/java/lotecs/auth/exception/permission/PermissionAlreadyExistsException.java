package lotecs.auth.exception.permission;

import lotecs.auth.exception.AuthDuplicateException;
import lotecs.auth.exception.AuthErrorCode;

/**
 * 권한이 이미 존재할 때 발생하는 예외.
 * HTTP 409 Conflict로 매핑됩니다.
 */
public class PermissionAlreadyExistsException extends AuthDuplicateException {

    private static final long serialVersionUID = 1L;

    public PermissionAlreadyExistsException() {
        super(AuthErrorCode.ATH_PERMISSION_ALREADY_EXISTS);
    }

    public PermissionAlreadyExistsException(String identifier) {
        super(AuthErrorCode.ATH_PERMISSION_ALREADY_EXISTS,
              AuthErrorCode.ATH_PERMISSION_ALREADY_EXISTS.getMessage() + ": " + identifier);
    }

    public static PermissionAlreadyExistsException byName(String permissionName) {
        return new PermissionAlreadyExistsException(permissionName);
    }

    public static PermissionAlreadyExistsException byResourceAndAction(String resource, String action) {
        return new PermissionAlreadyExistsException("resource=" + resource + ", action=" + action);
    }
}
