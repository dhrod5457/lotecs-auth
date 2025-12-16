package lotecs.auth.infrastructure.persistence.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.user.model.Permission;
import lotecs.auth.domain.user.repository.PermissionRepository;
import lotecs.auth.infrastructure.persistence.user.mapper.PermissionMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PermissionRepositoryImpl implements PermissionRepository {

    private final PermissionMapper permissionMapper;

    @Override
    public Optional<Permission> findById(String permissionId) {
        log.debug("Finding permission by id: {}", permissionId);
        return permissionMapper.findById(permissionId);
    }

    @Override
    public Optional<Permission> findByPermissionName(String permissionName, String tenantId) {
        log.debug("Finding permission by name: permissionName={}, tenantId={}", permissionName, tenantId);
        return permissionMapper.findByPermissionName(permissionName, tenantId);
    }

    @Override
    public Optional<Permission> findByResourceAndAction(String resource, String action, String tenantId) {
        log.debug("Finding permission by resource and action: resource={}, action={}, tenantId={}", resource, action, tenantId);
        return permissionMapper.findByResourceAndAction(resource, action, tenantId);
    }

    @Override
    public List<Permission> findByTenantId(String tenantId) {
        log.debug("Finding permissions by tenantId: {}", tenantId);
        return permissionMapper.findByTenantId(tenantId);
    }

    @Override
    public List<Permission> findByRoleId(String roleId) {
        log.debug("Finding permissions by roleId: {}", roleId);
        return permissionMapper.findByRoleId(roleId);
    }

    @Override
    public Permission save(Permission permission) {
        if (permission.getPermissionId() == null) {
            log.debug("Inserting new permission: permissionName={}", permission.getPermissionName());
            permissionMapper.insert(permission);
        } else {
            log.debug("Updating permission: permissionId={}", permission.getPermissionId());
            permissionMapper.update(permission);
        }
        return permission;
    }

    @Override
    public void delete(String permissionId) {
        log.debug("Deleting permission: permissionId={}", permissionId);
        permissionMapper.delete(permissionId);
    }
}
