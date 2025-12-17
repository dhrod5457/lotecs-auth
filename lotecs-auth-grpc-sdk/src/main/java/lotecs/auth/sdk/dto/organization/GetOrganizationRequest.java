package lotecs.auth.sdk.dto.organization;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetOrganizationRequest {
    private String organizationId;

    public com.lotecs.auth.grpc.GetOrganizationRequest toProto() {
        return com.lotecs.auth.grpc.GetOrganizationRequest.newBuilder()
                .setOrganizationId(organizationId != null ? organizationId : "")
                .build();
    }
}
