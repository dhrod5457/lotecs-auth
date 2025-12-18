package lotecs.auth.infrastructure.sso;

import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.sso.SsoAuthRequest;
import lotecs.auth.domain.sso.SsoAuthResult;
import lotecs.auth.domain.sso.SsoProvider;
import lotecs.auth.domain.sso.SsoType;
import lotecs.auth.domain.sso.exception.SsoConnectionException;
import lotecs.auth.domain.sso.model.TenantSsoConfig;
import lotecs.auth.domain.user.model.User;
import lotecs.auth.domain.user.model.UserProfile;
import lotecs.auth.domain.user.repository.UserProfileRepository;
import lotecs.auth.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Map;

/**
 * SSO Provider 데코레이터 - Fallback 기능을 제공한다.
 * SSO 서버 연결 실패 시 Internal DB로 폴백하여 인증을 시도한다.
 *
 * <p>폴백 조건:</p>
 * <ul>
 *   <li>연결 타임아웃</li>
 *   <li>HTTP 5xx 서버 오류</li>
 *   <li>네트워크 오류</li>
 * </ul>
 *
 * <p>폴백 시 동작:</p>
 * <ul>
 *   <li>기존에 동기화된 사용자만 로그인 허용</li>
 *   <li>DB에 없는 사용자는 로그인 실패</li>
 *   <li>저장된 프로필 데이터를 additionalData로 제공</li>
 * </ul>
 */
@Slf4j
public class FallbackAwareSsoProvider implements SsoProvider {

    private final SsoProvider delegate;
    private final TenantSsoConfig ssoConfig;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public FallbackAwareSsoProvider(
            SsoProvider delegate,
            TenantSsoConfig ssoConfig,
            UserRepository userRepository,
            UserProfileRepository userProfileRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.delegate = delegate;
        this.ssoConfig = ssoConfig;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SsoAuthResult authenticate(SsoAuthRequest request) {
        try {
            // 1. 원래 SSO Provider로 인증 시도
            return delegate.authenticate(request);

        } catch (SsoConnectionException e) {
            // 2. Fallback 가능한 예외인지 확인
            if (!e.isFallbackable()) {
                log.warn("[FALLBACK] SSO 연결 실패 (fallback 불가): errorType={}, message={}",
                        e.getErrorType(), e.getMessage());
                throw e;
            }

            // 3. Fallback 활성화 여부 확인
            if (!ssoConfig.isFallbackEnabled()) {
                log.warn("[FALLBACK] SSO 연결 실패 (fallback 비활성화): tenant={}, errorType={}",
                        request.getTenantId(), e.getErrorType());
                throw e;
            }

            // 4. Internal DB로 폴백
            log.info("[FALLBACK] SSO 연결 실패, Internal DB로 폴백 시도: tenant={}, username={}, errorType={}",
                    request.getTenantId(), request.getUsername(), e.getErrorType());

            return fallbackToInternal(request, e);
        }
    }

    /**
     * Internal DB로 폴백하여 인증
     */
    private SsoAuthResult fallbackToInternal(SsoAuthRequest request, SsoConnectionException originalException) {
        try {
            // 1. 사용자 조회 (기존에 동기화된 사용자만)
            User user = userRepository
                    .findByUsernameAndTenantId(request.getUsername(), request.getTenantId())
                    .orElse(null);

            if (user == null) {
                log.warn("[FALLBACK] 사용자를 찾을 수 없음 (동기화되지 않은 사용자): tenant={}, username={}",
                        request.getTenantId(), request.getUsername());
                return SsoAuthResult.failure(
                        "FALLBACK_USER_NOT_FOUND",
                        "SSO 서버에 연결할 수 없으며, 기존에 등록된 사용자가 아닙니다."
                );
            }

            // 2. 계정 상태 확인
            if (!user.isEnabled() || !user.isAccountNonLocked()) {
                log.warn("[FALLBACK] 계정 비활성화 또는 잠김: tenant={}, username={}, enabled={}, locked={}",
                        request.getTenantId(), request.getUsername(), user.isEnabled(), !user.isAccountNonLocked());
                return SsoAuthResult.failure("FALLBACK_ACCOUNT_DISABLED", "계정이 비활성화되었거나 잠겨있습니다.");
            }

            // 3. 비밀번호 검증 (설정에 따라)
            if (ssoConfig.isFallbackPasswordRequired()) {
                if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                    log.warn("[FALLBACK] 비밀번호 불일치: tenant={}, username={}",
                            request.getTenantId(), request.getUsername());
                    return SsoAuthResult.failure("FALLBACK_INVALID_PASSWORD", "비밀번호가 일치하지 않습니다.");
                }
            }

            // 4. 저장된 프로필 데이터 조회
            Map<String, Object> additionalData = loadStoredProfileData(user.getUserId(), request.getTenantId());

            // 5. 폴백 정보를 additionalData에 추가
            additionalData.put("_fallback", true);
            additionalData.put("_fallbackReason", originalException.getErrorType().name());
            if (originalException.getHttpStatusCode() != null) {
                additionalData.put("_fallbackHttpStatus", originalException.getHttpStatusCode());
            }

            log.info("[FALLBACK] Internal DB 인증 성공: tenant={}, username={}, userId={}",
                    request.getTenantId(), request.getUsername(), user.getUserId());

            return SsoAuthResult.success(
                    user.getUserId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getRoles().stream()
                            .map(role -> role.getRoleName())
                            .toList(),
                    additionalData
            );

        } catch (Exception e) {
            log.error("[FALLBACK] Internal DB 인증 중 오류 발생: tenant={}, username={}, error={}",
                    request.getTenantId(), request.getUsername(), e.getMessage(), e);
            return SsoAuthResult.failure("FALLBACK_ERROR", "폴백 인증 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 저장된 프로필 데이터 로드
     */
    private Map<String, Object> loadStoredProfileData(String userId, String tenantId) {
        try {
            UserProfile profile = userProfileRepository
                    .findByUserIdAndTenantId(userId, tenantId)
                    .orElse(null);

            if (profile != null) {
                Map<String, Object> data = profile.getProfileDataAsMap();
                // 프로필 메타 정보 추가
                if (profile.getSyncedAt() != null) {
                    data.put("_profileSyncedAt", profile.getSyncedAt().toString());
                }
                data.put("_profileSource", profile.getSource().name());
                return data;
            }
        } catch (Exception e) {
            log.warn("[FALLBACK] 프로필 데이터 로드 실패: userId={}, tenantId={}, error={}",
                    userId, tenantId, e.getMessage());
        }
        return new java.util.HashMap<>();
    }

    @Override
    public String buildLoginUrl(String callbackUrl) {
        return delegate.buildLoginUrl(callbackUrl);
    }

    @Override
    public String buildLogoutUrl(String callbackUrl) {
        return delegate.buildLogoutUrl(callbackUrl);
    }

    @Override
    public SsoType getSsoType() {
        return delegate.getSsoType();
    }
}
