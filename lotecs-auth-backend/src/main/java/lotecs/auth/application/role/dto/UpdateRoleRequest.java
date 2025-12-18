package lotecs.auth.application.role.dto;

import lombok.Builder;
import lombok.Getter;
import lotecs.auth.exception.role.RoleValidationException;

@Getter
@Builder
public class UpdateRoleRequest {
    private String roleId;
    private String tenantId;
    private String displayName;
    private String description;
    private Integer priority;
    private String updatedBy;

    public void validate() {
        if (roleId == null || roleId.trim().isEmpty()) {
            throw RoleValidationException.idRequired();
        }
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw RoleValidationException.tenantRequired();
        }
    }
}
