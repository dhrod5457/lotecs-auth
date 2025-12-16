package lotecs.auth.infrastructure.persistence.sso;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.sso.model.TenantSsoConfig;
import lotecs.auth.domain.sso.repository.TenantSsoConfigRepository;
import lotecs.auth.infrastructure.persistence.sso.mapper.TenantSsoConfigMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TenantSsoConfigRepositoryImpl implements TenantSsoConfigRepository {

    private final TenantSsoConfigMapper tenantSsoConfigMapper;

    @Override
    public Optional<TenantSsoConfig> findByTenantId(String tenantId) {
        log.debug("Finding SSO config by tenantId: {}", tenantId);
        return tenantSsoConfigMapper.findByTenantId(tenantId);
    }

    @Override
    public TenantSsoConfig save(TenantSsoConfig config) {
        Optional<TenantSsoConfig> existing = tenantSsoConfigMapper.findByTenantId(config.getTenantId());
        if (existing.isEmpty()) {
            log.debug("Inserting new SSO config for tenantId: {}", config.getTenantId());
            tenantSsoConfigMapper.insert(config);
        } else {
            log.debug("Updating SSO config for tenantId: {}", config.getTenantId());
            tenantSsoConfigMapper.update(config);
        }
        return config;
    }
}
