package lotecs.auth.infrastructure.persistence.sso.mapper;

import lotecs.auth.domain.sso.model.ExternalUserMapping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
     * 사용자 ID로 매핑 조회
     */
    Optional<ExternalUserMapping> findByUserId(@Param("userId") Long userId);

    /**
     * 매핑 등록
     */
    void insert(ExternalUserMapping mapping);

    /**
     * 마지막 동기화 시간 업데이트
     */
    void updateLastSyncedAt(@Param("mappingId") Long mappingId);

    /**
     * 매핑 삭제
     */
    void deleteByUserId(@Param("userId") Long userId);
}
