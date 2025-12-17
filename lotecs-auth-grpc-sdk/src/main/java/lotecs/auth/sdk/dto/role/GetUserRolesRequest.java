package lotecs.auth.sdk.dto.role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetUserRolesRequest {
    private String userId;
    private String tenantId;

    public com.lotecs.auth.grpc.GetUserRolesRequest toProto() {
        return com.lotecs.auth.grpc.GetUserRolesRequest.newBuilder()
                .setUserId(userId != null ? userId : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .build();
    }
}
