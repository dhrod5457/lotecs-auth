package lotecs.auth.sdk.dto.organization;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ListChildOrganizationsRequest {
    private String parentOrganizationId;

    public com.lotecs.auth.grpc.ListChildOrganizationsRequest toProto() {
        return com.lotecs.auth.grpc.ListChildOrganizationsRequest.newBuilder()
                .setParentOrganizationId(parentOrganizationId != null ? parentOrganizationId : "")
                .build();
    }
}
