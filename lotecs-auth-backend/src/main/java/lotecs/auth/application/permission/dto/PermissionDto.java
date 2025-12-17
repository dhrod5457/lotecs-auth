package lotecs.auth.application.permission.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PermissionDto {
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
}
