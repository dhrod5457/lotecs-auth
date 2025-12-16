package lotecs.auth.domain.user.repository;

import lotecs.auth.domain.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    /**
     * 사용자 ID로 조회
     */
    Optional<User> findById(String userId);

    /**
     * 사용자 ID와 테넌트 ID로 조회
     */
    Optional<User> findByIdAndTenantId(String userId, String tenantId);

    /**
     * 테넌트 ID와 사용자명으로 조회
     */
    Optional<User> findByUsernameAndTenantId(String username, String tenantId);

    /**
     * 테넌트 ID로 사용자 목록 조회 (페이징)
     */
    List<User> findByTenantId(String tenantId, int page, int size);

    /**
     * 사용자 저장
     */
    User save(User user);

    /**
     * 사용자 삭제
     */
    void delete(String userId);
}
