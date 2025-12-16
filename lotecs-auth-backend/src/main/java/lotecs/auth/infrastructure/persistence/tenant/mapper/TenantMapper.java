package lotecs.auth.infrastructure.persistence.tenant.mapper;

import lotecs.auth.domain.tenant.model.Tenant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface TenantMapper {

    /**
     * 테넌트 ID로 조회
     */
    Optional<Tenant> findById(@Param("tenantId") String tenantId);

    /**
     * 사이트 코드로 조회
     */
    Optional<Tenant> findBySiteCode(@Param("siteCode") String siteCode);

    /**
     * 도메인으로 조회
     */
    Optional<Tenant> findByDomain(@Param("domain") String domain);

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
    List<Tenant> findByStatus(@Param("status") String status);

    /**
     * 상위 테넌트 ID로 하위 테넌트 목록 조회
     */
    List<Tenant> findByParentTenantId(@Param("parentTenantId") String parentTenantId);

    /**
     * 테넌트 등록
     */
    void insert(Tenant tenant);

    /**
     * 테넌트 정보 수정
     */
    void update(Tenant tenant);

    /**
     * 테넌트 삭제
     */
    void delete(@Param("tenantId") String tenantId);
}
