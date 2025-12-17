package lotecs.auth.sdk.dto.organization;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ListOrganizationsRequest {
    private String tenantId;

    public com.lotecs.auth.grpc.ListOrganizationsRequest toProto() {
        return com.lotecs.auth.grpc.ListOrganizationsRequest.newBuilder()
                .setTenantId(tenantId != null ? tenantId : "")
                .build();
    }
}
