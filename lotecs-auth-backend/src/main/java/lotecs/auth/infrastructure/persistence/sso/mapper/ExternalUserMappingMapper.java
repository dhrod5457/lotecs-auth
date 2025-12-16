package lotecs.auth.infrastructure.persistence.sso.mapper;

import lotecs.auth.domain.sso.model.ExternalUserMapping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ExternalUserMappingMapper {

    /**
     * 외부 사용자 ID로 매핑 조회
     */
    Optional<ExternalUserMapping> findByExternalUserId(
            @Param("externalUserId") String externalUserId,
            @Param("externalSystem") String externalSystem,
            @Param("tenantId") String tenantId
    );

    /**
     * 사용자 ID로 매핑 목록 조회
     */
    List<ExternalUserMapping> findByUserId(@Param("userId") String userId);

    /**
     * 사용자 ID와 외부 시스템으로 매핑 조회
     */
    Optional<ExternalUserMapping> findByUserIdAndExternalSystem(
            @Param("userId") String userId,
            @Param("externalSystem") String externalSystem
    );

    /**
     * 매핑 등록
     */
    void insert(ExternalUserMapping mapping);

    /**
     * 마지막 동기화 시간 업데이트
     */
    void updateLastSyncedAt(@Param("mappingId") String mappingId);

    /**
     * 매핑 삭제 (사용자 ID로)
     */
    void deleteByUserId(@Param("userId") String userId);

    /**
     * 매핑 삭제 (매핑 ID로)
     */
    void deleteById(@Param("mappingId") String mappingId);
}
