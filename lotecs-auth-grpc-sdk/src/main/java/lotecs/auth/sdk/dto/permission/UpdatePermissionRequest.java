package lotecs.auth.sdk.dto.permission;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdatePermissionRequest {
    private String permissionId;
    private String tenantId;
    private String permissionName;
    private String description;
    private String resourceType;
    private String action;
    private String updatedBy;

    public com.lotecs.auth.grpc.UpdatePermissionRequest toProto() {
        return com.lotecs.auth.grpc.UpdatePermissionRequest.newBuilder()
                .setPermissionId(permissionId != null ? permissionId : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .setPermissionName(permissionName != null ? permissionName : "")
                .setDescription(description != null ? description : "")
                .setResourceType(resourceType != null ? resourceType : "")
                .setAction(action != null ? action : "")
                .setUpdatedBy(updatedBy != null ? updatedBy : "")
                .build();
    }
}
