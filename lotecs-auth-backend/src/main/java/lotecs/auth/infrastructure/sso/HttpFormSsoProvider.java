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

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

/**
 * HTTP Form 방식 SSO 제공자 (ewha 방식).
 * GET 요청으로 SSO 서버 API 호출.
 */
@Slf4j
@Component("http_form")
public class HttpFormSsoProvider implements SsoProvider {

    private final TenantSsoConfigRepository ssoConfigRepository;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpFormSsoProvider(TenantSsoConfigRepository ssoConfigRepository, ObjectMapper objectMapper) {
        this.ssoConfigRepository = ssoConfigRepository;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        log.debug("HttpFormSsoProvider initialized");
    }

    @Override
    public SsoAuthResult authenticate(SsoAuthRequest request) {
        log.debug("HTTP_FORM SSO authentication for user: {} in tenant: {}", request.getUsername(), request.getTenantId());

        try {
            TenantSsoConfig config = ssoConfigRepository.findByTenantId(request.getTenantId())
                    .orElseThrow(() -> new IllegalStateException("SSO configuration not found for tenant: " + request.getTenantId()));

            if (config.getSsoServerUrl() == null || config.getSsoServerUrl().isBlank()) {
                log.error("SSO server URL is not configured for tenant: {}", request.getTenantId());
                return SsoAuthResult.failure("CONFIG_ERROR", "SSO server URL is not configured");
            }

            return performAuthentication(request, config);

        } catch (IllegalStateException e) {
            log.error("Configuration error: {}", e.getMessage());
            return SsoAuthResult.failure("CONFIG_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("HTTP_FORM SSO authentication failed for user {} in tenant {}: {}",
                    request.getUsername(), request.getTenantId(), e.getMessage(), e);
            return SsoAuthResult.failure("AUTH_ERROR", "HTTP_FORM 인증 실패: " + e.getMessage());
        }
    }

    private SsoAuthResult performAuthentication(SsoAuthRequest request, TenantSsoConfig config) throws Exception {
        String url = buildConfirmUrl(request, config);
        log.debug("SSO confirm URL: {}", url);

        int timeout = config.getReadTimeoutMs() != null ? config.getReadTimeoutMs() : 5000;

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(timeout))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            log.warn("SSO server returned status: {}", response.statusCode());
            return SsoAuthResult.failure("HTTP_ERROR", "SSO 서버 응답 오류: " + response.statusCode());
        }

        return parseResponse(response.body(), request.getUsername());
    }

    private String buildConfirmUrl(SsoAuthRequest request, TenantSsoConfig config) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(config.getSsoServerUrl());

        String confirmEndpoint = config.getHttpFormConfirmEndpoint() != null
                ? config.getHttpFormConfirmEndpoint()
                : "/confirm";
        urlBuilder.append(confirmEndpoint);
        urlBuilder.append("?");

        String idParam = config.getHttpFormIdParam() != null ? config.getHttpFormIdParam() : "id";
        String passwordParam = config.getHttpFormPasswordParam() != null ? config.getHttpFormPasswordParam() : "password";

        urlBuilder.append(idParam);
        urlBuilder.append("=");
        urlBuilder.append(URLEncoder.encode(request.getUsername(), StandardCharsets.UTF_8));
        urlBuilder.append("&");
        urlBuilder.append(passwordParam);
        urlBuilder.append("=");

        String password = request.getPassword();
        if (config.isHttpFormEncodePassword()) {
            password = URLEncoder.encode(password, StandardCharsets.UTF_8);
        }
        urlBuilder.append(password);

        // 추가 파라미터
        if (request.getExtraParams() != null) {
            for (Map.Entry<String, String> entry : request.getExtraParams().entrySet()) {
                urlBuilder.append("&");
                urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
                urlBuilder.append("=");
                urlBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }
        }

        return urlBuilder.toString();
    }

    private SsoAuthResult parseResponse(String responseBody, String userId) {
        try {
            log.debug("SSO response: {}", responseBody);

            // JSON 응답인 경우
            if (responseBody.trim().startsWith("{")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);

                // 성공 여부 확인 (학교별로 응답 형식이 다를 수 있음)
                String resultCode = (String) result.get("result");
                if (resultCode == null) {
                    resultCode = (String) result.get("status");
                }
                if (resultCode == null) {
                    resultCode = result.containsKey("success") ? "Y" : "N";
                }

                if ("Y".equalsIgnoreCase(resultCode) || "success".equalsIgnoreCase(resultCode) || "true".equalsIgnoreCase(resultCode)) {
                    log.info("SSO login successful for user: {}", userId);
                    return SsoAuthResult.success(
                            userId,
                            (String) result.get("userName"),
                            (String) result.get("email"),
                            (String) result.get("userName"),
                            Collections.emptyList(),
                            Map.of(
                                    "userType", result.get("userType") != null ? result.get("userType").toString() : "",
                                    "department", result.get("department") != null ? result.get("department").toString() : "",
                                    "userInfo", responseBody
                            )
                    );
                } else {
                    String errorMessage = (String) result.get("message");
                    if (errorMessage == null) {
                        errorMessage = (String) result.get("error_msg");
                    }
                    log.warn("SSO login failed for user: {} - {}", userId, errorMessage);
                    return SsoAuthResult.failure("LOGIN_FAILED", errorMessage != null ? errorMessage : "SSO 로그인 실패");
                }
            }

            // 텍스트 응답인 경우 (Y/N)
            if ("Y".equalsIgnoreCase(responseBody.trim()) || "success".equalsIgnoreCase(responseBody.trim())) {
                return SsoAuthResult.success(
                        userId,
                        userId,
                        null,
                        null,
                        Collections.emptyList(),
                        Collections.emptyMap()
                );
            }

            return SsoAuthResult.failure("LOGIN_FAILED", "SSO 로그인 실패");

        } catch (Exception e) {
            log.error("Failed to parse SSO response", e);
            return SsoAuthResult.failure("PARSE_ERROR", "SSO 응답 파싱 오류: " + e.getMessage());
        }
    }

    @Override
    public String buildLoginUrl(String callbackUrl) {
        String encodedCallback = URLEncoder.encode(callbackUrl, StandardCharsets.UTF_8);
        return "/sso/http-form/login?service=" + encodedCallback;
    }

    @Override
    public String buildLogoutUrl(String callbackUrl) {
        String encodedCallback = URLEncoder.encode(callbackUrl, StandardCharsets.UTF_8);
        return "/sso/http-form/logout?service=" + encodedCallback;
    }

    @Override
    public SsoType getSsoType() {
        return SsoType.HTTP_FORM;
    }
}
