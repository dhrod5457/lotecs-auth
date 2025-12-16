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
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component("ldap")
@RequiredArgsConstructor
public class LdapSsoProvider implements SsoProvider {

    private final TenantSsoConfigRepository ssoConfigRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public SsoAuthResult authenticate(SsoAuthRequest request) {
        LdapConnection connection = null;
        try {
            TenantSsoConfig config = ssoConfigRepository.findByTenantId(request.getTenantId())
                    .orElseThrow(() -> new IllegalStateException("SSO configuration not found for tenant: " + request.getTenantId()));

            if (config.getAdditionalConfig() == null) {
                log.error("LDAP configuration is missing for tenant: {}", request.getTenantId());
                return SsoAuthResult.failure("CONFIG_ERROR", "LDAP configuration is missing");
            }

            // Parse additionalConfig JSON for ldap_url and base_dn
            JsonNode additionalConfig = objectMapper.readTree(config.getAdditionalConfig());
            String ldapUrl = additionalConfig.has("ldap_url") ? additionalConfig.get("ldap_url").asText() : null;
            String baseDn = additionalConfig.has("base_dn") ? additionalConfig.get("base_dn").asText() : null;

            if (ldapUrl == null || baseDn == null) {
                log.error("LDAP configuration is incomplete (ldap_url or base_dn missing) for tenant: {}", request.getTenantId());
                return SsoAuthResult.failure("CONFIG_ERROR", "LDAP configuration is incomplete");
            }

            log.debug("Authenticating user {} via LDAP for tenant {}", request.getUsername(), request.getTenantId());

            // Parse LDAP URL
            URI uri = new URI(ldapUrl);
            String host = uri.getHost();
            int port = uri.getPort() > 0 ? uri.getPort() : (uri.getScheme().equals("ldaps") ? 636 : 389);
            boolean useSsl = uri.getScheme().equals("ldaps");

            // TODO: Add support for LDAP connection pooling
            // TODO: Add support for StartTLS
            // Connect to LDAP server
            connection = new LdapNetworkConnection(host, port, useSsl);
            connection.setTimeOut(10000); // 10 second timeout

            // Search for user DN by username
            // TODO: Make search filter configurable (currently hardcoded to uid)
            String searchFilter = String.format("(uid=%s)", request.getUsername());
            String userDn = null;

            try {
                connection.bind();
                EntryCursor cursor = connection.search(baseDn, searchFilter, SearchScope.SUBTREE);

                if (cursor.next()) {
                    Entry entry = cursor.get();
                    userDn = entry.getDn().getName();
                } else {
                    log.warn("User {} not found in LDAP for tenant {}", request.getUsername(), request.getTenantId());
                    connection.unBind();
                    return SsoAuthResult.failure("USER_NOT_FOUND", "User not found in LDAP directory");
                }

                cursor.close();
                connection.unBind();
            } catch (Exception e) {
                log.error("LDAP search failed for user {}: {}", request.getUsername(), e.getMessage());
                return SsoAuthResult.failure("CONNECTION_ERROR", "LDAP search failed: " + e.getMessage());
            }

            // Authenticate by binding with user DN and password
            try {
                connection.bind(userDn, request.getPassword());
                log.info("Successfully authenticated user {} via LDAP for tenant {}", request.getUsername(), request.getTenantId());
            } catch (LdapException e) {
                log.warn("LDAP authentication failed for user {}: {}", request.getUsername(), e.getMessage());
                return SsoAuthResult.failure("INVALID_CREDENTIALS", "Invalid username or password");
            }

            // Retrieve user attributes
            String email = null;
            String fullName = null;
            // TODO: Add support for group-based role mapping
            List<String> roles = new ArrayList<>();

            try {
                EntryCursor cursor = connection.search(userDn, "(objectClass=*)", SearchScope.OBJECT, "mail", "cn", "displayName");

                if (cursor.next()) {
                    Entry entry = cursor.get();

                    if (entry.get("mail") != null) {
                        email = entry.get("mail").getString();
                    }

                    if (entry.get("displayName") != null) {
                        fullName = entry.get("displayName").getString();
                    } else if (entry.get("cn") != null) {
                        fullName = entry.get("cn").getString();
                    }
                }

                cursor.close();
            } catch (Exception e) {
                log.error("Failed to retrieve LDAP user attributes for user {}: {}", request.getUsername(), e.getMessage());
                // Continue with authentication even if attribute retrieval fails
            }

            // Use username as external user ID (LDAP DN could be used instead)
            String externalUserId = userDn;

            return SsoAuthResult.success(externalUserId, request.getUsername(), email, fullName, roles);

        } catch (IllegalStateException e) {
            log.error("Configuration error: {}", e.getMessage());
            return SsoAuthResult.failure("CONFIG_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("LDAP authentication failed for user {} in tenant {}: {}",
                    request.getUsername(), request.getTenantId(), e.getMessage(), e);
            return SsoAuthResult.failure("CONNECTION_ERROR", "Connection to LDAP failed: " + e.getMessage());
        } finally {
            // Close connection
            if (connection != null && connection.isConnected()) {
                try {
                    connection.unBind();
                    connection.close();
                } catch (IOException | LdapException e) {
                    log.warn("Failed to close LDAP connection: {}", e.getMessage());
                }
            }
        }
    }
}
