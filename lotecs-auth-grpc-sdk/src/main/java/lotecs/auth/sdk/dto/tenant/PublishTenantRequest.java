package lotecs.auth.sdk.dto.tenant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PublishTenantRequest {
    private String tenantId;
    private String updatedBy;

    public com.lotecs.auth.grpc.PublishTenantRequest toProto() {
        return com.lotecs.auth.grpc.PublishTenantRequest.newBuilder()
                .setTenantId(tenantId != null ? tenantId : "")
                .setUpdatedBy(updatedBy != null ? updatedBy : "")
                .build();
    }
}
