package lotecs.auth.application.permission.dto;

import lombok.Builder;
import lombok.Getter;
import lotecs.auth.exception.permission.PermissionValidationException;

@Getter
@Builder
public class CreatePermissionRequest {
    private String tenantId;
    private String permissionName;
    private String resource;
    private String action;
    private String description;
    private String createdBy;

    public void validate() {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw PermissionValidationException.tenantRequired();
        }
        if (permissionName == null || permissionName.trim().isEmpty()) {
            throw PermissionValidationException.nameRequired();
        }
        if (resource == null || resource.trim().isEmpty()) {
            throw PermissionValidationException.resourceRequired();
        }
        if (action == null || action.trim().isEmpty()) {
            throw PermissionValidationException.actionRequired();
        }
    }
}
