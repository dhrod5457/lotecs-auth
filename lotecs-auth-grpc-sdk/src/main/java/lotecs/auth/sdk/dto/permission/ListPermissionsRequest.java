package lotecs.auth.sdk.dto.permission;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ListPermissionsRequest {
    private String tenantId;

    public com.lotecs.auth.grpc.ListPermissionsRequest toProto() {
        return com.lotecs.auth.grpc.ListPermissionsRequest.newBuilder()
                .setTenantId(tenantId != null ? tenantId : "")
                .build();
    }
}
