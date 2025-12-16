package lotecs.auth.infrastructure.persistence.tenant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.tenant.model.Tenant;
import lotecs.auth.domain.tenant.repository.TenantRepository;
import lotecs.auth.infrastructure.persistence.tenant.mapper.TenantMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TenantRepositoryImpl implements TenantRepository {

    private final TenantMapper tenantMapper;

    @Override
    public Optional<Tenant> findById(String tenantId) {
        log.debug("Finding tenant by id: {}", tenantId);
        return tenantMapper.findById(tenantId);
    }

    @Override
    public Optional<Tenant> findBySiteCode(String siteCode) {
        log.debug("Finding tenant by siteCode: {}", siteCode);
        return tenantMapper.findBySiteCode(siteCode);
    }

    @Override
    public Optional<Tenant> findByDomain(String domain) {
        log.debug("Finding tenant by domain: {}", domain);
        return tenantMapper.findByDomain(domain);
    }

    @Override
    public List<Tenant> findAll() {
        log.debug("Finding all tenants");
        return tenantMapper.findAll();
    }

    @Override
    public List<Tenant> findActive() {
        log.debug("Finding active tenants");
        return tenantMapper.findActive();
    }

    @Override
    public List<Tenant> findByStatus(String status) {
        log.debug("Finding tenants by status: {}", status);
        return tenantMapper.findByStatus(status);
    }

    @Override
    public List<Tenant> findByParentTenantId(String parentTenantId) {
        log.debug("Finding tenants by parentTenantId: {}", parentTenantId);
        return tenantMapper.findByParentTenantId(parentTenantId);
    }

    @Override
    public Tenant save(Tenant tenant) {
        if (tenantMapper.findById(tenant.getTenantId()).isEmpty()) {
            log.debug("Inserting new tenant: tenantId={}", tenant.getTenantId());
            tenantMapper.insert(tenant);
        } else {
            log.debug("Updating tenant: tenantId={}", tenant.getTenantId());
            tenantMapper.update(tenant);
        }
        return tenant;
    }

    @Override
    public void delete(String tenantId) {
        log.debug("Deleting tenant: tenantId={}", tenantId);
        tenantMapper.delete(tenantId);
    }
}
