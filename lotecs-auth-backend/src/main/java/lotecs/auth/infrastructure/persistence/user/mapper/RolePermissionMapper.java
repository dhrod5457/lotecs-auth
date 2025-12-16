package lotecs.auth.infrastructure.persistence.user.mapper;

import lotecs.auth.domain.user.model.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RolePermissionMapper {

    /**
     * 역할-권한 매핑 조회
     */
    Optional<RolePermission> findByRoleIdAndPermissionId(@Param("roleId") String roleId, @Param("permissionId") String permissionId);

    /**
     * 역할 ID로 권한 매핑 목록 조회
     */
    List<RolePermission> findByRoleId(@Param("roleId") String roleId);

    /**
     * 권한 ID로 역할 매핑 목록 조회
     */
    List<RolePermission> findByPermissionId(@Param("permissionId") String permissionId);

    /**
     * 테넌트 ID로 매핑 목록 조회
     */
    List<RolePermission> findByTenantId(@Param("tenantId") String tenantId);

    /**
     * 권한 부여
     */
    void insert(RolePermission rolePermission);

    /**
     * 권한 부여 (Batch)
     */
    void insertBatch(@Param("rolePermissions") List<RolePermission> rolePermissions);

    /**
     * 권한 회수
     */
    void delete(@Param("roleId") String roleId, @Param("permissionId") String permissionId);

    /**
     * 역할의 모든 권한 회수
     */
    void deleteAllByRoleId(@Param("roleId") String roleId);

    /**
     * 권한에 연결된 모든 역할 매핑 삭제
     */
    void deleteAllByPermissionId(@Param("permissionId") String permissionId);
}
