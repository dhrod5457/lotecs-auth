package lotecs.auth.infrastructure.persistence.organization.mapper;

import lotecs.auth.domain.organization.model.UserOrganization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserOrganizationMapper {

    /**
     * 내부 ID로 조회
     */
    Optional<UserOrganization> findById(@Param("id") Long id);

    /**
     * 사용자 ID로 소속 조직 목록 조회
     */
    List<UserOrganization> findByUserId(@Param("userId") String userId);

    /**
     * 사용자 ID로 활성 소속 조직 목록 조회
     */
    List<UserOrganization> findActiveByUserId(@Param("userId") String userId);

    /**
     * 사용자의 주 소속 조직 조회
     */
    Optional<UserOrganization> findPrimaryByUserId(@Param("userId") String userId);

    /**
     * 조직 ID로 소속 사용자 목록 조회
     */
    List<UserOrganization> findByOrganizationId(@Param("organizationId") String organizationId);

    /**
     * 조직 ID로 활성 소속 사용자 목록 조회
     */
    List<UserOrganization> findActiveByOrganizationId(@Param("organizationId") String organizationId);

    /**
     * 테넌트 ID로 전체 조회
     */
    List<UserOrganization> findByTenantId(@Param("tenantId") String tenantId);

    /**
     * 사용자-조직 매핑 등록
     */
    void insert(UserOrganization userOrganization);

    /**
     * 사용자-조직 매핑 수정
     */
    void update(UserOrganization userOrganization);

    /**
     * 사용자-조직 매핑 삭제
     */
    void delete(@Param("id") Long id);

    /**
     * 사용자의 모든 조직 매핑 삭제
     */
    void deleteByUserId(@Param("userId") String userId);

    /**
     * 조직의 모든 사용자 매핑 삭제
     */
    void deleteByOrganizationId(@Param("organizationId") String organizationId);
}
