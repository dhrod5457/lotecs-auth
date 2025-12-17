package lotecs.auth.sdk.dto.permission;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RevokePermissionFromRoleRequest {
    private String roleId;
    private String tenantId;
    private String permissionId;
    private String revokedBy;

    public com.lotecs.auth.grpc.RevokePermissionFromRoleRequest toProto() {
        return com.lotecs.auth.grpc.RevokePermissionFromRoleRequest.newBuilder()
                .setRoleId(roleId != null ? roleId : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .setPermissionId(permissionId != null ? permissionId : "")
                .setRevokedBy(revokedBy != null ? revokedBy : "")
                .build();
    }
}
