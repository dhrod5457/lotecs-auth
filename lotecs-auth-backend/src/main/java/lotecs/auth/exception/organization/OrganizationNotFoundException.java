package lotecs.auth.exception.organization;

import lotecs.auth.exception.AuthErrorCode;
import lotecs.auth.exception.AuthResourceNotFoundException;

/**
 * 조직을 찾을 수 없을 때 발생하는 예외.
 * HTTP 404 Not Found로 매핑됩니다.
 */
public class OrganizationNotFoundException extends AuthResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public OrganizationNotFoundException() {
        super(AuthErrorCode.ATH_ORGANIZATION_NOT_FOUND);
    }

    public OrganizationNotFoundException(String organizationId) {
        super(AuthErrorCode.ATH_ORGANIZATION_NOT_FOUND,
              AuthErrorCode.ATH_ORGANIZATION_NOT_FOUND.getMessage() + ": " + organizationId);
    }

    public static OrganizationNotFoundException byId(String organizationId) {
        return new OrganizationNotFoundException(organizationId);
    }

    public static OrganizationNotFoundException byCode(String organizationCode, String tenantId) {
        return new OrganizationNotFoundException("organizationCode=" + organizationCode + ", tenantId=" + tenantId);
    }
}
