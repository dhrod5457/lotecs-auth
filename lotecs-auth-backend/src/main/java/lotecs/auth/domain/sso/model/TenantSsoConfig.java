package lotecs.auth.domain.sso.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.sso.SsoType;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantSsoConfig {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @NotBlank
    private String tenantId;

    @NotNull
    private SsoType ssoType;

    @NotNull
    @Builder.Default
    private Boolean ssoEnabled = false;

    // 공통 SSO 서버 설정
    private String ssoServerUrl;
    private String ssoClientId;
    private String ssoClientSecret;

    // KEYCLOAK 전용
    private String ssoRealm;

    // JWT_SSO 전용
    private String jwtSecretKey;
    private String jwtAgentId;
    private Integer jwtExpirationSeconds;

    // CAS 전용
    private String casValidateEndpoint;
    private String casServiceUrl;

    // REST_TOKEN 전용
    private String restTokenEndpoint;
    private String restConnectEndpoint;
    private String restCreateState;
    private String restVerifyState;

    // HTTP_FORM 전용
    private String httpFormConfirmEndpoint;
    private String httpFormIdParam;
    private String httpFormPasswordParam;
    @Builder.Default
    private Boolean httpFormEncodePassword = true;

    // 공통 설정
    private String loginEndpoint;
    private String logoutEndpoint;
    @Builder.Default
    private Integer readTimeoutMs = 5000;

    @NotNull
    @Builder.Default
    private Boolean userSyncEnabled = false;

    @NotNull
    @Builder.Default
    private Boolean roleMappingEnabled = false;

    // 추가 설정 (JSON)
    private String additionalConfig;

    // Deprecated - RELAY용 (하위 호환성)
    @Deprecated
    private String relayEndpoint;
    @Deprecated
    private Integer relayTimeoutMs;

    // Fallback 설정
    @NotNull
    @Builder.Default
    private Boolean fallbackEnabled = false;

    @NotNull
    @Builder.Default
    private Boolean fallbackPasswordRequired = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isRoleMappingEnabled() {
        return Boolean.TRUE.equals(this.roleMappingEnabled);
    }

    public boolean isUserSyncEnabled() {
        return Boolean.TRUE.equals(this.userSyncEnabled);
    }

    public boolean isHttpFormEncodePassword() {
        return Boolean.TRUE.equals(this.httpFormEncodePassword);
    }

    public boolean isFallbackEnabled() {
        return Boolean.TRUE.equals(this.fallbackEnabled);
    }

    public boolean isFallbackPasswordRequired() {
        return Boolean.TRUE.equals(this.fallbackPasswordRequired);
    }

    /**
     * additionalConfig JSON을 Map으로 파싱
     */
    public Map<String, Object> getAdditionalConfigAsMap() {
        if (additionalConfig == null || additionalConfig.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(additionalConfig, new TypeReference<>() {});
        } catch (Exception e) {
            log.warn("Failed to parse additionalConfig: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * additionalConfig에서 특정 키 값 조회
     */
    public String getAdditionalConfigValue(String key) {
        Map<String, Object> config = getAdditionalConfigAsMap();
        Object value = config.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 사용자 구분 매핑 조회 (REST_TOKEN용)
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getUserDivisionMapping() {
        Map<String, Object> config = getAdditionalConfigAsMap();
        Object mapping = config.get("userDivisionMapping");
        if (mapping instanceof Map) {
            return (Map<String, String>) mapping;
        }
        return Collections.emptyMap();
    }

    /**
     * 사용자 구분 prefix 매핑 조회 (REST_TOKEN용)
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getUserDivisionPrefix() {
        Map<String, Object> config = getAdditionalConfigAsMap();
        Object prefix = config.get("userDivisionPrefix");
        if (prefix instanceof Map) {
            return (Map<String, String>) prefix;
        }
        return Collections.emptyMap();
    }

    @Deprecated
    public String getRelayEndpoint() {
        return this.relayEndpoint;
    }
}
