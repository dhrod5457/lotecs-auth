package lotecs.auth.infrastructure.sso;

import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.sso.SsoAuthRequest;
import lotecs.auth.domain.sso.SsoAuthResult;
import lotecs.auth.domain.sso.SsoProvider;
import lotecs.auth.domain.sso.SsoType;
import lotecs.auth.domain.sso.model.TenantSsoConfig;
import lotecs.auth.domain.sso.repository.TenantSsoConfigRepository;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import lotecs.auth.domain.sso.exception.SsoConnectionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

/**
 * CAS 프로토콜 기반 SSO 제공자.
 */
@Slf4j
@Component("cas")
public class CasSsoProvider implements SsoProvider {

    private final TenantSsoConfigRepository ssoConfigRepository;
    private final HttpClient httpClient;

    public CasSsoProvider(TenantSsoConfigRepository ssoConfigRepository) {
        this.ssoConfigRepository = ssoConfigRepository;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        log.debug("CasSsoProvider initialized");
    }

    @Override
    public SsoAuthResult authenticate(SsoAuthRequest request) {
        log.debug("CAS SSO authentication for user: {} in tenant: {}", request.getUsername(), request.getTenantId());

        try {
            TenantSsoConfig config = ssoConfigRepository.findByTenantId(request.getTenantId())
                    .orElseThrow(() -> new IllegalStateException("SSO configuration not found for tenant: " + request.getTenantId()));

            if (config.getSsoServerUrl() == null || config.getSsoServerUrl().isBlank()) {
                log.error("CAS server URL is not configured for tenant: {}", request.getTenantId());
                return SsoAuthResult.failure("CONFIG_ERROR", "CAS server URL is not configured");
            }

            // SSO 토큰(CAS ticket)이 없으면 에러
            if (request.getSsoToken() == null || request.getSsoToken().isBlank()) {
                log.warn("CAS ticket is required for authentication");
                return SsoAuthResult.failure("TOKEN_REQUIRED", "CAS ticket is required");
            }

            return validateCasTicket(request.getSsoToken(), config);

        } catch (IllegalStateException e) {
            log.error("Configuration error: {}", e.getMessage());
            return SsoAuthResult.failure("CONFIG_ERROR", e.getMessage());
        } catch (SsoConnectionException e) {
            throw e;
        } catch (HttpTimeoutException | HttpConnectTimeoutException e) {
            log.error("CAS SSO timeout for user {} in tenant {}: {}",
                    request.getUsername(), request.getTenantId(), e.getMessage());
            throw SsoConnectionException.timeout("CAS 서버 연결 타임아웃: " + e.getMessage(), e);
        } catch (ConnectException e) {
            log.error("CAS SSO connection refused for user {} in tenant {}: {}",
                    request.getUsername(), request.getTenantId(), e.getMessage());
            throw SsoConnectionException.networkError("CAS 서버 연결 실패: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("CAS SSO IO error for user {} in tenant {}: {}",
                    request.getUsername(), request.getTenantId(), e.getMessage(), e);
            throw SsoConnectionException.networkError("CAS 서버 통신 오류: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw SsoConnectionException.networkError("CAS 인증 중단됨", e);
        } catch (Exception e) {
            log.error("CAS SSO authentication failed for user {} in tenant {}: {}",
                    request.getUsername(), request.getTenantId(), e.getMessage(), e);
            return SsoAuthResult.failure("AUTH_ERROR", "CAS 인증 실패: " + e.getMessage());
        }
    }

    private SsoAuthResult validateCasTicket(String ticket, TenantSsoConfig config) {
        try {
            String validateUrl = buildValidateUrl(ticket, config);
            log.debug("Validating CAS ticket with URL: {}", validateUrl);

            int timeout = config.getReadTimeoutMs() != null ? config.getReadTimeoutMs() : 5000;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(validateUrl))
                    .timeout(Duration.ofMillis(timeout))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 500) {
                throw SsoConnectionException.serverError(
                        "CAS 서버 오류: HTTP " + response.statusCode(),
                        response.statusCode()
                );
            }

            if (response.statusCode() != 200) {
                log.warn("CAS validation failed with status: {}", response.statusCode());
                return SsoAuthResult.failure("TOKEN_INVALID", "CAS 토큰 검증 실패");
            }

            return parseValidationResponse(response.body());

        } catch (HttpTimeoutException | HttpConnectTimeoutException e) {
            throw SsoConnectionException.timeout("CAS 서버 연결 타임아웃: " + e.getMessage(), e);
        } catch (ConnectException e) {
            throw SsoConnectionException.networkError("CAS 서버 연결 실패: " + e.getMessage(), e);
        } catch (IOException e) {
            throw SsoConnectionException.networkError("CAS 서버 통신 오류: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw SsoConnectionException.networkError("CAS 검증 중단됨", e);
        } catch (SsoConnectionException e) {
            throw e;
        } catch (Exception e) {
            log.error("CAS validation error", e);
            return SsoAuthResult.failure("TOKEN_INVALID", "CAS 검증 오류: " + e.getMessage());
        }
    }

