package lotecs.auth.exception.tenant;

import lotecs.auth.exception.AuthErrorCode;
import lotecs.auth.exception.AuthResourceNotFoundException;

/**
 * 테넌트를 찾을 수 없을 때 발생하는 예외.
 * HTTP 404 Not Found로 매핑됩니다.
 */
public class TenantNotFoundException extends AuthResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public TenantNotFoundException() {
        super(AuthErrorCode.ATH_TENANT_NOT_FOUND);
    }

    public TenantNotFoundException(String tenantId) {
        super(AuthErrorCode.ATH_TENANT_NOT_FOUND,
              AuthErrorCode.ATH_TENANT_NOT_FOUND.getMessage() + ": " + tenantId);
    }

    public static TenantNotFoundException byId(String tenantId) {
        return new TenantNotFoundException(tenantId);
    }

    public static TenantNotFoundException bySiteCode(String siteCode) {
        return new TenantNotFoundException("siteCode=" + siteCode);
    }
}
