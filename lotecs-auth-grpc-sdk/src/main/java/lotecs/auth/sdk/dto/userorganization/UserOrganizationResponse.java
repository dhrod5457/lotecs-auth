package lotecs.auth.sdk.dto.userorganization;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserOrganizationResponse {
    private UserOrganizationInfo userOrganization;
    private String errorMessage;

    public static UserOrganizationResponse fromProto(com.lotecs.auth.grpc.UserOrganizationResponse proto) {
        return UserOrganizationResponse.builder()
                .userOrganization(proto.hasUserOrganization() ? UserOrganizationInfo.fromProto(proto.getUserOrganization()) : null)
                .errorMessage(proto.getErrorMessage())
                .build();
    }
}
