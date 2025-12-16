package lotecs.auth.infrastructure.sso;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.sso.model.TenantSsoConfig;
import lotecs.auth.domain.sso.repository.TenantSsoConfigRepository;
import lotecs.auth.domain.sso.SsoAuthRequest;
import lotecs.auth.domain.sso.SsoAuthResult;
import lotecs.auth.domain.sso.SsoProvider;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component("keycloak")
@RequiredArgsConstructor
public class KeycloakSsoProvider implements SsoProvider {

    private final TenantSsoConfigRepository ssoConfigRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public SsoAuthResult authenticate(SsoAuthRequest request) {
        try {
            TenantSsoConfig config = ssoConfigRepository.findByTenantId(request.getTenantId())
                    .orElseThrow(() -> new IllegalStateException("SSO configuration not found for tenant: " + request.getTenantId()));

            if (config.getSsoServerUrl() == null || config.getSsoRealm() == null || config.getSsoClientId() == null) {
                log.error("Keycloak configuration is incomplete for tenant: {}", request.getTenantId());
                return SsoAuthResult.failure("CONFIG_ERROR", "Keycloak configuration is incomplete");
            }

            log.debug("Authenticating user {} via Keycloak for tenant {}", request.getUsername(), request.getTenantId());

            // Build Keycloak client with Password Grant
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(config.getSsoServerUrl())
                    .realm(config.getSsoRealm())
                    .clientId(config.getSsoClientId())
                    .clientSecret(config.getSsoClientSecret())
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .build();

            // Obtain Access Token
            AccessTokenResponse tokenResponse;
            try {
                tokenResponse = keycloak.tokenManager().getAccessToken();
            } catch (Exception e) {
                log.error("Failed to obtain access token from Keycloak: {}", e.getMessage());
                return SsoAuthResult.failure("INVALID_CREDENTIALS", "Authentication failed: " + e.getMessage());
            }

            // Parse Access Token to extract user information
            String accessToken = tokenResponse.getToken();
            String[] tokenParts = accessToken.split("\\.");
            if (tokenParts.length < 2) {
                log.error("Invalid access token format");
                return SsoAuthResult.failure("CONFIG_ERROR", "Invalid access token format");
            }

            // Decode JWT payload (base64url)
            String payload = new String(Base64.getUrlDecoder().decode(tokenParts[1]));
            JsonNode claims = objectMapper.readTree(payload);

            // Extract user information from claims
            String externalUserId = claims.has("sub") ? claims.get("sub").asText() : null;
            String username = claims.has("preferred_username") ? claims.get("preferred_username").asText() : request.getUsername();
            String email = claims.has("email") ? claims.get("email").asText() : null;
            String fullName = claims.has("name") ? claims.get("name").asText() : username;

            // Extract roles from token claims
            // TODO: Role mapping configuration should be externalized
            List<String> roles = new ArrayList<>();
            if (claims.has("realm_access") && claims.get("realm_access").has("roles")) {
                JsonNode rolesNode = claims.get("realm_access").get("roles");
                rolesNode.forEach(role -> roles.add(role.asText()));
            }

            log.info("Successfully authenticated user {} via Keycloak for tenant {}", username, request.getTenantId());

            return SsoAuthResult.success(externalUserId, username, email, fullName, roles, null);

        } catch (IllegalStateException e) {
            log.error("Configuration error: {}", e.getMessage());
            return SsoAuthResult.failure("CONFIG_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("Keycloak authentication failed for user {} in tenant {}: {}",
                    request.getUsername(), request.getTenantId(), e.getMessage(), e);
            return SsoAuthResult.failure("CONNECTION_ERROR", "Connection to Keycloak failed: " + e.getMessage());
        }
    }
}
