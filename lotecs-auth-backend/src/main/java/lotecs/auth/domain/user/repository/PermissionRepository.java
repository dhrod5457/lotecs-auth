package lotecs.auth.domain.user.repository;

import lotecs.auth.domain.user.model.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository {

    /**
     * 권한 ID로 조회
     */
    Optional<Permission> findById(Long permissionId);

    /**
     * 권한 코드로 조회
     */
    Optional<Permission> findByCode(String permissionCode);

    /**
     * 전체 권한 목록 조회
     */
    List<Permission> findAll();
}
