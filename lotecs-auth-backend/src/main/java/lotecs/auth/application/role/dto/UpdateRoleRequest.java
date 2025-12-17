package lotecs.auth.application.role.dto;

import lombok.Builder;
import lombok.Getter;

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
            throw new IllegalArgumentException("역할 ID는 필수입니다.");
        }
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("테넌트 ID는 필수입니다.");
        }
    }
}
