package lotecs.auth.sdk.dto.permission;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetPermissionRequest {
    private String permissionId;
    private String tenantId;

    public com.lotecs.auth.grpc.GetPermissionRequest toProto() {
        return com.lotecs.auth.grpc.GetPermissionRequest.newBuilder()
                .setPermissionId(permissionId != null ? permissionId : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .build();
    }
}
