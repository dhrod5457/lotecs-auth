package lotecs.auth.sdk.dto.tenant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteTenantRequest {
    private String tenantId;

    public com.lotecs.auth.grpc.DeleteTenantRequest toProto() {
        return com.lotecs.auth.grpc.DeleteTenantRequest.newBuilder()
                .setTenantId(tenantId != null ? tenantId : "")
                .build();
    }
}
