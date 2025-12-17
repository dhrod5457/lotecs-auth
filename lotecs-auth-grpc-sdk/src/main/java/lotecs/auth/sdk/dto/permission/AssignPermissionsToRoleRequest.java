package lotecs.auth.sdk.dto.permission;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AssignPermissionsToRoleRequest {
    private String roleId;
    private String tenantId;
    private List<String> permissionIds;
    private String assignedBy;

    public com.lotecs.auth.grpc.AssignPermissionsToRoleRequest toProto() {
        com.lotecs.auth.grpc.AssignPermissionsToRoleRequest.Builder builder =
            com.lotecs.auth.grpc.AssignPermissionsToRoleRequest.newBuilder()
                .setRoleId(roleId != null ? roleId : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .setAssignedBy(assignedBy != null ? assignedBy : "");
        if (permissionIds != null) {
            builder.addAllPermissionIds(permissionIds);
        }
        return builder.build();
    }
}
