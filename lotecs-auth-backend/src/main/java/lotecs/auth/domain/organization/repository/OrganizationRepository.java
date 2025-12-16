package lotecs.auth.domain.organization.repository;

import lotecs.auth.domain.organization.model.Organization;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository {

    /**
     * 내부 ID로 조회
     */
    Optional<Organization> findById(Long id);

    /**
     * 조직 ID로 조회
     */
    Optional<Organization> findByOrganizationId(String organizationId);

    /**
     * 테넌트 ID와 조직 코드로 조회
     */
    Optional<Organization> findByTenantIdAndCode(String tenantId, String organizationCode);

    /**
     * 테넌트 ID로 조직 목록 조회
     */
    List<Organization> findByTenantId(String tenantId);

    /**
     * 상위 조직 ID로 하위 조직 목록 조회
     */
    List<Organization> findByParentOrganizationId(String parentOrganizationId);

    /**
     * 테넌트 ID와 조직 유형으로 조회
     */
    List<Organization> findByTenantIdAndType(String tenantId, String organizationType);

    /**
     * 최상위 조직 목록 조회
     */
    List<Organization> findRootOrganizations(String tenantId);

    /**
     * 활성 조직 목록 조회
     */
    List<Organization> findActiveByTenantId(String tenantId);

    /**
     * 조직 저장
     */
    Organization save(Organization organization);

    /**
     * 조직 삭제
     */
    void delete(Long id);

    /**
     * 조직 ID로 삭제
     */
    void deleteByOrganizationId(String organizationId);
}
