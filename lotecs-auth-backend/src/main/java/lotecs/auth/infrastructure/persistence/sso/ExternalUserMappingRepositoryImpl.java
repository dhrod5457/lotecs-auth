package lotecs.auth.infrastructure.persistence.sso;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.sso.model.ExternalUserMapping;
import lotecs.auth.domain.sso.repository.ExternalUserMappingRepository;
import lotecs.auth.infrastructure.persistence.sso.mapper.ExternalUserMappingMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ExternalUserMappingRepositoryImpl implements ExternalUserMappingRepository {

    private final ExternalUserMappingMapper externalUserMappingMapper;

    @Override
    public Optional<ExternalUserMapping> findByExternalUserId(String tenantId, String externalUserId, String externalSystem) {
        log.debug("Finding external user mapping: tenantId={}, externalUserId={}, externalSystem={}",
                tenantId, externalUserId, externalSystem);
        return externalUserMappingMapper.findByExternalUserId(externalUserId, externalSystem, tenantId);
    }

    @Override
    public Optional<ExternalUserMapping> findByUserId(Long userId) {
        log.debug("Finding external user mapping by userId: {}", userId);
        return externalUserMappingMapper.findByUserId(userId);
    }

    @Override
    public ExternalUserMapping save(ExternalUserMapping mapping) {
        if (mapping.getMappingId() == null) {
            log.debug("Inserting new external user mapping: externalUserId={}", mapping.getExternalUserId());
            externalUserMappingMapper.insert(mapping);
        } else {
            log.debug("Updating external user mapping: mappingId={}", mapping.getMappingId());
            externalUserMappingMapper.updateLastSyncedAt(mapping.getMappingId());
        }
        return mapping;
    }
}
