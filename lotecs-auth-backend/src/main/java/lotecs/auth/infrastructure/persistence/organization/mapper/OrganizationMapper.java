package lotecs.auth.infrastructure.persistence.organization.mapper;

import lotecs.auth.domain.organization.model.Organization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface OrganizationMapper {

    /**
     * 내부 ID로 조회
     */
    Optional<Organization> findById(@Param("id") Long id);

    /**
     * 조직 ID로 조회
     */
    Optional<Organization> findByOrganizationId(@Param("organizationId") String organizationId);

    /**
     * 테넌트 ID와 조직 코드로 조회
     */
    Optional<Organization> findByTenantIdAndCode(@Param("tenantId") String tenantId,
                                                  @Param("organizationCode") String organizationCode);

    /**
     * 테넌트 ID로 조직 목록 조회
     */
    List<Organization> findByTenantId(@Param("tenantId") String tenantId);

    /**
     * 상위 조직 ID로 하위 조직 목록 조회
     */
    List<Organization> findByParentOrganizationId(@Param("parentOrganizationId") String parentOrganizationId);

    /**
     * 테넌트 ID와 조직 유형으로 조회
     */
    List<Organization> findByTenantIdAndType(@Param("tenantId") String tenantId,
                                              @Param("organizationType") String organizationType);

    /**
     * 최상위 조직 목록 조회
     */
    List<Organization> findRootOrganizations(@Param("tenantId") String tenantId);

    /**
     * 활성 조직 목록 조회
     */
    List<Organization> findActiveByTenantId(@Param("tenantId") String tenantId);

    /**
     * 조직 등록
     */
    void insert(Organization organization);

    /**
     * 조직 정보 수정
     */
    void update(Organization organization);

    /**
     * 조직 삭제
     */
    void delete(@Param("id") Long id);

    /**
     * 조직 ID로 삭제
     */
    void deleteByOrganizationId(@Param("organizationId") String organizationId);
}
