package lotecs.auth.infrastructure.sso;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.sso.SsoAuthRequest;
import lotecs.auth.domain.sso.SsoAuthResult;
import lotecs.auth.domain.sso.SsoProvider;
import lotecs.auth.domain.sso.SsoType;
import lotecs.auth.domain.sso.model.TenantSsoConfig;
import lotecs.auth.domain.sso.repository.TenantSsoConfigRepository;
import org.springframework.stereotype.Component;

import lotecs.auth.domain.sso.exception.SsoConnectionException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * REST API + 토큰 방식 SSO 제공자 (bu, bscu 방식).
 * clientId/secret으로 시스템 토큰 발급 후 사용자 검증.
 */
@Slf4j
@Component("rest_token")
public class RestTokenSsoProvider implements SsoProvider {

    private final TenantSsoConfigRepository ssoConfigRepository;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public RestTokenSsoProvider(TenantSsoConfigRepository ssoConfigRepository, ObjectMapper objectMapper) {
        this.ssoConfigRepository = ssoConfigRepository;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        log.debug("RestTokenSsoProvider initialized");
    }

    @Override
    public SsoAuthResult authenticate(SsoAuthRequest request) {
        log.debug("REST_TOKEN SSO authentication for user: {} in tenant: {}", request.getUsername(), request.getTenantId());

        try {
            TenantSsoConfig config = ssoConfigRepository.findByTenantId(request.getTenantId())
                    .orElseThrow(() -> new IllegalStateException("SSO configuration not found for tenant: " + request.getTenantId()));

            if (config.getSsoServerUrl() == null || config.getSsoServerUrl().isBlank()) {
                log.error("SSO server URL is not configured for tenant: {}", request.getTenantId());
                return SsoAuthResult.failure("CONFIG_ERROR", "SSO server URL is not configured");
            }

            if (config.getSsoClientId() == null || config.getSsoClientSecret() == null) {
                log.error("SSO client credentials are not configured for tenant: {}", request.getTenantId());
                return SsoAuthResult.failure("CONFIG_ERROR", "SSO client credentials are not configured");
            }

            // 시스템 토큰 발급
            String accessToken = request.getSsoToken();
            if (accessToken == null || accessToken.isBlank()) {
                accessToken = getSystemToken(config);
            }

            // 사용자 인증
            return authenticateUser(request, config, accessToken);

        } catch (IllegalStateException e) {
            log.error("Configuration error: {}", e.getMessage());
            return SsoAuthResult.failure("CONFIG_ERROR", e.getMessage());
        } catch (SsoConnectionException e) {
            // 연결 오류는 그대로 전파 (Fallback 처리용)
            throw e;
        } catch (HttpTimeoutException e) {
            log.error("REST_TOKEN SSO timeout for user {} in tenant {}: {}",
                    request.getUsername(), request.getTenantId(), e.getMessage());
            throw SsoConnectionException.timeout("SSO 서버 연결 타임아웃: " + e.getMessage(), e);
        } catch (ConnectException e) {
            log.error("REST_TOKEN SSO connection refused for user {} in tenant {}: {}",
                    request.getUsername(), request.getTenantId(), e.getMessage());
            throw SsoConnectionException.networkError("SSO 서버 연결 실패: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("REST_TOKEN SSO IO error for user {} in tenant {}: {}",
                    request.getUsername(), request.getTenantId(), e.getMessage(), e);
            throw SsoConnectionException.networkError("SSO 서버 통신 오류: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("REST_TOKEN SSO interrupted for user {} in tenant {}",
                    request.getUsername(), request.getTenantId());
            throw SsoConnectionException.networkError("SSO 인증 중단됨", e);
        } catch (Exception e) {
            log.error("REST_TOKEN SSO authentication failed for user {} in tenant {}: {}",
                    request.getUsername(), request.getTenantId(), e.getMessage(), e);
            return SsoAuthResult.failure("AUTH_ERROR", "REST_TOKEN 인증 실패: " + e.getMessage());
        }
    }

