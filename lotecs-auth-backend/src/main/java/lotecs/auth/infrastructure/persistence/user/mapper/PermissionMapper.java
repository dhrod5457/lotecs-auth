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
    Optional<Permission> findById(@Param("permissionId") Long permissionId);

    /**
     * 권한 코드로 권한 조회
     */
    Optional<Permission> findByCode(@Param("permissionCode") String permissionCode);

    /**
     * 모든 권한 조회
     */
    List<Permission> findAll();

    /**
     * 역할 ID로 권한 목록 조회
     */
    List<Permission> findByRoleId(@Param("roleId") Long roleId);
}
