package lotecs.auth.sdk.dto.tenant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UnpublishTenantRequest {
    private String tenantId;
    private String reason;
    private String updatedBy;

    public com.lotecs.auth.grpc.UnpublishTenantRequest toProto() {
        return com.lotecs.auth.grpc.UnpublishTenantRequest.newBuilder()
                .setTenantId(tenantId != null ? tenantId : "")
                .setReason(reason != null ? reason : "")
                .setUpdatedBy(updatedBy != null ? updatedBy : "")
                .build();
    }
}