    private String getSystemToken(TenantSsoConfig config) throws Exception {
        log.debug("Requesting system token from SSO server");

        String tokenEndpoint = config.getRestTokenEndpoint() != null
                ? config.getRestTokenEndpoint()
                : "/api/token";
        String url = config.getSsoServerUrl() + tokenEndpoint;

        String createState = config.getRestCreateState() != null ? config.getRestCreateState() : "create";

        String formData = buildFormData(Map.of(
                "state", createState,
                "id", config.getSsoClientId(),
                "secret_key", config.getSsoClientSecret()
        ));

        int timeout = config.getReadTimeoutMs() != null ? config.getReadTimeoutMs() : 5000;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(timeout))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 500) {
            throw SsoConnectionException.serverError(
                    "SSO 서버 오류 (토큰 발급): HTTP " + response.statusCode(),
                    response.statusCode()
            );
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("SSO server returned status: " + response.statusCode());
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> result = objectMapper.readValue(response.body(), Map.class);
        String accessToken = (String) result.get("access_token");

        if (accessToken == null || accessToken.isBlank()) {
            throw new RuntimeException("No access_token in response");
        }

        log.debug("System token acquired successfully");
        return accessToken;
    }

    private SsoAuthResult authenticateUser(SsoAuthRequest request, TenantSsoConfig config, String accessToken) throws Exception {
        String tokenEndpoint = config.getRestTokenEndpoint() != null
                ? config.getRestTokenEndpoint()
                : "/api/token";
        String url = config.getSsoServerUrl() + tokenEndpoint;

        String verifyState = config.getRestVerifyState() != null ? config.getRestVerifyState() : "verify";

        // 사용자 구분 매핑
        String userDiv = mapUserDivision(request.getUserDivision(), request.getUniversityUserDivision(), config);

        Map<String, String> params = new LinkedHashMap<>();
        params.put("state", verifyState);
        params.put("id", config.getSsoClientId());
        params.put("secret_key", config.getSsoClientSecret());
        params.put("access_token", accessToken);
        params.put("user_id", request.getUsername());
        params.put("user_password", request.getPassword());
        params.put("is_mobile", request.isMobile() ? "Y" : "N");

        if (userDiv != null) {
            params.put("userDiv", userDiv);
        }

        // 추가 파라미터
        if (request.getExtraParams() != null) {
            params.putAll(request.getExtraParams());
        }

        String formData = buildFormData(params);
        int timeout = config.getReadTimeoutMs() != null ? config.getReadTimeoutMs() : 5000;

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(timeout))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 500) {
            throw SsoConnectionException.serverError(
                    "SSO 서버 오류 (사용자 인증): HTTP " + response.statusCode(),
                    response.statusCode()
            );
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> result = objectMapper.readValue(response.body(), Map.class);

        String userInfo = (String) result.get("user_info");
        if (userInfo == null || userInfo.isBlank()) {
            log.warn("SSO login failed for user: {}", request.getUsername());
            return SsoAuthResult.failure("LOGIN_FAILED", "SSO 로그인 실패");
        }

        log.info("SSO login successful for user: {}", request.getUsername());
        return SsoAuthResult.success(
                request.getUsername(),
                request.getUsername(),
                null,
                null,
                Collections.emptyList(),
                Map.of(
                        "accessToken", accessToken,
                        "userInfo", userInfo,
                        "data", result.get("data") != null ? result.get("data").toString() : ""
                )
        );
    }

    private String mapUserDivision(String userDivision, String univUserDivision, TenantSsoConfig config) {
        if (userDivision == null) {
            return null;
        }

        // 설정에서 매핑 조회
        Map<String, String> mapping = config.getUserDivisionMapping();
        if (mapping != null && !mapping.isEmpty()) {
            // 시간강사 등 특수 케이스 처리
            if (univUserDivision != null) {
                String key = userDivision + "_" + univUserDivision;
                if (mapping.containsKey(key)) {
                    return mapping.get(key);
                }
            }
            return mapping.get(userDivision);
        }

        // 기본 매핑 (bu 방식)
        return switch (userDivision) {
            case "1" -> "1";      // 학부생
            case "2" -> "INSTR".equals(univUserDivision) ? "4" : "5";  // 시간강사/교원
            case "3" -> "5";      // 직원
            case "6" -> "2";      // 대학원
            case "7" -> "3";      // 평생교육원
            default -> userDivision;
        };
    }

    private String buildFormData(Map<String, String> params) {
        StringJoiner joiner = new StringJoiner("&");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            joiner.add(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)
                    + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return joiner.toString();
    }

    @Override
    public String buildLoginUrl(String callbackUrl) {
        String encodedCallback = URLEncoder.encode(callbackUrl, StandardCharsets.UTF_8);
        return "/sso/rest-token/login?service=" + encodedCallback;
    }

    @Override
    public String buildLogoutUrl(String callbackUrl) {
        String encodedCallback = URLEncoder.encode(callbackUrl, StandardCharsets.UTF_8);
        return "/sso/rest-token/logout?service=" + encodedCallback;
    }

    @Override
    public SsoType getSsoType() {
        return SsoType.REST_TOKEN;
    }
}
