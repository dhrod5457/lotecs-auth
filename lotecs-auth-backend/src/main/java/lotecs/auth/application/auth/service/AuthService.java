package lotecs.auth.application.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.application.auth.dto.LoginRequest;
import lotecs.auth.application.auth.dto.LoginResponse;
import lotecs.auth.application.token.dto.TokenRefreshResult;
import lotecs.auth.application.token.dto.ValidateTokenResponse;
import lotecs.auth.application.token.service.TokenService;
import lotecs.auth.application.user.mapper.UserDtoMapper;
import lotecs.auth.application.user.service.UserSyncService;
import lotecs.auth.domain.sso.SsoAuthRequest;
import lotecs.auth.domain.sso.SsoAuthResult;
import lotecs.auth.domain.sso.SsoProvider;
import lotecs.auth.domain.sso.SsoType;
import lotecs.auth.domain.sso.model.TenantSsoConfig;
import lotecs.auth.domain.sso.repository.TenantSsoConfigRepository;
import lotecs.auth.domain.user.model.User;
import lotecs.auth.domain.user.repository.UserRepository;
import lotecs.auth.infrastructure.sso.SsoProviderFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final TenantSsoConfigRepository ssoConfigRepository;
    private final SsoProviderFactory ssoProviderFactory;
    private final UserSyncService userSyncService;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDtoMapper userDtoMapper;

    /**
     * 로그인 처리
     *
     * @param request 로그인 요청
     * @return 로그인 응답 (JWT 토큰 포함)
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("[AUTH] 로그인 시도: tenant={}, username={}, ip={}",
                request.getTenantId(), request.getUsername(), request.getIpAddress());

        // 1. SSO 설정 조회 (없으면 INTERNAL)
        TenantSsoConfig ssoConfig = ssoConfigRepository
                .findByTenantId(request.getTenantId())
                .orElseGet(() -> getDefaultInternalConfig(request.getTenantId()));

        log.info("[AUTH] SSO 설정: tenant={}, ssoType={}, ssoEnabled={}",
                request.getTenantId(), ssoConfig.getSsoType(), ssoConfig.getSsoEnabled());

        User user;

        // 2. SSO Type에 따라 인증 분기
        switch (ssoConfig.getSsoType()) {
            case INTERNAL:
                user = authenticateInternal(request);
                break;

            case RELAY:
            case KEYCLOAK:
            case LDAP:
            case EXTERNAL:
                user = authenticateExternal(request, ssoConfig);
                break;

            default:
                log.error("[AUTH] 지원하지 않는 SSO 타입: {}", ssoConfig.getSsoType());
                throw new UnsupportedOperationException("Unsupported SSO type: " + ssoConfig.getSsoType());
        }

        // 3. 로그인 정보 업데이트
        user.updateLoginInfo(request.getIpAddress());
        userRepository.save(user);

        // 4. JWT 발급 (TokenService 사용)
        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        log.info("[AUTH-001] 로그인 성공: userId={}, tenant={}, ssoType={}, ip={}",
                user.getUserId(), user.getTenantId(), ssoConfig.getSsoType(), request.getIpAddress());

        // 5. LoginResponse 반환
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(900L) // 15분 (초 단위)
                .user(userDtoMapper.toDto(user))
                .ssoType(ssoConfig.getSsoType())
                .build();
    }

    /**
     * INTERNAL 인증: DB 기반 인증
     *
     * @param request 로그인 요청
     * @return 인증된 사용자
     */
    private User authenticateInternal(LoginRequest request) {
        log.debug("[AUTH] INTERNAL 인증 시작: username={}, tenant={}",
                request.getUsername(), request.getTenantId());

        // 사용자 조회
        User user = userRepository
                .findByUsernameAndTenantId(request.getUsername(), request.getTenantId())
                .orElseThrow(() -> {
                    log.warn("[AUTH] 사용자를 찾을 수 없음: username={}, tenant={}",
                            request.getUsername(), request.getTenantId());
                    return new IllegalArgumentException("Invalid credentials");
                });

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("[AUTH] 비밀번호 불일치: username={}", request.getUsername());
            user.incrementFailedAttempts();
            userRepository.save(user);
            throw new IllegalArgumentException("Invalid credentials");
        }

        // 계정 상태 확인
        validateUserStatus(user);

        log.info("[AUTH] INTERNAL 인증 성공: userId={}, username={}", user.getUserId(), user.getUsername());
        return user;
    }

    /**
     * 외부 SSO 인증: RELAY/KEYCLOAK/LDAP/EXTERNAL
     *
     * @param request 로그인 요청
     * @param ssoConfig SSO 설정
     * @return 인증된 사용자
     */
    private User authenticateExternal(LoginRequest request, TenantSsoConfig ssoConfig) {
        log.debug("[AUTH] 외부 SSO 인증 시작: ssoType={}, tenant={}",
                ssoConfig.getSsoType(), request.getTenantId());

        // SsoProvider 가져오기
        SsoProvider ssoProvider = ssoProviderFactory.getProvider(ssoConfig.getSsoType());

        // SSO 인증 요청
        SsoAuthRequest ssoRequest = new SsoAuthRequest(
                request.getTenantId(),
                request.getUsername(),
                request.getPassword(),
                request.getIpAddress()
        );

        SsoAuthResult ssoResult = ssoProvider.authenticate(ssoRequest);

        // 인증 실패 처리
        if (!ssoResult.success()) {
            log.warn("[AUTH] 외부 SSO 인증 실패: ssoType={}, errorCode={}, errorMessage={}",
                    ssoConfig.getSsoType(), ssoResult.errorCode(), ssoResult.errorMessage());
            throw new IllegalArgumentException("SSO authentication failed: " + ssoResult.errorMessage());
        }

        log.info("[AUTH] 외부 SSO 인증 성공: ssoType={}, externalUserId={}, username={}",
                ssoConfig.getSsoType(), ssoResult.externalUserId(), ssoResult.username());

        // 사용자 동기화 (UserSyncService)
        User user = userSyncService.syncUserFromExternal(ssoResult, ssoConfig);

        // 계정 상태 확인
        validateUserStatus(user);

        log.info("[AUTH] 외부 SSO 인증 및 동기화 완료: userId={}, externalUserId={}",
                user.getUserId(), ssoResult.externalUserId());

        return user;
    }

    /**
     * 사용자 계정 상태 검증
     *
     * @param user 사용자
     */
    private void validateUserStatus(User user) {
        if (!user.getAccountNonLocked()) {
            log.warn("[AUTH] 계정 잠김: userId={}, username={}", user.getUserId(), user.getUsername());
            throw new IllegalArgumentException("Account is locked");
        }

        if (!user.getEnabled()) {
            log.warn("[AUTH] 계정 비활성화: userId={}, username={}", user.getUserId(), user.getUsername());
            throw new IllegalArgumentException("Account is disabled");
        }

        if ("DELETED".equals(user.getStatus()) || "SUSPENDED".equals(user.getStatus())) {
            log.warn("[AUTH] 계정 상태 이상: userId={}, status={}", user.getUserId(), user.getStatus());
            throw new IllegalArgumentException("Account is not active");
        }
    }

    /**
     * 기본 INTERNAL SSO 설정 반환
     *
     * @param tenantId 테넌트 ID
     * @return INTERNAL SSO 설정
     */
    private TenantSsoConfig getDefaultInternalConfig(String tenantId) {
        log.info("[AUTH] SSO 설정이 없음, INTERNAL로 기본 설정 사용: tenantId={}", tenantId);

        return TenantSsoConfig.builder()
                .tenantId(tenantId)
                .ssoType(SsoType.INTERNAL)
                .ssoEnabled(true)
                .userSyncEnabled(false)
                .roleMappingEnabled(false)
                .build();
    }

    /**
     * 로그아웃 처리
     *
     * @param userId 사용자 ID
     */
    @Transactional
    public void logout(Long userId) {
        log.info("[AUTH-007] 로그아웃: userId={}", userId);

        // Refresh Token 삭제 (TokenService에 위임하지 않고 직접 삭제 필요시)
        // refreshTokenRepository.deleteByUserId(userId);

        log.info("[AUTH-008] 로그아웃 완료: userId={}", userId);
    }

    /**
     * 토큰 갱신
     *
     * @param refreshToken Refresh Token
     * @return 로그인 응답
     */
    @Transactional
    public LoginResponse refresh(String refreshToken) {
        log.info("[AUTH-009] 토큰 갱신 시도");

        TokenRefreshResult result = tokenService.refreshToken(refreshToken);

        // User 조회
        User user = tokenService.validateAccessToken(result.getAccessToken());

        log.info("[AUTH-010] 토큰 갱신 성공: userId={}", user.getUserId());

        return LoginResponse.builder()
                .accessToken(result.getAccessToken())
                .refreshToken(result.getRefreshToken())
                .expiresIn(result.getExpiresIn())
                .user(userDtoMapper.toDto(user))
                .build();
    }

    /**
     * 토큰 검증
     *
     * @param accessToken Access Token
     * @return 검증 결과
     */
    @Transactional(readOnly = true)
    public ValidateTokenResponse validate(String accessToken) {
        log.debug("[AUTH-011] 토큰 검증 시도");

        try {
            User user = tokenService.validateAccessToken(accessToken);

            log.debug("[AUTH-012] 토큰 검증 성공: userId={}", user.getUserId());

            return ValidateTokenResponse.builder()
                    .valid(true)
                    .user(userDtoMapper.toDto(user))
                    .build();

        } catch (Exception e) {
            log.warn("[AUTH-013] 토큰 검증 실패: {}", e.getMessage());

            return ValidateTokenResponse.builder()
                    .valid(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }
}
