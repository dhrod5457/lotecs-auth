package lotecs.auth.domain.sso.repository;

import lotecs.auth.domain.sso.model.TenantSsoConfig;

import java.util.Optional;

public interface TenantSsoConfigRepository {

    /**
     * 테넌트 ID로 SSO 설정 조회
     */
    Optional<TenantSsoConfig> findByTenantId(String tenantId);

    /**
     * SSO 설정 저장
     */
    TenantSsoConfig save(TenantSsoConfig config);
}
