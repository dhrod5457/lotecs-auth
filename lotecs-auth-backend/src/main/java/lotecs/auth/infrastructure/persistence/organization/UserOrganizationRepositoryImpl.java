package lotecs.auth.infrastructure.persistence.organization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.organization.model.UserOrganization;
import lotecs.auth.domain.organization.repository.UserOrganizationRepository;
import lotecs.auth.infrastructure.persistence.organization.mapper.UserOrganizationMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserOrganizationRepositoryImpl implements UserOrganizationRepository {

    private final UserOrganizationMapper userOrganizationMapper;

    @Override
    public Optional<UserOrganization> findById(Long id) {
        log.debug("Finding user organization by id: {}", id);
        return userOrganizationMapper.findById(id);
    }

    @Override
    public List<UserOrganization> findByUserId(String userId) {
        log.debug("Finding user organizations by userId: {}", userId);
        return userOrganizationMapper.findByUserId(userId);
    }

    @Override
    public List<UserOrganization> findActiveByUserId(String userId) {
        log.debug("Finding active user organizations by userId: {}", userId);
        return userOrganizationMapper.findActiveByUserId(userId);
    }

    @Override
    public Optional<UserOrganization> findPrimaryByUserId(String userId) {
        log.debug("Finding primary user organization by userId: {}", userId);
        return userOrganizationMapper.findPrimaryByUserId(userId);
    }

    @Override
    public List<UserOrganization> findByOrganizationId(String organizationId) {
        log.debug("Finding user organizations by organizationId: {}", organizationId);
        return userOrganizationMapper.findByOrganizationId(organizationId);
    }

    @Override
    public List<UserOrganization> findActiveByOrganizationId(String organizationId) {
        log.debug("Finding active user organizations by organizationId: {}", organizationId);
        return userOrganizationMapper.findActiveByOrganizationId(organizationId);
    }

    @Override
    public List<UserOrganization> findByTenantId(String tenantId) {
        log.debug("Finding user organizations by tenantId: {}", tenantId);
        return userOrganizationMapper.findByTenantId(tenantId);
    }

    @Override
    public UserOrganization save(UserOrganization userOrganization) {
        if (userOrganization.getId() == null) {
            log.debug("Inserting new user organization: userId={}, organizationId={}",
                    userOrganization.getUserId(), userOrganization.getOrganizationId());
            userOrganizationMapper.insert(userOrganization);
        } else {
            log.debug("Updating user organization: id={}", userOrganization.getId());
            userOrganizationMapper.update(userOrganization);
        }
        return userOrganization;
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting user organization: id={}", id);
        userOrganizationMapper.delete(id);
    }

    @Override
    public void deleteByUserId(String userId) {
        log.debug("Deleting user organizations by userId: {}", userId);
        userOrganizationMapper.deleteByUserId(userId);
    }

    @Override
    public void deleteByOrganizationId(String organizationId) {
        log.debug("Deleting user organizations by organizationId: {}", organizationId);
        userOrganizationMapper.deleteByOrganizationId(organizationId);
    }
}
