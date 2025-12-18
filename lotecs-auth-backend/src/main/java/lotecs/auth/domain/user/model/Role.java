package lotecs.auth.domain.user.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lotecs.auth.exception.role.RoleValidationException;

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
    private LocalDateTime deletedAt;

    @Builder.Default
    private List<Permission> permissions = new ArrayList<>();

    public void validate() {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw RoleValidationException.nameRequired();
        }
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw RoleValidationException.tenantRequired();
        }
        if (priority < 0) {
            throw RoleValidationException.priorityInvalid();
        }
    }

    public void updateInfo(String displayName, String description, String updatedBy) {
        this.displayName = displayName;
        this.description = description;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 새 역할 생성
     */
    public static Role create(String tenantId, String roleName, String displayName, String description, int priority, String createdBy) {
        return Role.builder()
                .roleId(UUID.randomUUID().toString())
                .tenantId(tenantId)
                .roleName(roleName)
                .displayName(displayName)
                .description(description)
                .priority(priority)
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 삭제 여부 확인
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
