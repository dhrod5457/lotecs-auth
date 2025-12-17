package lotecs.auth.sdk.dto.permission;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetRolePermissionsRequest {
    private String roleId;
    private String tenantId;

    public com.lotecs.auth.grpc.GetRolePermissionsRequest toProto() {
        return com.lotecs.auth.grpc.GetRolePermissionsRequest.newBuilder()
                .setRoleId(roleId != null ? roleId : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .build();
    }
}
