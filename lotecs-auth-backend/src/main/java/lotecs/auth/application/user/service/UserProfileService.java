package lotecs.auth.application.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.sso.SsoAuthResult;
import lotecs.auth.domain.sso.SsoType;
import lotecs.auth.domain.user.model.ProfileSource;
import lotecs.auth.domain.user.model.UserProfile;
import lotecs.auth.domain.user.repository.UserProfileRepository;
import lotecs.auth.infrastructure.sso.SsoResponseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * 사용자 프로필 서비스.
 * SSO 로그인 시 추가 데이터를 저장하고 관리한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final SsoResponseMapper ssoResponseMapper;

    /**
     * SSO 인증 결과로부터 사용자 프로필을 저장/업데이트한다.
     *
     * @param userId     사용자 ID
     * @param tenantId   테넌트 ID
     * @param ssoResult  SSO 인증 결과
     * @param ssoType    SSO 타입
     */
    @Transactional
    public void saveProfileFromSso(String userId, String tenantId, SsoAuthResult ssoResult, SsoType ssoType) {
        if (ssoResult == null || ssoResult.getAdditionalData() == null || ssoResult.getAdditionalData().isEmpty()) {
            log.debug("No additional data to save for user: {} in tenant: {}", userId, tenantId);
            return;
        }

        // _fallback으로 시작하는 메타 필드는 제외
        Map<String, Object> profileData = filterProfileData(ssoResult.getAdditionalData());

        if (profileData.isEmpty()) {
            log.debug("No profile data to save after filtering for user: {} in tenant: {}", userId, tenantId);
            return;
        }

        try {
            Optional<UserProfile> existingProfile = userProfileRepository.findByUserIdAndTenantId(userId, tenantId);

            if (existingProfile.isPresent()) {
                UserProfile profile = existingProfile.get();
                profile.updateFromSso(profileData, ssoType.name());
                userProfileRepository.save(profile);
                log.info("Updated user profile from SSO: userId={}, tenantId={}, ssoType={}", userId, tenantId, ssoType);
            } else {
                UserProfile newProfile = UserProfile.createFromSso(userId, tenantId, profileData, ssoType.name());
                userProfileRepository.save(newProfile);
                log.info("Created user profile from SSO: userId={}, tenantId={}, ssoType={}", userId, tenantId, ssoType);
            }
        } catch (Exception e) {
            // 프로필 저장 실패는 로그인을 실패시키지 않음
            log.error("Failed to save user profile: userId={}, tenantId={}, error={}", userId, tenantId, e.getMessage(), e);
        }
    }

    /**
     * 사용자 프로필 조회
     */
    @Transactional(readOnly = true)
    public Optional<UserProfile> getProfile(String userId, String tenantId) {
        return userProfileRepository.findByUserIdAndTenantId(userId, tenantId);
    }

    /**
     * 사용자 프로필 데이터(Map) 조회
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getProfileData(String userId, String tenantId) {
        return userProfileRepository.findByUserIdAndTenantId(userId, tenantId)
                .map(UserProfile::getProfileDataAsMap)
                .orElse(Map.of());
    }

    /**
     * 수동으로 프로필 업데이트
     */
    @Transactional
    public void updateProfile(String userId, String tenantId, Map<String, Object> profileData) {
        Optional<UserProfile> existingProfile = userProfileRepository.findByUserIdAndTenantId(userId, tenantId);

        UserProfile profile;
        if (existingProfile.isPresent()) {
            profile = existingProfile.get();
            profile.setProfileDataFromMap(profileData);
            profile.setSource(ProfileSource.MANUAL);
            profile.setUpdatedAt(LocalDateTime.now());
        } else {
            profile = UserProfile.builder()
                    .userId(userId)
                    .tenantId(tenantId)
                    .source(ProfileSource.MANUAL)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            profile.setProfileDataFromMap(profileData);
        }

        userProfileRepository.save(profile);
        log.info("Updated user profile manually: userId={}, tenantId={}", userId, tenantId);
    }

    /**
     * 프로필 삭제
     */
    @Transactional
    public void deleteProfile(String userId, String tenantId) {
        userProfileRepository.deleteByUserIdAndTenantId(userId, tenantId);
        log.info("Deleted user profile: userId={}, tenantId={}", userId, tenantId);
    }

    /**
     * 메타 필드(_로 시작하는 필드)를 제외한 프로필 데이터 필터링
     */
    private Map<String, Object> filterProfileData(Map<String, Object> additionalData) {
        return additionalData.entrySet().stream()
                .filter(entry -> !entry.getKey().startsWith("_"))
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
