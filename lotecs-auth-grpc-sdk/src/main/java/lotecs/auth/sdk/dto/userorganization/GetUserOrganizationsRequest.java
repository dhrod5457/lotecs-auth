package lotecs.auth.sdk.dto.userorganization;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetUserOrganizationsRequest {
    private String userId;

    public com.lotecs.auth.grpc.GetUserOrganizationsRequest toProto() {
        return com.lotecs.auth.grpc.GetUserOrganizationsRequest.newBuilder()
                .setUserId(userId != null ? userId : "")
                .build();
    }
}
