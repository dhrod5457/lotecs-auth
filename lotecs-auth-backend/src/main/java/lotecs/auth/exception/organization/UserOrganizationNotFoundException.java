package lotecs.auth.exception.organization;

import lotecs.auth.exception.AuthErrorCode;
import lotecs.auth.exception.AuthResourceNotFoundException;

/**
 * 사용자-조직 매핑을 찾을 수 없을 때 발생하는 예외.
 * HTTP 404 Not Found로 매핑됩니다.
 */
public class UserOrganizationNotFoundException extends AuthResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public UserOrganizationNotFoundException() {
        super(AuthErrorCode.ATH_USER_ORG_NOT_FOUND);
    }

    public UserOrganizationNotFoundException(String identifier) {
        super(AuthErrorCode.ATH_USER_ORG_NOT_FOUND,
              AuthErrorCode.ATH_USER_ORG_NOT_FOUND.getMessage() + ": " + identifier);
    }

    public static UserOrganizationNotFoundException byId(String id) {
        return new UserOrganizationNotFoundException(id);
    }

    public static UserOrganizationNotFoundException byId(Long id) {
        return new UserOrganizationNotFoundException(String.valueOf(id));
    }

    public static UserOrganizationNotFoundException byUserAndOrg(String userId, String organizationId) {
        return new UserOrganizationNotFoundException("userId=" + userId + ", organizationId=" + organizationId);
    }

    public static UserOrganizationNotFoundException primaryNotFound(String userId) {
        return new UserOrganizationNotFoundException("사용자 주 소속을 찾을 수 없습니다: " + userId);
    }
}
