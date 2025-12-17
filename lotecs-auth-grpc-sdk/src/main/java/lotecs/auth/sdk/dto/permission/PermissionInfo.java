package lotecs.auth.sdk.dto.permission;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PermissionInfo {
    private String permissionId;
    private String tenantId;
    private String permissionCode;
    private String permissionName;
    private String description;
    private String resourceType;
    private String action;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;

    public static PermissionInfo fromProto(com.lotecs.auth.grpc.PermissionInfo proto) {
        return PermissionInfo.builder()
                .permissionId(proto.getPermissionId())
                .tenantId(proto.getTenantId())
                .permissionCode(proto.getPermissionCode())
                .permissionName(proto.getPermissionName())
                .description(proto.getDescription())
                .resourceType(proto.getResourceType())
                .action(proto.getAction())
                .createdBy(proto.getCreatedBy())
                .createdAt(proto.getCreatedAt())
                .updatedBy(proto.getUpdatedBy())
                .updatedAt(proto.getUpdatedAt())
                .build();
    }
}
