package lotecs.auth.exception.role;

import lotecs.auth.exception.AuthErrorCode;
import lotecs.auth.exception.AuthValidationException;

/**
 * 역할 상태 데이터 검증 실패 시 발생하는 예외.
 * HTTP 400 Bad Request로 매핑됩니다.
 */
public class RoleStatusValidationException extends AuthValidationException {

    private static final long serialVersionUID = 1L;

    public RoleStatusValidationException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public RoleStatusValidationException(AuthErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static RoleStatusValidationException codeRequired() {
        return new RoleStatusValidationException(AuthErrorCode.ATH_ROLE_STATUS_CODE_REQUIRED);
    }

    public static RoleStatusValidationException nameRequired() {
        return new RoleStatusValidationException(AuthErrorCode.ATH_ROLE_STATUS_NAME_REQUIRED);
    }

    public static RoleStatusValidationException categoryRequired() {
        return new RoleStatusValidationException(AuthErrorCode.ATH_ROLE_STATUS_CATEGORY_REQUIRED);
    }

    public static RoleStatusValidationException categoryInvalid(String category) {
        return new RoleStatusValidationException(
            AuthErrorCode.ATH_ROLE_STATUS_CATEGORY_INVALID,
            AuthErrorCode.ATH_ROLE_STATUS_CATEGORY_INVALID.getMessage() + ": " + category
        );
    }
}
