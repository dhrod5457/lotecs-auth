package lotecs.auth.sdk.dto.role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetRoleByNameRequest {
    private String roleName;
    private String tenantId;

    public com.lotecs.auth.grpc.GetRoleByNameRequest toProto() {
        return com.lotecs.auth.grpc.GetRoleByNameRequest.newBuilder()
                .setRoleName(roleName != null ? roleName : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .build();
    }
}
