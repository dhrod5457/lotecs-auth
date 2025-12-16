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
            case RELAY -> providers.get("relay");
            case KEYCLOAK -> providers.get("keycloak");
            case LDAP -> providers.get("ldap");
            case INTERNAL -> throw new UnsupportedOperationException(
                    "INTERNAL SSO type is not supported by SsoProviderFactory"
            );
            case EXTERNAL -> throw new UnsupportedOperationException(
                    "EXTERNAL SSO type is not supported by SsoProviderFactory"
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
