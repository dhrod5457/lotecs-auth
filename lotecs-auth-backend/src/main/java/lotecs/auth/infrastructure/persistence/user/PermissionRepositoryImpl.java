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
    public Optional<Permission> findById(Long permissionId) {
        log.debug("Finding permission by id: {}", permissionId);
        return permissionMapper.findById(permissionId);
    }

    @Override
    public Optional<Permission> findByCode(String permissionCode) {
        log.debug("Finding permission by code: {}", permissionCode);
        return permissionMapper.findByCode(permissionCode);
    }

    @Override
    public List<Permission> findAll() {
        log.debug("Finding all permissions");
        return permissionMapper.findAll();
    }
}
