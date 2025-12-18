package lotecs.auth.exception;

import lombok.Getter;
import lotecs.framework.common.exception.core.BaseModuleException;

/**
 * lotecs-auth 서비스 최상위 예외 클래스.
 * 모든 Auth 관련 예외는 이 클래스를 상속받습니다.
 */
@Getter
public class AuthException extends BaseModuleException {

    private static final long serialVersionUID = 1L;

    public static final String MODULE_CODE = "ATH";
    public static final String DEFAULT_ERROR_CODE = "ATH_ERROR";

    private final AuthErrorCode authErrorCode;

    public AuthException(AuthErrorCode errorCode) {
        super(MODULE_CODE, errorCode.getCode(), errorCode.getMessage());
        this.authErrorCode = errorCode;
    }

    public AuthException(AuthErrorCode errorCode, String message) {
        super(MODULE_CODE, errorCode.getCode(), message);
        this.authErrorCode = errorCode;
    }

    public AuthException(AuthErrorCode errorCode, String message, Throwable cause) {
        super(MODULE_CODE, errorCode.getCode(), message, cause);
        this.authErrorCode = errorCode;
    }

    public AuthException(AuthErrorCode errorCode, Throwable cause) {
        super(MODULE_CODE, errorCode.getCode(), errorCode.getMessage(), cause);
        this.authErrorCode = errorCode;
    }

    @Override
    public String getDefaultErrorCode() {
        return DEFAULT_ERROR_CODE;
    }

    @Override
    public String getModuleCode() {
        return MODULE_CODE;
    }
}
