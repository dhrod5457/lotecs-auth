package lotecs.auth.infrastructure.persistence.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.user.model.RoleStatus;
import lotecs.auth.domain.user.repository.RoleStatusRepository;
import lotecs.auth.infrastructure.persistence.user.mapper.RoleStatusMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RoleStatusRepositoryImpl implements RoleStatusRepository {

    private final RoleStatusMapper roleStatusMapper;

    @Override
    public Optional<RoleStatus> findByStatusCode(String statusCode) {
        log.debug("Finding role status by statusCode: {}", statusCode);
        return roleStatusMapper.findByStatusCode(statusCode);
    }

    @Override
    public List<RoleStatus> findAll() {
        log.debug("Finding all role statuses");
        return roleStatusMapper.findAll();
    }

    @Override
    public List<RoleStatus> findActive() {
        log.debug("Finding active role statuses");
        return roleStatusMapper.findActive();
    }

    @Override
    public List<RoleStatus> findByRoleCategory(String roleCategory) {
        log.debug("Finding role statuses by roleCategory: {}", roleCategory);
        return roleStatusMapper.findByRoleCategory(roleCategory);
    }

    @Override
    public Optional<RoleStatus> findDefaultByRoleCategory(String roleCategory) {
        log.debug("Finding default role status by roleCategory: {}", roleCategory);
        return roleStatusMapper.findDefaultByRoleCategory(roleCategory);
    }

    @Override
    public RoleStatus save(RoleStatus roleStatus) {
        if (roleStatusMapper.findByStatusCode(roleStatus.getStatusCode()).isEmpty()) {
            log.debug("Inserting new role status: statusCode={}", roleStatus.getStatusCode());
            roleStatusMapper.insert(roleStatus);
        } else {
            log.debug("Updating role status: statusCode={}", roleStatus.getStatusCode());
            roleStatusMapper.update(roleStatus);
        }
        return roleStatus;
    }

    @Override
    public void delete(String statusCode) {
        log.debug("Deleting role status: statusCode={}", statusCode);
        roleStatusMapper.delete(statusCode);
    }
}
