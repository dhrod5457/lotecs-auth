package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RevokeRoleRequest {
    private String userId;
    private String tenantId;
    private String roleId;
    private String revokedBy;

    public com.lotecs.auth.grpc.RevokeRoleRequest toProto() {
        return com.lotecs.auth.grpc.RevokeRoleRequest.newBuilder()
                .setUserId(userId != null ? userId : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .setRoleId(roleId != null ? roleId : "")
                .setRevokedBy(revokedBy != null ? revokedBy : "")
                .build();
    }
}
