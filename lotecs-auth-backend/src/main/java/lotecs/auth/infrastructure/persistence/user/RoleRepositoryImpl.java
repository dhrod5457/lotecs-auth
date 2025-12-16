package lotecs.auth.infrastructure.persistence.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.user.model.Role;
import lotecs.auth.domain.user.repository.RoleRepository;
import lotecs.auth.infrastructure.persistence.user.mapper.RoleMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleMapper roleMapper;

    @Override
    public Optional<Role> findById(String roleId) {
        log.debug("Finding role by id: {}", roleId);
        return roleMapper.findById(roleId);
    }

    @Override
    public Optional<Role> findByRoleName(String tenantId, String roleName) {
        log.debug("Finding role by name: tenantId={}, roleName={}", tenantId, roleName);
        return roleMapper.findByRoleName(roleName, tenantId);
    }

    @Override
    public List<Role> findByTenantId(String tenantId) {
        log.debug("Finding roles by tenantId: {}", tenantId);
        return roleMapper.findByTenantId(tenantId);
    }

    @Override
    public List<Role> findByUserId(String userId) {
        log.debug("Finding roles by userId: {}", userId);
        return roleMapper.findByUserId(userId);
    }

    @Override
    public Role save(Role role) {
        if (role.getRoleId() == null) {
            log.debug("Inserting new role: roleName={}", role.getRoleName());
            roleMapper.insert(role);
        } else {
            log.debug("Updating role: roleId={}", role.getRoleId());
            roleMapper.update(role);
        }
        return role;
    }

    @Override
    public void delete(String roleId) {
        log.debug("Deleting role: roleId={}", roleId);
        roleMapper.delete(roleId);
    }
}
