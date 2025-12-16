package lotecs.auth.domain.user.repository;

import lotecs.auth.domain.user.model.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository {

    /**
     * 권한 ID로 조회
     */
    Optional<Permission> findById(String permissionId);

    /**
     * 권한명으로 조회
     */
    Optional<Permission> findByPermissionName(String permissionName, String tenantId);

    /**
     * 리소스와 액션으로 조회
     */
    Optional<Permission> findByResourceAndAction(String resource, String action, String tenantId);

    /**
     * 테넌트 ID로 전체 권한 목록 조회
     */
    List<Permission> findByTenantId(String tenantId);

    /**
     * 역할 ID로 권한 목록 조회
     */
    List<Permission> findByRoleId(String roleId);

    /**
     * 권한 저장
     */
    Permission save(Permission permission);

    /**
     * 권한 삭제
     */
    void delete(String permissionId);
}
