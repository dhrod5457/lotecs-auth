package lotecs.auth.sdk.dto.role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ListRolesRequest {
    private String tenantId;

    public com.lotecs.auth.grpc.ListRolesRequest toProto() {
        return com.lotecs.auth.grpc.ListRolesRequest.newBuilder()
                .setTenantId(tenantId != null ? tenantId : "")
                .build();
    }
}
