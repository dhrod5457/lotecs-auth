package lotecs.auth.infrastructure.persistence.user.mapper;

import lotecs.auth.domain.user.model.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserRoleMapper {

    /**
     * 사용자-역할 매핑 조회
     */
    Optional<UserRole> findByUserIdAndRoleId(@Param("userId") String userId, @Param("roleId") String roleId);

    /**
     * 사용자 ID로 역할 매핑 목록 조회
     */
    List<UserRole> findByUserId(@Param("userId") String userId);

    /**
     * 사용자 ID로 활성 역할 매핑 목록 조회
     */
    List<UserRole> findActiveByUserId(@Param("userId") String userId);

    /**
     * 역할 ID로 사용자 매핑 목록 조회
     */
    List<UserRole> findByRoleId(@Param("roleId") String roleId);

    /**
     * 테넌트 ID로 매핑 목록 조회
     */
    List<UserRole> findByTenantId(@Param("tenantId") String tenantId);

    /**
     * 역할 할당
     */
    void insert(UserRole userRole);

    /**
     * 역할 상태 업데이트
     */
    void updateStatus(UserRole userRole);

    /**
     * 역할 회수
     */
    void revoke(@Param("userId") String userId, @Param("roleId") String roleId, @Param("revokedBy") String revokedBy);

    /**
     * 사용자의 모든 역할 회수
     */
    void revokeAllByUserId(@Param("userId") String userId, @Param("revokedBy") String revokedBy);

    /**
     * 역할 매핑 삭제 (Hard Delete)
     */
    void delete(@Param("userId") String userId, @Param("roleId") String roleId);
}
