package lotecs.auth.infrastructure.persistence.user.mapper;

import lotecs.auth.domain.user.model.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RoleMapper {

    /**
     * ID로 역할 조회
     */
    Optional<Role> findById(@Param("roleId") String roleId);

    /**
     * ID와 테넌트 ID로 역할 조회
     */
    Optional<Role> findByIdAndTenantId(@Param("roleId") String roleId, @Param("tenantId") String tenantId);

    /**
     * 역할명으로 역할 조회
     */
    Optional<Role> findByRoleName(@Param("roleName") String roleName, @Param("tenantId") String tenantId);

    /**
     * 테넌트 ID로 역할 목록 조회
     */
    List<Role> findByTenantId(@Param("tenantId") String tenantId);

    /**
     * 사용자 ID로 역할 목록 조회
     */
    List<Role> findByUserId(@Param("userId") String userId);

    /**
     * 역할 등록
     */
    void insert(Role role);

    /**
     * 역할 정보 수정
     */
    void update(Role role);

    /**
     * 역할 삭제 (Soft Delete)
     */
    void delete(@Param("roleId") String roleId);
}
