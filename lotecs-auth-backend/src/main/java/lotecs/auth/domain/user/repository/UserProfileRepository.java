package lotecs.auth.domain.user.repository;

import lotecs.auth.domain.user.model.UserProfile;

import java.util.Optional;

/**
 * 사용자 프로필 Repository 인터페이스
 */
public interface UserProfileRepository {

    /**
     * 사용자 ID와 테넌트 ID로 프로필 조회
     */
    Optional<UserProfile> findByUserIdAndTenantId(String userId, String tenantId);

    /**
     * 프로필 저장 (신규 또는 업데이트)
     */
    void save(UserProfile profile);

    /**
     * 프로필 삭제
     */
    void deleteByUserIdAndTenantId(String userId, String tenantId);

    /**
     * 프로필 존재 여부 확인
     */
    boolean existsByUserIdAndTenantId(String userId, String tenantId);
}
