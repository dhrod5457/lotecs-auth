package lotecs.auth.sdk.dto.organization;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrganizationResponse {
    private OrganizationInfo organization;
    private String errorMessage;

    public static OrganizationResponse fromProto(com.lotecs.auth.grpc.OrganizationResponse proto) {
        return OrganizationResponse.builder()
                .organization(proto.hasOrganization() ? OrganizationInfo.fromProto(proto.getOrganization()) : null)
                .errorMessage(proto.getErrorMessage())
                .build();
    }
}
