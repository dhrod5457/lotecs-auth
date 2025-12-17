package lotecs.auth.sdk.dto.tenant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetTenantRequest {
    private String tenantId;

    public com.lotecs.auth.grpc.GetTenantRequest toProto() {
        return com.lotecs.auth.grpc.GetTenantRequest.newBuilder()
                .setTenantId(tenantId != null ? tenantId : "")
                .build();
    }
}