    private String buildValidateUrl(String ticket, TenantSsoConfig config) {
        String validateEndpoint = config.getCasValidateEndpoint() != null
                ? config.getCasValidateEndpoint()
                : "/serviceValidate";
        String serviceUrl = config.getCasServiceUrl() != null
                ? config.getCasServiceUrl()
                : config.getSsoServerUrl();

        String encodedService = URLEncoder.encode(serviceUrl, StandardCharsets.UTF_8);
        return config.getSsoServerUrl() + validateEndpoint + "?ticket=" + ticket + "&service=" + encodedService;
    }

    private SsoAuthResult parseValidationResponse(String responseBody) {
        log.debug("Parsing CAS response: {}", responseBody);

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            // XXE 방지
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(responseBody.getBytes(StandardCharsets.UTF_8)));

            // CAS 인증 성공 확인
            NodeList successNodes = doc.getElementsByTagNameNS("*", "authenticationSuccess");
            if (successNodes.getLength() == 0) {
                // 인증 실패 응답 확인
                NodeList failureNodes = doc.getElementsByTagNameNS("*", "authenticationFailure");
                if (failureNodes.getLength() > 0) {
                    Element failure = (Element) failureNodes.item(0);
                    String code = failure.getAttribute("code");
                    String message = failure.getTextContent().trim();
                    log.warn("CAS authentication failed: {} - {}", code, message);
                    return SsoAuthResult.failure(mapCasErrorCode(code), message);
                }
                return SsoAuthResult.failure("TOKEN_INVALID", "Invalid CAS response");
            }

            Element success = (Element) successNodes.item(0);

            // 사용자 ID 추출
            String userId = getElementText(success, "user");
            if (userId == null || userId.isBlank()) {
                return SsoAuthResult.failure("USER_NOT_FOUND", "User ID not found in CAS response");
            }

            // CAS attributes 추출
            String userName = getAttributeValue(success, "userName");
            String userType = getAttributeValue(success, "userType");
            String department = getAttributeValue(success, "department");
            String email = getAttributeValue(success, "email");

            log.debug("CAS validation successful for user: {}", userId);
            return SsoAuthResult.success(
                    userId,
                    userName != null ? userName : userId,
                    email,
                    userName,
                    Collections.emptyList(),
                    Map.of(
                            "userType", userType != null ? userType : "",
                            "department", department != null ? department : ""
                    )
            );

        } catch (Exception e) {
            log.error("Failed to parse CAS response", e);
            return SsoAuthResult.failure("TOKEN_INVALID", "CAS 응답 파싱 오류: " + e.getMessage());
        }
    }

    private String mapCasErrorCode(String casCode) {
        return switch (casCode) {
            case "INVALID_TICKET" -> "TOKEN_INVALID";
            case "INVALID_SERVICE" -> "UNAUTHORIZED";
            case "INVALID_REQUEST" -> "TOKEN_INVALID";
            default -> "TOKEN_INVALID";
        };
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagNameNS("*", tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent().trim();
        }
        return null;
    }

    private String getAttributeValue(Element success, String attrName) {
        NodeList attrNodes = success.getElementsByTagNameNS("*", "attributes");
        if (attrNodes.getLength() > 0) {
            Element attrs = (Element) attrNodes.item(0);
            NodeList valueNodes = attrs.getElementsByTagNameNS("*", attrName);
            if (valueNodes.getLength() > 0) {
                return valueNodes.item(0).getTextContent().trim();
            }
        }
        return null;
    }

    @Override
    public String buildLoginUrl(String callbackUrl) {
        String encodedCallback = URLEncoder.encode(callbackUrl, StandardCharsets.UTF_8);
        return "/cas/login?service=" + encodedCallback;
    }

    @Override
    public String buildLogoutUrl(String callbackUrl) {
        String encodedCallback = URLEncoder.encode(callbackUrl, StandardCharsets.UTF_8);
        return "/cas/logout?service=" + encodedCallback;
    }

    @Override
    public SsoType getSsoType() {
        return SsoType.CAS;
    }
}
