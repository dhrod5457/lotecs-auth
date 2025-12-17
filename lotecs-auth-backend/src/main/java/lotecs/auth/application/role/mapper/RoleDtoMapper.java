package lotecs.auth.application.role.mapper;

import lotecs.auth.application.role.dto.RoleDto;
import lotecs.auth.domain.user.model.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleDtoMapper {

    public RoleDto toDto(Role role) {
        if (role == null) {
            return null;
        }

        return RoleDto.builder()
                .roleId(role.getRoleId())
                .tenantId(role.getTenantId())
                .roleName(role.getRoleName())
                .displayName(role.getDisplayName())
                .description(role.getDescription())
                .priority(role.getPriority())
                .createdBy(role.getCreatedBy())
                .createdAt(role.getCreatedAt())
                .updatedBy(role.getUpdatedBy())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}
