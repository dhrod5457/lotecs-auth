package lotecs.auth.infrastructure.sso;

import lotecs.auth.domain.sso.SsoAuthRequest;
import lotecs.auth.domain.sso.SsoAuthResult;
import lotecs.auth.domain.sso.SsoProvider;
import lotecs.auth.domain.sso.SsoType;
import lotecs.auth.domain.sso.exception.SsoConnectionErrorType;
import lotecs.auth.domain.sso.exception.SsoConnectionException;
import lotecs.auth.domain.sso.model.TenantSsoConfig;
import lotecs.auth.domain.user.model.User;
import lotecs.auth.domain.user.model.UserStatus;
import lotecs.auth.domain.user.model.ProfileSource;
import lotecs.auth.domain.user.model.UserProfile;
import lotecs.auth.domain.user.repository.UserProfileRepository;
import lotecs.auth.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * FallbackAwareSsoProvider 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FallbackAwareSsoProvider 단위 테스트")
class FallbackAwareSsoProviderTest {

    @Mock
    private SsoProvider delegateProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private TenantSsoConfig ssoConfig;
    private FallbackAwareSsoProvider fallbackProvider;

    @BeforeEach
    void setUp() {
        ssoConfig = TenantSsoConfig.builder()
                .tenantId("test-tenant")
                .ssoType(SsoType.REST_TOKEN)
                .ssoEnabled(true)
                .fallbackEnabled(true)
                .fallbackPasswordRequired(true)
                .build();

        fallbackProvider = new FallbackAwareSsoProvider(
                delegateProvider,
                ssoConfig,
                userRepository,
                userProfileRepository,
                passwordEncoder
        );
    }

    @Nested
    @DisplayName("정상 SSO 인증")
    class NormalAuthentication {

        @Test
        @DisplayName("SSO 인증 성공 시 결과를 그대로 반환한다")
        void shouldReturnSsoResultOnSuccess() {
            // given
            SsoAuthRequest request = createAuthRequest();
            SsoAuthResult expectedResult = SsoAuthResult.success(
                    "user123", "홍길동", "test@example.com", "홍길동",
                    Collections.emptyList(), Map.of("userType", "학생")
            );
            when(delegateProvider.authenticate(request)).thenReturn(expectedResult);

            // when
            SsoAuthResult result = fallbackProvider.authenticate(request);

            // then
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getExternalUserId()).isEqualTo("user123");
            assertThat(result.getUsername()).isEqualTo("홍길동");
            verify(userRepository, never()).findByUsernameAndTenantId(anyString(), anyString());
        }

        @Test
        @DisplayName("SSO 인증 실패 시 결과를 그대로 반환한다")
        void shouldReturnFailureResultOnSsoFailure() {
            // given
            SsoAuthRequest request = createAuthRequest();
            SsoAuthResult expectedResult = SsoAuthResult.failure("LOGIN_FAILED", "비밀번호가 일치하지 않습니다");
            when(delegateProvider.authenticate(request)).thenReturn(expectedResult);

            // when
            SsoAuthResult result = fallbackProvider.authenticate(request);

            // then
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getErrorCode()).isEqualTo("LOGIN_FAILED");
            verify(userRepository, never()).findByUsernameAndTenantId(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("Fallback 인증")
    class FallbackAuthentication {

        @Test
        @DisplayName("타임아웃 발생 시 Fallback으로 기존 사용자 인증에 성공한다")
        void shouldFallbackOnTimeoutWithExistingUser() {
            // given
            SsoAuthRequest request = createAuthRequest();
            User existingUser = createExistingUser();

            when(delegateProvider.authenticate(request))
                    .thenThrow(SsoConnectionException.timeout("Connection timeout", new RuntimeException()));
            when(userRepository.findByUsernameAndTenantId("testuser", "test-tenant"))
                    .thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches("password123", existingUser.getPassword()))
                    .thenReturn(true);

            // when
            SsoAuthResult result = fallbackProvider.authenticate(request);

            // then
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getUsername()).isEqualTo("testuser");
            assertThat(result.getAdditionalData()).containsKey("_fallback");
            assertThat(result.getAdditionalData().get("_fallback")).isEqualTo(true);
            assertThat(result.getAdditionalData().get("_fallbackReason")).isEqualTo("TIMEOUT");
        }

