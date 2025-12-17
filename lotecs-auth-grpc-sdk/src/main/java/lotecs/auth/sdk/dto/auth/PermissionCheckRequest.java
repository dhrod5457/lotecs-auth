package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PermissionCheckRequest {
    private String userId;
    private String tenantId;
    private String permissionCode;

    public com.lotecs.auth.grpc.PermissionCheckRequest toProto() {
        return com.lotecs.auth.grpc.PermissionCheckRequest.newBuilder()
                .setUserId(userId != null ? userId : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .setPermissionCode(permissionCode != null ? permissionCode : "")
                .build();
    }
}
