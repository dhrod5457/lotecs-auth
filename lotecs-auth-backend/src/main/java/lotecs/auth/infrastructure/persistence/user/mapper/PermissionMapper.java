package lotecs.auth.infrastructure.persistence.user.mapper;

import lotecs.auth.domain.user.model.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PermissionMapper {

    /**
     * ID로 권한 조회
     */
    Optional<Permission> findById(@Param("permissionId") String permissionId);

    /**
     * 권한명으로 권한 조회
     */
    Optional<Permission> findByPermissionName(@Param("permissionName") String permissionName, @Param("tenantId") String tenantId);

    /**
     * 리소스와 액션으로 권한 조회
     */
    Optional<Permission> findByResourceAndAction(@Param("resource") String resource, @Param("action") String action, @Param("tenantId") String tenantId);

    /**
     * 테넌트 ID로 모든 권한 조회
     */
    List<Permission> findByTenantId(@Param("tenantId") String tenantId);

    /**
     * 역할 ID로 권한 목록 조회
     */
    List<Permission> findByRoleId(@Param("roleId") String roleId);

    /**
     * 권한 등록
     */
    void insert(Permission permission);

    /**
     * 권한 정보 수정
     */
    void update(Permission permission);

    /**
     * 권한 삭제 (Soft Delete)
     */
    void delete(@Param("permissionId") String permissionId);
}
