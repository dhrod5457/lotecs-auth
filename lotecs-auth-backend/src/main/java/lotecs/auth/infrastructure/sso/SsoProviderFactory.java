package lotecs.auth.infrastructure.sso;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.sso.SsoProvider;
import lotecs.auth.domain.sso.SsoType;
import lotecs.auth.domain.sso.model.TenantSsoConfig;
import lotecs.auth.domain.user.repository.UserProfileRepository;
import lotecs.auth.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SsoProviderFactory {

    private final Map<String, SsoProvider> providers;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * SSO 타입에 해당하는 Provider를 반환한다.
     * Fallback 기능이 필요한 경우 getProviderWithFallback을 사용한다.
     */
    public SsoProvider getProvider(SsoType ssoType) {
        log.debug("Retrieving SSO provider for type: {}", ssoType);

        SsoProvider provider = switch (ssoType) {
            case KEYCLOAK -> providers.get("keycloak");
            case LDAP -> providers.get("ldap");
            case JWT_SSO -> providers.get("jwt_sso");
            case CAS -> providers.get("cas");
            case REST_TOKEN -> providers.get("rest_token");
            case HTTP_FORM -> providers.get("http_form");
            case INTERNAL -> throw new UnsupportedOperationException(
                    "INTERNAL SSO type is not supported by SsoProviderFactory"
            );
            case RELAY -> throw new UnsupportedOperationException(
                    "RELAY SSO type is deprecated. Please migrate to JWT_SSO, CAS, REST_TOKEN, or HTTP_FORM"
            );
            case EXTERNAL -> throw new UnsupportedOperationException(
                    "EXTERNAL SSO type is deprecated and not supported"
            );
        };

        if (provider == null) {
            log.error("No provider found for SSO type: {}", ssoType);
            throw new IllegalStateException("SSO provider not found for type: " + ssoType);
        }

        log.debug("Successfully retrieved provider: {} for SSO type: {}", provider.getClass().getSimpleName(), ssoType);
        return provider;
    }

    /**
     * Fallback 기능이 적용된 SSO Provider를 반환한다.
     * SSO 서버 연결 실패 시 Internal DB로 폴백하여 인증을 시도한다.
     *
     * @param ssoConfig 테넌트 SSO 설정 (fallbackEnabled 여부 포함)
     * @return Fallback이 적용된 SsoProvider
     */
    public SsoProvider getProviderWithFallback(TenantSsoConfig ssoConfig) {
        SsoProvider baseProvider = getProvider(ssoConfig.getSsoType());

        // Fallback이 비활성화되어 있으면 기본 Provider 반환
        if (!ssoConfig.isFallbackEnabled()) {
            log.debug("Fallback disabled for tenant: {}, returning base provider", ssoConfig.getTenantId());
            return baseProvider;
        }

        // Fallback이 활성화되어 있으면 FallbackAwareSsoProvider로 래핑
        log.debug("Fallback enabled for tenant: {}, wrapping with FallbackAwareSsoProvider", ssoConfig.getTenantId());
        return new FallbackAwareSsoProvider(
                baseProvider,
                ssoConfig,
                userRepository,
                userProfileRepository,
                passwordEncoder
        );
    }
}
