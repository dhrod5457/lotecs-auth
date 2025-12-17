package lotecs.auth.domain.sso.exception;

/**
 * SSO 연결 오류 유형
 */
public enum SsoConnectionErrorType {

    /**
     * 연결 타임아웃
     */
    TIMEOUT(true),

    /**
     * 네트워크 오류 (DNS 실패, 연결 거부 등)
     */
    NETWORK_ERROR(true),

    /**
     * HTTP 서버 오류 (5xx)
     */
    SERVER_ERROR(true),

    /**
     * SSO 서버 응답 파싱 오류
     */
    RESPONSE_ERROR(true),

    /**
     * 설정 오류 (fallback 불가)
     */
    CONFIG_ERROR(false),

    /**
     * 인증 오류 (잘못된 자격 증명 - fallback 불가)
     */
    AUTH_ERROR(false);

    private final boolean fallbackable;

    SsoConnectionErrorType(boolean fallbackable) {
        this.fallbackable = fallbackable;
    }

    /**
     * 이 오류 유형이 fallback을 트리거할 수 있는지 여부
     */
    public boolean isFallbackable() {
        return fallbackable;
    }
}
