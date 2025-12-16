package lotecs.auth.domain.tenant.repository;

import lotecs.auth.domain.tenant.model.Tenant;

import java.util.List;
import java.util.Optional;

public interface TenantRepository {

    /**
     * 테넌트 ID로 조회
     */
    Optional<Tenant> findById(String tenantId);

    /**
     * 사이트 코드로 조회
     */
    Optional<Tenant> findBySiteCode(String siteCode);

    /**
     * 도메인으로 조회
     */
    Optional<Tenant> findByDomain(String domain);

    /**
     * 전체 테넌트 목록 조회
     */
    List<Tenant> findAll();

    /**
     * 활성 테넌트 목록 조회
     */
    List<Tenant> findActive();

    /**
     * 상태별 테넌트 목록 조회
     */
    List<Tenant> findByStatus(String status);

    /**
     * 상위 테넌트 ID로 하위 테넌트 목록 조회
     */
    List<Tenant> findByParentTenantId(String parentTenantId);

    /**
     * 테넌트 저장
     */
    Tenant save(Tenant tenant);

    /**
     * 테넌트 삭제
     */
    void delete(String tenantId);
}
