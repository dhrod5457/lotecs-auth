package lotecs.auth.domain.user.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    private String permissionId;
    private String tenantId;
    private String permissionName;
    private String resource;
    private String action;
    private String description;

    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    public String toAuthority() {
        return resource + ":" + action;
    }

    public void validate() {
        if (permissionName == null || permissionName.trim().isEmpty()) {
            throw new IllegalArgumentException("권한 이름은 필수입니다.");
        }
        if (resource == null || resource.trim().isEmpty()) {
            throw new IllegalArgumentException("리소스는 필수입니다.");
        }
        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException("액션은 필수입니다.");
        }
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("테넌트 ID는 필수입니다.");
        }
    }

    public void updateInfo(String description, String updatedBy) {
        this.description = description;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }
}
