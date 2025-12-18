package lotecs.auth.exception.role;

import lotecs.auth.exception.AuthDuplicateException;
import lotecs.auth.exception.AuthErrorCode;

/**
 * 역할 상태가 이미 존재할 때 발생하는 예외.
 * HTTP 409 Conflict로 매핑됩니다.
 */
public class RoleStatusAlreadyExistsException extends AuthDuplicateException {

    private static final long serialVersionUID = 1L;

    public RoleStatusAlreadyExistsException() {
        super(AuthErrorCode.ATH_ROLE_STATUS_ALREADY_EXISTS);
    }

    public RoleStatusAlreadyExistsException(String statusCode) {
        super(AuthErrorCode.ATH_ROLE_STATUS_ALREADY_EXISTS,
              AuthErrorCode.ATH_ROLE_STATUS_ALREADY_EXISTS.getMessage() + ": " + statusCode);
    }

    public static RoleStatusAlreadyExistsException byCode(String statusCode) {
        return new RoleStatusAlreadyExistsException(statusCode);
    }
}
