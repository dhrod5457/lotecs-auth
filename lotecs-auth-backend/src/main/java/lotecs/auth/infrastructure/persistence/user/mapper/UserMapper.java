package lotecs.auth.infrastructure.persistence.user.mapper;

import lotecs.auth.domain.user.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {

    /**
     * ID로 사용자 조회
     */
    Optional<User> findById(@Param("userId") String userId);

    /**
     * 사용자명과 테넌트 ID로 사용자 조회
     */
    Optional<User> findByUsernameAndTenantId(@Param("username") String username, @Param("tenantId") String tenantId);

    /**
     * 테넌트 ID로 사용자 목록 조회 (페이징)
     */
    List<User> findByTenantId(@Param("tenantId") String tenantId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 테넌트 ID로 사용자 수 조회
     */
    int countByTenantId(@Param("tenantId") String tenantId);

    /**
     * 사용자 등록
     */
    void insert(User user);

    /**
     * 사용자 정보 수정
     */
    void update(User user);

    /**
     * 사용자 삭제
     */
    void delete(@Param("userId") String userId);
}
