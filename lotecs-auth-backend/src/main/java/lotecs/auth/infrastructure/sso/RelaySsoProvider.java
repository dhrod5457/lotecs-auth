package lotecs.auth.infrastructure.sso;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.sso.model.TenantSsoConfig;
import lotecs.auth.domain.sso.repository.TenantSsoConfigRepository;
import lotecs.auth.domain.sso.SsoAuthRequest;
import lotecs.auth.domain.sso.SsoAuthResult;
import lotecs.auth.domain.sso.SsoProvider;
import lotecs.relay.sdk.auth.RelayAuthClient;
import lotecs.relay.sdk.auth.RelayAuthRequest;
import lotecs.relay.sdk.auth.RelayAuthResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component("relay")
@RequiredArgsConstructor
public class RelaySsoProvider implements SsoProvider {

    private final TenantSsoConfigRepository ssoConfigRepository;
    private final RelayAuthClient relayClient;

    @Override
    public SsoAuthResult authenticate(SsoAuthRequest request) {
        try {
            TenantSsoConfig config = ssoConfigRepository.findByTenantId(request.getTenantId())
                    .orElseThrow(() -> new IllegalStateException("SSO configuration not found for tenant: " + request.getTenantId()));

            if (config.getRelayEndpoint() == null || config.getRelayEndpoint().isBlank()) {
                log.error("Relay endpoint is not configured for tenant: {}", request.getTenantId());
                return SsoAuthResult.failure("CONFIG_ERROR", "Relay endpoint is not configured");
            }

            log.debug("Authenticating user {} via Relay for tenant {}", request.getUsername(), request.getTenantId());

            RelayAuthRequest relayRequest = RelayAuthRequest.builder()
                    .tenantId(request.getTenantId())
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .ipAddress(request.getIpAddress())
                    .build();

            RelayAuthResponse relayResponse = relayClient.authenticate(config.getRelayEndpoint(), relayRequest);

            if (relayResponse.isSuccess()) {
                log.info("Successfully authenticated user {} via Relay for tenant {}", request.getUsername(), request.getTenantId());
                return SsoAuthResult.success(
                        relayResponse.getExternalUserId(),
                        relayResponse.getUsername(),
                        relayResponse.getEmail(),
                        relayResponse.getFullName(),
                        relayResponse.getRoles(),
                        relayResponse.getAdditionalData()
                );
            } else {
                log.warn("Relay authentication failed for user {}: {} - {}",
                        request.getUsername(), relayResponse.getErrorCode(), relayResponse.getErrorMessage());
                return SsoAuthResult.failure(
                        relayResponse.getErrorCode(),
                        relayResponse.getErrorMessage()
                );
            }

        } catch (IllegalStateException e) {
            log.error("Configuration error: {}", e.getMessage());
            return SsoAuthResult.failure("CONFIG_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("Relay authentication failed for user {} in tenant {}: {}",
                    request.getUsername(), request.getTenantId(), e.getMessage(), e);
            return SsoAuthResult.failure("CONNECTION_ERROR", "Connection to Relay failed: " + e.getMessage());
        }
    }
}