        @Test
        @DisplayName("네트워크 오류 발생 시 Fallback으로 기존 사용자 인증에 성공한다")
        void shouldFallbackOnNetworkErrorWithExistingUser() {
            // given
            SsoAuthRequest request = createAuthRequest();
            User existingUser = createExistingUser();

            when(delegateProvider.authenticate(request))
                    .thenThrow(SsoConnectionException.networkError("Network error", new RuntimeException()));
            when(userRepository.findByUsernameAndTenantId("testuser", "test-tenant"))
                    .thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches("password123", existingUser.getPassword()))
                    .thenReturn(true);

            // when
            SsoAuthResult result = fallbackProvider.authenticate(request);

            // then
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getAdditionalData().get("_fallbackReason")).isEqualTo("NETWORK_ERROR");
        }

        @Test
        @DisplayName("서버 오류(5xx) 발생 시 Fallback으로 기존 사용자 인증에 성공한다")
        void shouldFallbackOnServerErrorWithExistingUser() {
            // given
            SsoAuthRequest request = createAuthRequest();
            User existingUser = createExistingUser();

            when(delegateProvider.authenticate(request))
                    .thenThrow(SsoConnectionException.serverError("Server error", 500));
            when(userRepository.findByUsernameAndTenantId("testuser", "test-tenant"))
                    .thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches("password123", existingUser.getPassword()))
                    .thenReturn(true);

            // when
            SsoAuthResult result = fallbackProvider.authenticate(request);

            // then
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getAdditionalData().get("_fallbackReason")).isEqualTo("SERVER_ERROR");
        }

        @Test
        @DisplayName("Fallback 시 사용자가 없으면 인증 실패 결과를 반환한다")
        void shouldReturnFailureWhenUserNotFoundOnFallback() {
            // given
            SsoAuthRequest request = createAuthRequest();

            when(delegateProvider.authenticate(request))
                    .thenThrow(SsoConnectionException.timeout("Connection timeout", new RuntimeException()));
            when(userRepository.findByUsernameAndTenantId("testuser", "test-tenant"))
                    .thenReturn(Optional.empty());

            // when
            SsoAuthResult result = fallbackProvider.authenticate(request);

            // then
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getErrorCode()).isEqualTo("FALLBACK_USER_NOT_FOUND");
        }

        @Test
        @DisplayName("Fallback 시 비밀번호가 일치하지 않으면 인증 실패 결과를 반환한다")
        void shouldReturnFailureWhenPasswordMismatchOnFallback() {
            // given
            SsoAuthRequest request = createAuthRequest();
            User existingUser = createExistingUser();

            when(delegateProvider.authenticate(request))
                    .thenThrow(SsoConnectionException.timeout("Connection timeout", new RuntimeException()));
            when(userRepository.findByUsernameAndTenantId("testuser", "test-tenant"))
                    .thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches("password123", existingUser.getPassword()))
                    .thenReturn(false);

            // when
            SsoAuthResult result = fallbackProvider.authenticate(request);

            // then
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getErrorCode()).isEqualTo("FALLBACK_INVALID_PASSWORD");
        }

        @Test
        @DisplayName("Fallback 시 비활성화된 계정은 인증 실패 결과를 반환한다")
        void shouldReturnFailureWhenAccountDisabledOnFallback() {
            // given
            SsoAuthRequest request = createAuthRequest();
            User disabledUser = User.builder()
                    .userId("user-123")
                    .tenantId("test-tenant")
                    .username("testuser")
                    .password("encodedPassword")
                    .status(UserStatus.SUSPENDED)
                    .enabled(false)
                    .accountNonLocked(true)
                    .build();

            when(delegateProvider.authenticate(request))
                    .thenThrow(SsoConnectionException.timeout("Connection timeout", new RuntimeException()));
            when(userRepository.findByUsernameAndTenantId("testuser", "test-tenant"))
                    .thenReturn(Optional.of(disabledUser));
            // 비밀번호 검증은 계정 상태 확인 이후에 수행되므로 mock 불필요

            // when
            SsoAuthResult result = fallbackProvider.authenticate(request);

            // then
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getErrorCode()).isEqualTo("FALLBACK_ACCOUNT_DISABLED");
        }
    }

    @Nested
    @DisplayName("Fallback 비활성화")
    class FallbackDisabled {

        @Test
        @DisplayName("Fallback 비활성화 시 연결 오류가 그대로 전파된다")
        void shouldPropagateExceptionWhenFallbackDisabled() {
            // given
            ssoConfig = TenantSsoConfig.builder()
                    .tenantId("test-tenant")
                    .ssoType(SsoType.REST_TOKEN)
                    .ssoEnabled(true)
                    .fallbackEnabled(false)
                    .build();

            fallbackProvider = new FallbackAwareSsoProvider(
                    delegateProvider,
                    ssoConfig,
                    userRepository,
                    userProfileRepository,
                    passwordEncoder
            );

            SsoAuthRequest request = createAuthRequest();

            when(delegateProvider.authenticate(request))
                    .thenThrow(SsoConnectionException.timeout("Connection timeout", new RuntimeException()));

            // when & then
            assertThatThrownBy(() -> fallbackProvider.authenticate(request))
                    .isInstanceOf(SsoConnectionException.class);

            verify(userRepository, never()).findByUsernameAndTenantId(anyString(), anyString());
        }

        @Test
        @DisplayName("Fallback 불가능한 오류(설정 오류)는 그대로 전파된다")
        void shouldPropagateNonFallbackableException() {
            // given
            SsoAuthRequest request = createAuthRequest();
            SsoConnectionException configError = new SsoConnectionException(
                    "SSO configuration error",
                    SsoConnectionErrorType.CONFIG_ERROR
            );

            when(delegateProvider.authenticate(request)).thenThrow(configError);

            // when & then
            assertThatThrownBy(() -> fallbackProvider.authenticate(request))
                    .isInstanceOf(SsoConnectionException.class)
                    .satisfies(ex -> {
                        SsoConnectionException sce = (SsoConnectionException) ex;
                        assertThat(sce.isFallbackable()).isFalse();
                    });
        }
    }

    @Nested
    @DisplayName("비밀번호 검증 없는 Fallback")
    class FallbackWithoutPasswordValidation {

        @Test
        @DisplayName("비밀번호 검증 비활성화 시 비밀번호 없이 Fallback 인증에 성공한다")
        void shouldFallbackWithoutPasswordValidation() {
            // given
            ssoConfig = TenantSsoConfig.builder()
                    .tenantId("test-tenant")
                    .ssoType(SsoType.CAS)
                    .ssoEnabled(true)
                    .fallbackEnabled(true)
                    .fallbackPasswordRequired(false)
                    .build();

            fallbackProvider = new FallbackAwareSsoProvider(
                    delegateProvider,
                    ssoConfig,
                    userRepository,
                    userProfileRepository,
                    passwordEncoder
            );

            SsoAuthRequest request = createAuthRequest();
            User existingUser = createExistingUser();

            when(delegateProvider.authenticate(request))
                    .thenThrow(SsoConnectionException.timeout("Connection timeout", new RuntimeException()));
            when(userRepository.findByUsernameAndTenantId("testuser", "test-tenant"))
                    .thenReturn(Optional.of(existingUser));

            // when
            SsoAuthResult result = fallbackProvider.authenticate(request);

            // then
            assertThat(result.isSuccess()).isTrue();
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("프로필 데이터 조회")
    class ProfileDataRetrieval {

        @Test
        @DisplayName("Fallback 인증 시 저장된 프로필 데이터를 additionalData에 포함한다")
        void shouldIncludeProfileDataOnFallback() {
            // given
            SsoAuthRequest request = createAuthRequest();
            User existingUser = createExistingUser();

            UserProfile profile = UserProfile.builder()
                    .userId("user-123")
                    .tenantId("test-tenant")
                    .source(ProfileSource.SSO)
                    .sourceSystem("REST_TOKEN")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            profile.setProfileDataFromMap(Map.of(
                    "studentId", "2023001234",
                    "department", "컴퓨터공학과",
                    "grade", "3"
            ));

            when(delegateProvider.authenticate(request))
                    .thenThrow(SsoConnectionException.timeout("Connection timeout", new RuntimeException()));
            when(userRepository.findByUsernameAndTenantId("testuser", "test-tenant"))
                    .thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches("password123", existingUser.getPassword()))
                    .thenReturn(true);
            when(userProfileRepository.findByUserIdAndTenantId("user-123", "test-tenant"))
                    .thenReturn(Optional.of(profile));

            // when
            SsoAuthResult result = fallbackProvider.authenticate(request);

            // then
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getAdditionalData()).containsKey("studentId");
            assertThat(result.getAdditionalData().get("studentId")).isEqualTo("2023001234");
            assertThat(result.getAdditionalData().get("department")).isEqualTo("컴퓨터공학과");
        }
    }

    @Nested
    @DisplayName("SsoProvider 인터페이스 위임")
    class ProviderDelegation {

        @Test
        @DisplayName("buildLoginUrl을 delegate에 위임한다")
        void shouldDelegateBuildLoginUrl() {
            // given
            String callbackUrl = "http://localhost/callback";
            when(delegateProvider.buildLoginUrl(callbackUrl)).thenReturn("/sso/login?service=" + callbackUrl);

            // when
            String result = fallbackProvider.buildLoginUrl(callbackUrl);

            // then
            assertThat(result).contains("/sso/login");
            verify(delegateProvider).buildLoginUrl(callbackUrl);
        }

        @Test
        @DisplayName("buildLogoutUrl을 delegate에 위임한다")
        void shouldDelegateBuildLogoutUrl() {
            // given
            String callbackUrl = "http://localhost/callback";
            when(delegateProvider.buildLogoutUrl(callbackUrl)).thenReturn("/sso/logout?service=" + callbackUrl);

            // when
            String result = fallbackProvider.buildLogoutUrl(callbackUrl);

            // then
            assertThat(result).contains("/sso/logout");
            verify(delegateProvider).buildLogoutUrl(callbackUrl);
        }

        @Test
        @DisplayName("getSsoType을 delegate에 위임한다")
        void shouldDelegateGetSsoType() {
            // given
            when(delegateProvider.getSsoType()).thenReturn(SsoType.REST_TOKEN);

            // when
            SsoType result = fallbackProvider.getSsoType();

            // then
            assertThat(result).isEqualTo(SsoType.REST_TOKEN);
            verify(delegateProvider).getSsoType();
        }
    }

    private SsoAuthRequest createAuthRequest() {
        SsoAuthRequest request = new SsoAuthRequest();
        request.setTenantId("test-tenant");
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setIpAddress("127.0.0.1");
        return request;
    }

    private User createExistingUser() {
        return User.builder()
                .userId("user-123")
                .tenantId("test-tenant")
                .username("testuser")
                .password("encodedPassword")
                .fullName("테스트 사용자")
                .email("testuser@example.com")
                .status(UserStatus.ACTIVE)
                .enabled(true)
                .accountNonLocked(true)
                .build();
    }
}
