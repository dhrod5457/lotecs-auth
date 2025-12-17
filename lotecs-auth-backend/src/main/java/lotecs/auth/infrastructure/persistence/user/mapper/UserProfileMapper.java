package lotecs.auth.infrastructure.persistence.user.mapper;

import lotecs.auth.domain.user.model.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * 사용자 프로필 MyBatis Mapper
 */
@Mapper
public interface UserProfileMapper {

    /**
     * 사용자 ID와 테넌트 ID로 프로필 조회
     */
    Optional<UserProfile> findByUserIdAndTenantId(
            @Param("userId") String userId,
            @Param("tenantId") String tenantId
    );

    /**
     * 프로필 등록
     */
    void insert(UserProfile profile);

    /**
     * 프로필 수정
     */
    void update(UserProfile profile);

    /**
     * 프로필 삭제
     */
    void deleteByUserIdAndTenantId(
            @Param("userId") String userId,
            @Param("tenantId") String tenantId
    );

    /**
     * 프로필 존재 여부 확인
     */
    boolean existsByUserIdAndTenantId(
            @Param("userId") String userId,
            @Param("tenantId") String tenantId
    );
}
