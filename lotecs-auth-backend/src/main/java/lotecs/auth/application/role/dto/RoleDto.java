package lotecs.auth.application.role.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RoleDto {
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
}
