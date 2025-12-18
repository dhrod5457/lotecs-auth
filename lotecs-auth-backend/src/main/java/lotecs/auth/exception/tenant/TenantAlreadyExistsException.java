package lotecs.auth.exception.tenant;

import lotecs.auth.exception.AuthDuplicateException;
import lotecs.auth.exception.AuthErrorCode;

/**
 * 테넌트가 이미 존재할 때 발생하는 예외.
 * HTTP 409 Conflict로 매핑됩니다.
 */
public class TenantAlreadyExistsException extends AuthDuplicateException {

    private static final long serialVersionUID = 1L;

    public TenantAlreadyExistsException() {
        super(AuthErrorCode.ATH_TENANT_ALREADY_EXISTS);
    }

    public TenantAlreadyExistsException(String siteCode) {
        super(AuthErrorCode.ATH_TENANT_ALREADY_EXISTS,
              AuthErrorCode.ATH_TENANT_ALREADY_EXISTS.getMessage() + ": " + siteCode);
    }

    public static TenantAlreadyExistsException bySiteCode(String siteCode) {
        return new TenantAlreadyExistsException(siteCode);
    }
}
