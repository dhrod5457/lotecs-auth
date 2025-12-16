package lotecs.auth.infrastructure.persistence.organization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.organization.model.Organization;
import lotecs.auth.domain.organization.repository.OrganizationRepository;
import lotecs.auth.infrastructure.persistence.organization.mapper.OrganizationMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrganizationRepositoryImpl implements OrganizationRepository {

    private final OrganizationMapper organizationMapper;

    @Override
    public Optional<Organization> findById(Long id) {
        log.debug("Finding organization by id: {}", id);
        return organizationMapper.findById(id);
    }

    @Override
    public Optional<Organization> findByOrganizationId(String organizationId) {
        log.debug("Finding organization by organizationId: {}", organizationId);
        return organizationMapper.findByOrganizationId(organizationId);
    }

    @Override
    public Optional<Organization> findByTenantIdAndCode(String tenantId, String organizationCode) {
        log.debug("Finding organization by tenantId={}, organizationCode={}", tenantId, organizationCode);
        return organizationMapper.findByTenantIdAndCode(tenantId, organizationCode);
    }

    @Override
    public List<Organization> findByTenantId(String tenantId) {
        log.debug("Finding organizations by tenantId: {}", tenantId);
        return organizationMapper.findByTenantId(tenantId);
    }

    @Override
    public List<Organization> findByParentOrganizationId(String parentOrganizationId) {
        log.debug("Finding organizations by parentOrganizationId: {}", parentOrganizationId);
        return organizationMapper.findByParentOrganizationId(parentOrganizationId);
    }

    @Override
    public List<Organization> findByTenantIdAndType(String tenantId, String organizationType) {
        log.debug("Finding organizations by tenantId={}, organizationType={}", tenantId, organizationType);
        return organizationMapper.findByTenantIdAndType(tenantId, organizationType);
    }

    @Override
    public List<Organization> findRootOrganizations(String tenantId) {
        log.debug("Finding root organizations by tenantId: {}", tenantId);
        return organizationMapper.findRootOrganizations(tenantId);
    }

    @Override
    public List<Organization> findActiveByTenantId(String tenantId) {
        log.debug("Finding active organizations by tenantId: {}", tenantId);
        return organizationMapper.findActiveByTenantId(tenantId);
    }

    @Override
    public Organization save(Organization organization) {
        if (organization.getId() == null) {
            log.debug("Inserting new organization: organizationCode={}", organization.getOrganizationCode());
            organizationMapper.insert(organization);
        } else {
            log.debug("Updating organization: id={}", organization.getId());
            organizationMapper.update(organization);
        }
        return organization;
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting organization: id={}", id);
        organizationMapper.delete(id);
    }

    @Override
    public void deleteByOrganizationId(String organizationId) {
        log.debug("Deleting organization: organizationId={}", organizationId);
        organizationMapper.deleteByOrganizationId(organizationId);
    }
}
