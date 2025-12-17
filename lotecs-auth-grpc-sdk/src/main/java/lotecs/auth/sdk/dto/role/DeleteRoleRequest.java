package lotecs.auth.sdk.dto.role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteRoleRequest {
    private String roleId;
    private String tenantId;

    public com.lotecs.auth.grpc.DeleteRoleRequest toProto() {
        return com.lotecs.auth.grpc.DeleteRoleRequest.newBuilder()
                .setRoleId(roleId != null ? roleId : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .build();
    }
}
