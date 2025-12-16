package lotecs.auth.infrastructure.persistence.sso.mapper;

import lotecs.auth.domain.sso.model.TenantSsoConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface TenantSsoConfigMapper {

    /**
     * 테넌트 ID로 SSO 설정 조회
     */
    Optional<TenantSsoConfig> findByTenantId(@Param("tenantId") String tenantId);

    /**
     * SSO 설정 등록
     */
    void insert(TenantSsoConfig config);

    /**
     * SSO 설정 수정
     */
    void update(TenantSsoConfig config);

    /**
     * SSO 설정 삭제
     */
    void deleteByTenantId(@Param("tenantId") String tenantId);
}
