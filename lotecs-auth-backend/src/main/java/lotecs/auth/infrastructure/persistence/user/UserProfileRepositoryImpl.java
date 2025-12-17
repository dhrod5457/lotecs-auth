package lotecs.auth.infrastructure.persistence.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.user.model.UserProfile;
import lotecs.auth.domain.user.repository.UserProfileRepository;
import lotecs.auth.infrastructure.persistence.user.mapper.UserProfileMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 사용자 프로필 Repository 구현체
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserProfileRepositoryImpl implements UserProfileRepository {

    private final UserProfileMapper userProfileMapper;

    @Override
    public Optional<UserProfile> findByUserIdAndTenantId(String userId, String tenantId) {
        return userProfileMapper.findByUserIdAndTenantId(userId, tenantId);
    }

    @Override
    public void save(UserProfile profile) {
        if (profile.getCreatedAt() == null) {
            profile.setCreatedAt(LocalDateTime.now());
        }
        profile.setUpdatedAt(LocalDateTime.now());

        if (existsByUserIdAndTenantId(profile.getUserId(), profile.getTenantId())) {
            log.debug("Updating user profile: userId={}, tenantId={}", profile.getUserId(), profile.getTenantId());
            userProfileMapper.update(profile);
        } else {
            log.debug("Inserting user profile: userId={}, tenantId={}", profile.getUserId(), profile.getTenantId());
            userProfileMapper.insert(profile);
        }
    }

    @Override
    public void deleteByUserIdAndTenantId(String userId, String tenantId) {
        userProfileMapper.deleteByUserIdAndTenantId(userId, tenantId);
    }

    @Override
    public boolean existsByUserIdAndTenantId(String userId, String tenantId) {
        return userProfileMapper.existsByUserIdAndTenantId(userId, tenantId);
    }
}
