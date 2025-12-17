package lotecs.auth.domain.sso.exception;

/**
 * SSO 서버 연결 실패 예외.
 * 타임아웃, 네트워크 오류, HTTP 5xx 오류 등 SSO 서버와의 통신 실패 시 발생.
 * 이 예외가 발생하면 fallback 로직이 트리거될 수 있다.
 */
public class SsoConnectionException extends RuntimeException {

    private final SsoConnectionErrorType errorType;
    private final Integer httpStatusCode;

    public SsoConnectionException(String message, SsoConnectionErrorType errorType) {
        super(message);
        this.errorType = errorType;
        this.httpStatusCode = null;
    }

    public SsoConnectionException(String message, SsoConnectionErrorType errorType, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
        this.httpStatusCode = null;
    }

    public SsoConnectionException(String message, SsoConnectionErrorType errorType, int httpStatusCode) {
        super(message);
        this.errorType = errorType;
        this.httpStatusCode = httpStatusCode;
    }

    public SsoConnectionException(String message, SsoConnectionErrorType errorType, int httpStatusCode, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
        this.httpStatusCode = httpStatusCode;
    }

    public SsoConnectionErrorType getErrorType() {
        return errorType;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    /**
     * 연결 타임아웃 예외 생성
     */
    public static SsoConnectionException timeout(String message) {
        return new SsoConnectionException(message, SsoConnectionErrorType.TIMEOUT);
    }

    public static SsoConnectionException timeout(String message, Throwable cause) {
        return new SsoConnectionException(message, SsoConnectionErrorType.TIMEOUT, cause);
    }

    /**
     * 네트워크 오류 예외 생성
     */
    public static SsoConnectionException networkError(String message) {
        return new SsoConnectionException(message, SsoConnectionErrorType.NETWORK_ERROR);
    }

    public static SsoConnectionException networkError(String message, Throwable cause) {
        return new SsoConnectionException(message, SsoConnectionErrorType.NETWORK_ERROR, cause);
    }

    /**
     * HTTP 서버 오류 예외 생성 (5xx)
     */
    public static SsoConnectionException serverError(String message, int httpStatusCode) {
        return new SsoConnectionException(message, SsoConnectionErrorType.SERVER_ERROR, httpStatusCode);
    }

    public static SsoConnectionException serverError(String message, int httpStatusCode, Throwable cause) {
        return new SsoConnectionException(message, SsoConnectionErrorType.SERVER_ERROR, httpStatusCode, cause);
    }

    /**
     * SSO 서버 응답 오류 예외 생성
     */
    public static SsoConnectionException responseError(String message) {
        return new SsoConnectionException(message, SsoConnectionErrorType.RESPONSE_ERROR);
    }

    public static SsoConnectionException responseError(String message, Throwable cause) {
        return new SsoConnectionException(message, SsoConnectionErrorType.RESPONSE_ERROR, cause);
    }

    /**
     * Fallback 가능 여부 확인
     */
    public boolean isFallbackable() {
        return errorType.isFallbackable();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SsoConnectionException{");
        sb.append("errorType=").append(errorType);
        if (httpStatusCode != null) {
            sb.append(", httpStatusCode=").append(httpStatusCode);
        }
        sb.append(", message='").append(getMessage()).append("'");
        sb.append('}');
        return sb.toString();
    }
}
