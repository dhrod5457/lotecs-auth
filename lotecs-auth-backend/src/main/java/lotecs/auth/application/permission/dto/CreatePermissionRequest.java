package lotecs.auth.application.permission.dto;

import lombok.Builder;
import lombok.Getter;

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
            throw new IllegalArgumentException("테넌트 ID는 필수입니다.");
        }
        if (permissionName == null || permissionName.trim().isEmpty()) {
            throw new IllegalArgumentException("권한명은 필수입니다.");
        }
        if (resource == null || resource.trim().isEmpty()) {
            throw new IllegalArgumentException("리소스는 필수입니다.");
        }
        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException("액션은 필수입니다.");
        }
    }
}
