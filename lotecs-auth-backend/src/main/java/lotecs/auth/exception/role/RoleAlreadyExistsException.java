package lotecs.auth.exception.role;

import lotecs.auth.exception.AuthDuplicateException;
import lotecs.auth.exception.AuthErrorCode;

/**
 * 역할이 이미 존재할 때 발생하는 예외.
 * HTTP 409 Conflict로 매핑됩니다.
 */
public class RoleAlreadyExistsException extends AuthDuplicateException {

    private static final long serialVersionUID = 1L;

    public RoleAlreadyExistsException() {
        super(AuthErrorCode.ATH_ROLE_ALREADY_EXISTS);
    }

    public RoleAlreadyExistsException(String roleName) {
        super(AuthErrorCode.ATH_ROLE_ALREADY_EXISTS,
              AuthErrorCode.ATH_ROLE_ALREADY_EXISTS.getMessage() + ": " + roleName);
    }

    public static RoleAlreadyExistsException byName(String roleName) {
        return new RoleAlreadyExistsException(roleName);
    }
}
