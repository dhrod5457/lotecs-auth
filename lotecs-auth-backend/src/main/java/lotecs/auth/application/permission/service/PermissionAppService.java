package lotecs.auth.application.permission.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.application.permission.dto.CreatePermissionRequest;
import lotecs.auth.application.permission.dto.PermissionDto;
import lotecs.auth.application.permission.dto.UpdatePermissionRequest;
import lotecs.auth.application.permission.mapper.PermissionDtoMapper;
import lotecs.auth.domain.user.model.Permission;
import lotecs.auth.domain.user.model.RolePermission;
import lotecs.auth.domain.user.repository.PermissionRepository;
import lotecs.auth.domain.user.repository.RoleRepository;
import lotecs.auth.infrastructure.persistence.user.mapper.RolePermissionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionAppService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionDtoMapper permissionDtoMapper;

    @Transactional(readOnly = true)
    public PermissionDto getPermission(String permissionId, String tenantId) {
        log.debug("[PERM-001] 권한 조회: permissionId={}, tenantId={}", permissionId, tenantId);

        Permission permission = permissionRepository.findById(permissionId)
                .filter(p -> p.getTenantId().equals(tenantId))
                .orElseThrow(() -> {
                    log.warn("[PERM-002] 권한을 찾을 수 없음: permissionId={}", permissionId);
                    return new IllegalArgumentException("Permission not found: " + permissionId);
                });

        return permissionDtoMapper.toDto(permission);
    }

    @Transactional(readOnly = true)
    public List<PermissionDto> listPermissions(String tenantId) {
        log.debug("[PERM-003] 권한 목록 조회: tenantId={}", tenantId);

        List<Permission> permissions = permissionRepository.findByTenantId(tenantId);
        return permissions.stream()
                .map(permissionDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PermissionDto createPermission(CreatePermissionRequest request) {
        log.info("[PERM-004] 권한 생성: tenantId={}, permissionName={}", request.getTenantId(), request.getPermissionName());

        request.validate();

        // 중복 확인 (permissionName)
        if (permissionRepository.findByPermissionName(request.getPermissionName(), request.getTenantId()).isPresent()) {
            log.warn("[PERM-005] 중복된 권한명: permissionName={}", request.getPermissionName());
            throw new IllegalArgumentException("Permission name already exists: " + request.getPermissionName());
        }

        // 중복 확인 (resource + action)
        if (permissionRepository.findByResourceAndAction(request.getResource(), request.getAction(), request.getTenantId()).isPresent()) {
            log.warn("[PERM-006] 중복된 리소스/액션: resource={}, action={}", request.getResource(), request.getAction());
            throw new IllegalArgumentException("Permission with resource and action already exists");
        }

        Permission permission = Permission.create(
                request.getTenantId(),
                request.getPermissionName(),
                request.getResource(),
                request.getAction(),
                request.getDescription(),
                request.getCreatedBy()
        );

        permission = permissionRepository.save(permission);

        log.info("[PERM-007] 권한 생성 완료: permissionId={}", permission.getPermissionId());
        return permissionDtoMapper.toDto(permission);
    }

    @Transactional
    public PermissionDto updatePermission(UpdatePermissionRequest request) {
        log.info("[PERM-008] 권한 수정: permissionId={}, tenantId={}", request.getPermissionId(), request.getTenantId());

        request.validate();

        Permission permission = permissionRepository.findById(request.getPermissionId())
                .filter(p -> p.getTenantId().equals(request.getTenantId()))
                .orElseThrow(() -> {
                    log.warn("[PERM-009] 권한을 찾을 수 없음: permissionId={}", request.getPermissionId());
                    return new IllegalArgumentException("Permission not found: " + request.getPermissionId());
                });

        permission.updateInfo(request.getDescription(), request.getUpdatedBy());
        permission = permissionRepository.save(permission);

        log.info("[PERM-010] 권한 수정 완료: permissionId={}", permission.getPermissionId());
        return permissionDtoMapper.toDto(permission);
    }

    @Transactional
    public void deletePermission(String permissionId, String tenantId) {
        log.info("[PERM-011] 권한 삭제: permissionId={}, tenantId={}", permissionId, tenantId);

        permissionRepository.findById(permissionId)
                .filter(p -> p.getTenantId().equals(tenantId))
                .orElseThrow(() -> {
                    log.warn("[PERM-012] 권한을 찾을 수 없음: permissionId={}", permissionId);
                    return new IllegalArgumentException("Permission not found: " + permissionId);
                });

        // 역할-권한 매핑 삭제
        rolePermissionMapper.deleteAllByPermissionId(permissionId);

        permissionRepository.delete(permissionId);

        log.info("[PERM-013] 권한 삭제 완료: permissionId={}", permissionId);
    }

    @Transactional(readOnly = true)
    public List<PermissionDto> getRolePermissions(String roleId, String tenantId) {
        log.debug("[PERM-014] 역할 권한 목록 조회: roleId={}, tenantId={}", roleId, tenantId);

        // 역할 존재 확인
        roleRepository.findByIdAndTenantId(roleId, tenantId)
                .orElseThrow(() -> {
                    log.warn("[PERM-015] 역할을 찾을 수 없음: roleId={}", roleId);
                    return new IllegalArgumentException("Role not found: " + roleId);
                });

        List<Permission> permissions = permissionRepository.findByRoleId(roleId);
        return permissions.stream()
                .map(permissionDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void assignPermissionsToRole(String roleId, List<String> permissionIds, String tenantId, String grantedBy) {
        log.info("[PERM-016] 역할에 권한 할당: roleId={}, permissionIds={}, tenantId={}", roleId, permissionIds, tenantId);

        // 역할 존재 확인
        roleRepository.findByIdAndTenantId(roleId, tenantId)
                .orElseThrow(() -> {
                    log.warn("[PERM-017] 역할을 찾을 수 없음: roleId={}", roleId);
                    return new IllegalArgumentException("Role not found: " + roleId);
                });

        List<RolePermission> rolePermissions = permissionIds.stream()
                .filter(permissionId -> {
                    // 권한 존재 확인
                    boolean exists = permissionRepository.findById(permissionId)
                            .filter(p -> p.getTenantId().equals(tenantId))
                            .isPresent();
                    if (!exists) {
                        log.warn("[PERM-018] 권한을 찾을 수 없음: permissionId={}", permissionId);
                    }
                    return exists;
                })
                .filter(permissionId -> {
                    // 이미 할당된 권한 제외
                    boolean alreadyAssigned = rolePermissionMapper.findByRoleIdAndPermissionId(roleId, permissionId).isPresent();
                    if (alreadyAssigned) {
                        log.debug("[PERM-019] 이미 할당된 권한: roleId={}, permissionId={}", roleId, permissionId);
                    }
                    return !alreadyAssigned;
                })
                .map(permissionId -> RolePermission.grant(roleId, permissionId, tenantId, grantedBy))
                .collect(Collectors.toList());

        if (!rolePermissions.isEmpty()) {
            rolePermissionMapper.insertBatch(rolePermissions);
            log.info("[PERM-020] 역할에 권한 할당 완료: roleId={}, count={}", roleId, rolePermissions.size());
        }
    }

    @Transactional
    public void revokePermissionFromRole(String roleId, String permissionId, String tenantId) {
        log.info("[PERM-021] 역할에서 권한 회수: roleId={}, permissionId={}, tenantId={}", roleId, permissionId, tenantId);

        // 역할 존재 확인
        roleRepository.findByIdAndTenantId(roleId, tenantId)
                .orElseThrow(() -> {
                    log.warn("[PERM-022] 역할을 찾을 수 없음: roleId={}", roleId);
                    return new IllegalArgumentException("Role not found: " + roleId);
                });

        rolePermissionMapper.delete(roleId, permissionId);

        log.info("[PERM-023] 역할에서 권한 회수 완료: roleId={}, permissionId={}", roleId, permissionId);
    }

    /**
     * 사용자 권한 확인
     *
     * @param userId 사용자 ID
     * @param permissionCode 권한 코드 (resource:action 형식 또는 permissionName)
     * @param tenantId 테넌트 ID
     * @return 권한 보유 여부
     */
    @Transactional(readOnly = true)
    public boolean hasPermission(String userId, String permissionCode, String tenantId) {
        log.debug("[PERM-024] 권한 확인: userId={}, permissionCode={}, tenantId={}", userId, permissionCode, tenantId);

        // 사용자의 역할 목록 조회
        List<lotecs.auth.domain.user.model.Role> roles = roleRepository.findByUserId(userId);

        if (roles.isEmpty()) {
            log.debug("[PERM-025] 사용자에게 할당된 역할 없음: userId={}", userId);
            return false;
        }

        // 각 역할의 권한 조회 및 확인
        for (lotecs.auth.domain.user.model.Role role : roles) {
            List<Permission> permissions = permissionRepository.findByRoleId(role.getRoleId());

            for (Permission permission : permissions) {
                // permissionCode가 "resource:action" 형식인 경우
                String authority = permission.toAuthority();
                if (authority.equals(permissionCode)) {
                    log.debug("[PERM-026] 권한 확인 성공 (authority): userId={}, permissionCode={}, roleId={}",
                            userId, permissionCode, role.getRoleId());
                    return true;
                }

                // permissionCode가 permissionName인 경우
                if (permission.getPermissionName().equals(permissionCode)) {
                    log.debug("[PERM-027] 권한 확인 성공 (permissionName): userId={}, permissionCode={}, roleId={}",
                            userId, permissionCode, role.getRoleId());
                    return true;
                }
            }
        }

        log.debug("[PERM-028] 권한 없음: userId={}, permissionCode={}", userId, permissionCode);
        return false;
    }

    /**
     * 사용자가 여러 권한 중 하나라도 보유하고 있는지 확인
     */
    @Transactional(readOnly = true)
    public boolean hasAnyPermission(String userId, List<String> permissionCodes, String tenantId) {
        log.debug("[PERM-029] 권한 확인 (OR): userId={}, permissionCodes={}, tenantId={}", userId, permissionCodes, tenantId);

        for (String permissionCode : permissionCodes) {
            if (hasPermission(userId, permissionCode, tenantId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 사용자가 모든 권한을 보유하고 있는지 확인
     */
    @Transactional(readOnly = true)
    public boolean hasAllPermissions(String userId, List<String> permissionCodes, String tenantId) {
        log.debug("[PERM-030] 권한 확인 (AND): userId={}, permissionCodes={}, tenantId={}", userId, permissionCodes, tenantId);

        for (String permissionCode : permissionCodes) {
            if (!hasPermission(userId, permissionCode, tenantId)) {
                return false;
            }
        }
        return true;
    }
}
