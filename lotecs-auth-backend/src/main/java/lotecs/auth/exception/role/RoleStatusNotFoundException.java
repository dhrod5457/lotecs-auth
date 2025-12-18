package lotecs.auth.exception.role;

import lotecs.auth.exception.AuthErrorCode;
import lotecs.auth.exception.AuthResourceNotFoundException;

/**
 * 역할 상태를 찾을 수 없을 때 발생하는 예외.
 * HTTP 404 Not Found로 매핑됩니다.
 */
public class RoleStatusNotFoundException extends AuthResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public RoleStatusNotFoundException() {
        super(AuthErrorCode.ATH_ROLE_STATUS_NOT_FOUND);
    }

    public RoleStatusNotFoundException(String statusCode) {
        super(AuthErrorCode.ATH_ROLE_STATUS_NOT_FOUND,
              AuthErrorCode.ATH_ROLE_STATUS_NOT_FOUND.getMessage() + ": " + statusCode);
    }

    public static RoleStatusNotFoundException byCode(String statusCode) {
        return new RoleStatusNotFoundException(statusCode);
    }
}
