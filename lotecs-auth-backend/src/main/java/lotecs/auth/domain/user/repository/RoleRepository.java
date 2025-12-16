package lotecs.auth.domain.user.repository;

import lotecs.auth.domain.user.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {

    /**
     * 역할 ID로 조회
     */
    Optional<Role> findById(String roleId);

    /**
     * 테넌트 ID와 역할명으로 조회
     */
    Optional<Role> findByRoleName(String tenantId, String roleName);

    /**
     * 역할명과 테넌트 ID로 조회 (별칭)
     */
    default Optional<Role> findByRoleNameAndTenantId(String roleName, String tenantId) {
        return findByRoleName(tenantId, roleName);
    }

    /**
     * 테넌트 ID로 역할 목록 조회
     */
    List<Role> findByTenantId(String tenantId);

    /**
     * 사용자 ID로 역할 목록 조회
     */
    List<Role> findByUserId(String userId);

    /**
     * 역할 저장
     */
    Role save(Role role);

    /**
     * 역할 삭제
     */
    void delete(String roleId);
}
