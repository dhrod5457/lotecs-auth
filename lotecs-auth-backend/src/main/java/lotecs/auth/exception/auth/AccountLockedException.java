package lotecs.auth.exception.auth;

import lotecs.auth.exception.AuthErrorCode;
import lotecs.auth.exception.AuthException;

/**
 * 계정이 잠겨있을 때 발생하는 예외.
 * HTTP 423 Locked로 매핑됩니다.
 */
public class AccountLockedException extends AuthException {

    private static final long serialVersionUID = 1L;

    public AccountLockedException() {
        super(AuthErrorCode.ATH_ACCOUNT_LOCKED);
    }

    public AccountLockedException(String message) {
        super(AuthErrorCode.ATH_ACCOUNT_LOCKED, message);
    }
}
