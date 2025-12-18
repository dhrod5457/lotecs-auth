package lotecs.auth.exception.role;

import lotecs.auth.exception.AuthErrorCode;
import lotecs.auth.exception.AuthResourceNotFoundException;

/**
 * 역할을 찾을 수 없을 때 발생하는 예외.
 * HTTP 404 Not Found로 매핑됩니다.
 */
public class RoleNotFoundException extends AuthResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public RoleNotFoundException() {
        super(AuthErrorCode.ATH_ROLE_NOT_FOUND);
    }

    public RoleNotFoundException(String roleId) {
        super(AuthErrorCode.ATH_ROLE_NOT_FOUND,
              AuthErrorCode.ATH_ROLE_NOT_FOUND.getMessage() + ": " + roleId);
    }

    public static RoleNotFoundException byId(String roleId) {
        return new RoleNotFoundException(roleId);
    }

    public static RoleNotFoundException byName(String roleName, String tenantId) {
        return new RoleNotFoundException("roleName=" + roleName + ", tenantId=" + tenantId);
    }

    public static RoleNotFoundException byName(String roleName) {
        return new RoleNotFoundException("roleName=" + roleName);
    }
}
