package lotecs.auth.sdk.dto.role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetRoleRequest {
    private String roleId;
    private String tenantId;

    public com.lotecs.auth.grpc.GetRoleRequest toProto() {
        return com.lotecs.auth.grpc.GetRoleRequest.newBuilder()
                .setRoleId(roleId != null ? roleId : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .build();
    }
}
