package lotecs.auth.domain.sso.repository;

import lotecs.auth.domain.sso.model.ExternalUserMapping;

import java.util.Optional;

public interface ExternalUserMappingRepository {

    /**
     * 외부 사용자 ID로 매핑 조회
     */
    Optional<ExternalUserMapping> findByExternalUserId(String tenantId, String externalUserId, String externalSystem);

    /**
     * 내부 사용자 ID로 매핑 조회
     */
    Optional<ExternalUserMapping> findByUserId(Long userId);

    /**
     * 매핑 저장
     */
    ExternalUserMapping save(ExternalUserMapping mapping);
}
