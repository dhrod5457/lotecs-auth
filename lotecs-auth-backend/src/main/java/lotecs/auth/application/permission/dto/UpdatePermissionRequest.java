package lotecs.auth.application.permission.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdatePermissionRequest {
    private String permissionId;
    private String tenantId;
    private String description;
    private String updatedBy;

    public void validate() {
        if (permissionId == null || permissionId.trim().isEmpty()) {
            throw new IllegalArgumentException("권한 ID는 필수입니다.");
        }
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("테넌트 ID는 필수입니다.");
        }
    }
}
