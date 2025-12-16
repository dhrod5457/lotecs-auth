package lotecs.auth.domain.sso.repository;

import lotecs.auth.domain.sso.model.ExternalUserMapping;

import java.util.List;
import java.util.Optional;

public interface ExternalUserMappingRepository {

    /**
     * 외부 사용자 ID로 매핑 조회
     */
    Optional<ExternalUserMapping> findByExternalUserId(String tenantId, String externalUserId, String externalSystem);

    /**
     * 내부 사용자 ID로 매핑 목록 조회
     */
    List<ExternalUserMapping> findByUserId(String userId);

    /**
     * 사용자 ID와 외부 시스템으로 매핑 조회
     */
    Optional<ExternalUserMapping> findByUserIdAndExternalSystem(String userId, String externalSystem);

    /**
     * 매핑 저장
     */
    ExternalUserMapping save(ExternalUserMapping mapping);

    /**
     * 매핑 삭제 (사용자 ID로)
     */
    void deleteByUserId(String userId);

    /**
     * 매핑 삭제 (매핑 ID로)
     */
    void deleteById(String mappingId);
}
