package lotecs.auth.sdk.dto.permission;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatePermissionRequest {
    private String tenantId;
    private String permissionCode;
    private String permissionName;
    private String description;
    private String resourceType;
    private String action;
    private String createdBy;

    public com.lotecs.auth.grpc.CreatePermissionRequest toProto() {
        return com.lotecs.auth.grpc.CreatePermissionRequest.newBuilder()
                .setTenantId(tenantId != null ? tenantId : "")
                .setPermissionCode(permissionCode != null ? permissionCode : "")
                .setPermissionName(permissionName != null ? permissionName : "")
                .setDescription(description != null ? description : "")
                .setResourceType(resourceType != null ? resourceType : "")
                .setAction(action != null ? action : "")
                .setCreatedBy(createdBy != null ? createdBy : "")
                .build();
    }
}
