package lotecs.auth.application.role.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.application.role.dto.CreateRoleRequest;
import lotecs.auth.application.role.dto.RoleDto;
import lotecs.auth.application.role.dto.UpdateRoleRequest;
import lotecs.auth.application.role.mapper.RoleDtoMapper;
import lotecs.auth.domain.user.model.Role;
import lotecs.auth.domain.user.repository.RoleRepository;
import lotecs.auth.exception.role.RoleAlreadyExistsException;
import lotecs.auth.exception.role.RoleNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleAppService {

    private final RoleRepository roleRepository;
    private final RoleDtoMapper roleDtoMapper;

    @Transactional(readOnly = true)
    public RoleDto getRole(String roleId, String tenantId) {
        log.debug("[ROLE-001] 역할 조회: roleId={}, tenantId={}", roleId, tenantId);

        Role role = roleRepository.findByIdAndTenantId(roleId, tenantId)
                .orElseThrow(() -> {
                    log.warn("[ROLE-002] 역할을 찾을 수 없음: roleId={}", roleId);
                    return RoleNotFoundException.byId(roleId);
                });

        return roleDtoMapper.toDto(role);
    }

    @Transactional(readOnly = true)
    public RoleDto getRoleByName(String roleName, String tenantId) {
        log.debug("[ROLE-003] 역할명으로 조회: roleName={}, tenantId={}", roleName, tenantId);

        Role role = roleRepository.findByRoleNameAndTenantId(roleName, tenantId)
                .orElseThrow(() -> {
                    log.warn("[ROLE-004] 역할을 찾을 수 없음: roleName={}", roleName);
                    return RoleNotFoundException.byName(roleName);
                });

        return roleDtoMapper.toDto(role);
    }

    @Transactional(readOnly = true)
    public List<RoleDto> listRoles(String tenantId) {
        log.debug("[ROLE-005] 역할 목록 조회: tenantId={}", tenantId);

        List<Role> roles = roleRepository.findByTenantId(tenantId);
        return roles.stream()
                .map(roleDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoleDto createRole(CreateRoleRequest request) {
        log.info("[ROLE-006] 역할 생성: tenantId={}, roleName={}", request.getTenantId(), request.getRoleName());

        // 중복 확인
        if (roleRepository.findByRoleNameAndTenantId(request.getRoleName(), request.getTenantId()).isPresent()) {
            log.warn("[ROLE-007] 중복된 역할명: roleName={}", request.getRoleName());
            throw RoleAlreadyExistsException.byName(request.getRoleName());
        }

        Role role = Role.builder()
                .roleId(UUID.randomUUID().toString())
                .tenantId(request.getTenantId())
                .roleName(request.getRoleName())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .priority(request.getPriority() != null ? request.getPriority() : 0)
                .createdBy(request.getCreatedBy())
                .createdAt(LocalDateTime.now())
                .build();

        role.validate();
        role = roleRepository.save(role);

        log.info("[ROLE-008] 역할 생성 완료: roleId={}", role.getRoleId());
        return roleDtoMapper.toDto(role);
    }

    @Transactional
    public RoleDto updateRole(UpdateRoleRequest request) {
        log.info("[ROLE-009] 역할 수정: roleId={}, tenantId={}", request.getRoleId(), request.getTenantId());

        Role role = roleRepository.findByIdAndTenantId(request.getRoleId(), request.getTenantId())
                .orElseThrow(() -> {
                    log.warn("[ROLE-010] 역할을 찾을 수 없음: roleId={}", request.getRoleId());
                    return RoleNotFoundException.byId(request.getRoleId());
                });

        if (request.getDisplayName() != null) {
            role.setDisplayName(request.getDisplayName());
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            role.setPriority(request.getPriority());
        }
        role.setUpdatedBy(request.getUpdatedBy());
        role.setUpdatedAt(LocalDateTime.now());

        role = roleRepository.save(role);

        log.info("[ROLE-011] 역할 수정 완료: roleId={}", role.getRoleId());
        return roleDtoMapper.toDto(role);
    }

    @Transactional
    public void deleteRole(String roleId, String tenantId) {
        log.info("[ROLE-012] 역할 삭제: roleId={}, tenantId={}", roleId, tenantId);

        roleRepository.findByIdAndTenantId(roleId, tenantId)
                .orElseThrow(() -> {
                    log.warn("[ROLE-013] 역할을 찾을 수 없음: roleId={}", roleId);
                    return RoleNotFoundException.byId(roleId);
                });

        roleRepository.delete(roleId);

        log.info("[ROLE-014] 역할 삭제 완료: roleId={}", roleId);
    }

    @Transactional(readOnly = true)
    public List<RoleDto> getUserRoles(String userId, String tenantId) {
        log.debug("[ROLE-015] 사용자 역할 목록 조회: userId={}, tenantId={}", userId, tenantId);

        List<Role> roles = roleRepository.findByUserId(userId);
        return roles.stream()
                .map(roleDtoMapper::toDto)
                .collect(Collectors.toList());
    }
}
