package lotecs.auth.application.permission.mapper;

import lotecs.auth.application.permission.dto.PermissionDto;
import lotecs.auth.domain.user.model.Permission;
import org.springframework.stereotype.Component;

@Component
public class PermissionDtoMapper {

    public PermissionDto toDto(Permission permission) {
        if (permission == null) {
            return null;
        }

        return PermissionDto.builder()
                .permissionId(permission.getPermissionId())
                .tenantId(permission.getTenantId())
                .permissionName(permission.getPermissionName())
                .resource(permission.getResource())
                .action(permission.getAction())
                .description(permission.getDescription())
                .createdBy(permission.getCreatedBy())
                .createdAt(permission.getCreatedAt())
                .updatedBy(permission.getUpdatedBy())
                .updatedAt(permission.getUpdatedAt())
                .build();
    }
}
