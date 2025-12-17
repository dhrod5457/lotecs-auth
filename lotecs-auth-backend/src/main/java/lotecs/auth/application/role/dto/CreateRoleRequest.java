package lotecs.auth.application.role.dto;

import lombok.Builder;
import lombok.Getter;

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
            throw new IllegalArgumentException("테넌트 ID는 필수입니다.");
        }
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("역할명은 필수입니다.");
        }
    }
}
