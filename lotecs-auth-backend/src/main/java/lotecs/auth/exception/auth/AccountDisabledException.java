package lotecs.auth.exception.auth;

import lotecs.auth.exception.AuthErrorCode;
import lotecs.auth.exception.AuthException;

/**
 * 계정이 비활성화되었을 때 발생하는 예외.
 * HTTP 403 Forbidden으로 매핑됩니다.
 */
public class AccountDisabledException extends AuthException {

    private static final long serialVersionUID = 1L;

    public AccountDisabledException() {
        super(AuthErrorCode.ATH_ACCOUNT_DISABLED);
    }

    public AccountDisabledException(String message) {
        super(AuthErrorCode.ATH_ACCOUNT_DISABLED, message);
    }

    public static AccountDisabledException notActive() {
        return new AccountDisabledException(AuthErrorCode.ATH_ACCOUNT_NOT_ACTIVE.getMessage());
    }
}
