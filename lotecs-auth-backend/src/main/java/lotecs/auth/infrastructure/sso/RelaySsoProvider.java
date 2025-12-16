package lotecs.auth.infrastructure.sso;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.sso.model.TenantSsoConfig;
import lotecs.auth.domain.sso.repository.TenantSsoConfigRepository;
import lotecs.auth.domain.sso.SsoAuthRequest;
import lotecs.auth.domain.sso.SsoAuthResult;
import lotecs.auth.domain.sso.SsoProvider;
import lotecs.auth.infrastructure.relay.RelayAuthRequest;
import lotecs.auth.infrastructure.relay.RelayAuthResponse;
import lotecs.auth.infrastructure.relay.RelayClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component("relay")
@RequiredArgsConstructor
public class RelaySsoProvider implements SsoProvider {

    private final TenantSsoConfigRepository ssoConfigRepository;
    private final RelayClient relayClient;

    @Override
    public SsoAuthResult authenticate(SsoAuthRequest request) {
        try {
            TenantSsoConfig config = ssoConfigRepository.findByTenantId(request.tenantId())
                    .orElseThrow(() -> new IllegalStateException("SSO configuration not found for tenant: " + request.tenantId()));

            if (config.getRelayEndpoint() == null || config.getRelayEndpoint().isBlank()) {
                log.error("Relay endpoint is not configured for tenant: {}", request.tenantId());
                return SsoAuthResult.failure("CONFIG_ERROR", "Relay endpoint is not configured");
            }

            log.debug("Authenticating user {} via Relay for tenant {}", request.username(), request.tenantId());

            RelayAuthRequest relayRequest = new RelayAuthRequest(
                    request.tenantId(),
                    request.username(),
                    request.password()
            );

            RelayAuthResponse relayResponse = relayClient.authenticate(config.getRelayEndpoint(), relayRequest);

            if (relayResponse.success()) {
                log.info("Successfully authenticated user {} via Relay for tenant {}", request.username(), request.tenantId());
                return SsoAuthResult.success(
                        relayResponse.externalUserId(),
                        relayResponse.username(),
                        relayResponse.email(),
                        relayResponse.fullName(),
                        relayResponse.roles()
                );
            } else {
                log.warn("Relay authentication failed for user {}: {} - {}",
                        request.username(), relayResponse.errorCode(), relayResponse.errorMessage());
                return SsoAuthResult.failure(
                        relayResponse.errorCode(),
                        relayResponse.errorMessage()
                );
            }

        } catch (IllegalStateException e) {
            log.error("Configuration error: {}", e.getMessage());
            return SsoAuthResult.failure("CONFIG_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("Relay authentication failed for user {} in tenant {}: {}",
                    request.username(), request.tenantId(), e.getMessage(), e);
            return SsoAuthResult.failure("CONNECTION_ERROR", "Connection to Relay failed: " + e.getMessage());
        }
    }
}
