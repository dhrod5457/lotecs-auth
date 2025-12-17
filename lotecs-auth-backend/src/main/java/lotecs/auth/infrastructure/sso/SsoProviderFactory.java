package lotecs.auth.infrastructure.sso;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.sso.SsoProvider;
import lotecs.auth.domain.sso.SsoType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SsoProviderFactory {

    private final Map<String, SsoProvider> providers;

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
}
