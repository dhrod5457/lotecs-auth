package lotecs.auth.sdk.dto.organization;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteOrganizationRequest {
    private String organizationId;

    public com.lotecs.auth.grpc.DeleteOrganizationRequest toProto() {
        return com.lotecs.auth.grpc.DeleteOrganizationRequest.newBuilder()
                .setOrganizationId(organizationId != null ? organizationId : "")
                .build();
    }
}
