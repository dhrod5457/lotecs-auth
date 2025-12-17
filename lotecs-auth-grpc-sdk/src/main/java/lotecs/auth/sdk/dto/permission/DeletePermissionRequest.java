package lotecs.auth.sdk.dto.permission;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeletePermissionRequest {
    private String permissionId;
    private String tenantId;

    public com.lotecs.auth.grpc.DeletePermissionRequest toProto() {
        return com.lotecs.auth.grpc.DeletePermissionRequest.newBuilder()
                .setPermissionId(permissionId != null ? permissionId : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .build();
    }
}
