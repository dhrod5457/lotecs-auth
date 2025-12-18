package lotecs.auth.exception.tenant;

import lotecs.auth.exception.AuthErrorCode;
import lotecs.auth.exception.AuthException;

/**
 * 테넌트 상태 전이 규칙 위반 시 발생하는 예외.
 * HTTP 422 Unprocessable Entity로 매핑됩니다.
 */
public class TenantStateException extends AuthException {

    private static final long serialVersionUID = 1L;

    private TenantStateException(String message) {
        super(AuthErrorCode.ATH_TENANT_STATUS_INVALID, message);
    }

    public static TenantStateException cannotPublish(String currentStatus) {
        return new TenantStateException(
            "DRAFT 상태에서만 게시할 수 있습니다. 현재 상태: " + currentStatus
        );
    }

    public static TenantStateException cannotUnpublish(String currentStatus) {
        return new TenantStateException(
            "PUBLISHED 상태에서만 게시 중단할 수 있습니다. 현재 상태: " + currentStatus
        );
    }

    public static TenantStateException cannotSuspend(String currentStatus) {
        return new TenantStateException(
            "PUBLISHED 상태에서만 일시중지할 수 있습니다. 현재 상태: " + currentStatus
        );
    }

    public static TenantStateException cannotResume(String currentStatus) {
        return new TenantStateException(
            "SUSPENDED 상태에서만 재개할 수 있습니다. 현재 상태: " + currentStatus
        );
    }

    public static TenantStateException cannotArchive(String currentStatus) {
        return new TenantStateException(
            "DRAFT 또는 SUSPENDED 상태에서만 보관할 수 있습니다. 현재 상태: " + currentStatus
        );
    }
}
