package lotecs.auth.domain.sso;

/**
 * SSO 인증 제공자 인터페이스.
 * 각 SSO 타입별로 구현체를 제공한다.
 */
public interface SsoProvider {

    /**
     * SSO 인증을 수행한다.
     *
     * @param request 인증 요청 정보
     * @return 인증 결과
     */
    SsoAuthResult authenticate(SsoAuthRequest request);

    /**
     * SSO 로그인 URL을 생성한다.
     * 프론트엔드에서 SSO 서버로 리다이렉트할 때 사용.
     *
     * @param callbackUrl 인증 후 돌아올 콜백 URL
     * @return SSO 로그인 URL
     */
    default String buildLoginUrl(String callbackUrl) {
        throw new UnsupportedOperationException("buildLoginUrl is not supported by this provider");
    }

    /**
     * SSO 로그아웃 URL을 생성한다.
     *
     * @param callbackUrl 로그아웃 후 돌아올 콜백 URL
     * @return SSO 로그아웃 URL
     */
    default String buildLogoutUrl(String callbackUrl) {
        throw new UnsupportedOperationException("buildLogoutUrl is not supported by this provider");
    }

    /**
     * 해당 SSO 타입을 반환한다.
     *
     * @return SSO 타입
     */
    default SsoType getSsoType() {
        return SsoType.INTERNAL;
    }
}
