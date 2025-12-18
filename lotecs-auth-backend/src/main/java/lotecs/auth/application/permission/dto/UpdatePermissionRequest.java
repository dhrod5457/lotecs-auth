package lotecs.auth.application.permission.dto;

import lombok.Builder;
import lombok.Getter;
import lotecs.auth.exception.permission.PermissionValidationException;

@Getter
@Builder
public class UpdatePermissionRequest {
    private String permissionId;
    private String tenantId;
    private String description;
    private String updatedBy;

    public void validate() {
        if (permissionId == null || permissionId.trim().isEmpty()) {
            throw PermissionValidationException.idRequired();
        }
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw PermissionValidationException.tenantRequired();
        }
    }
}
