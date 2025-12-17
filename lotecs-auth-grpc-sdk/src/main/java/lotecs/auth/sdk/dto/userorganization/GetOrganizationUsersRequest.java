package lotecs.auth.sdk.dto.userorganization;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetOrganizationUsersRequest {
    private String organizationId;

    public com.lotecs.auth.grpc.GetOrganizationUsersRequest toProto() {
        return com.lotecs.auth.grpc.GetOrganizationUsersRequest.newBuilder()
                .setOrganizationId(organizationId != null ? organizationId : "")
                .build();
    }
}
