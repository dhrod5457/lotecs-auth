package lotecs.auth.application.role.dto;

import lombok.Builder;
import lombok.Getter;
import lotecs.auth.exception.role.RoleValidationException;

@Getter
@Builder
public class CreateRoleRequest {
    private String tenantId;
    private String roleName;
    private String displayName;
    private String description;
    private Integer priority;
    private String createdBy;

    public void validate() {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw RoleValidationException.tenantRequired();
        }
        if (roleName == null || roleName.trim().isEmpty()) {
            throw RoleValidationException.nameRequired();
        }
    }
}
