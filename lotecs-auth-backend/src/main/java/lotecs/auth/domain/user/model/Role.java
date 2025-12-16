package lotecs.auth.domain.user.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
public class Role {

    private String roleId;
    private String tenantId;
    private String roleName;
    private String displayName;
    private String description;
    private int priority;

    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    @Builder.Default
    private List<Permission> permissions = new ArrayList<>();

    public void validate() {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("역할 이름은 필수입니다.");
        }
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("테넌트 ID는 필수입니다.");
        }
        if (priority < 0) {
            throw new IllegalArgumentException("우선순위는 0 이상이어야 합니다.");
        }
    }

    public void updateInfo(String displayName, String description, String updatedBy) {
        this.displayName = displayName;
        this.description = description;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }
}
