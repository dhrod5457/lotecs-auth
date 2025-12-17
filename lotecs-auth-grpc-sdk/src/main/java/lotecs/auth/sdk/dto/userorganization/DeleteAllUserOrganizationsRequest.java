package lotecs.auth.sdk.dto.userorganization;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteAllUserOrganizationsRequest {
    private String userId;

    public com.lotecs.auth.grpc.DeleteAllUserOrganizationsRequest toProto() {
        return com.lotecs.auth.grpc.DeleteAllUserOrganizationsRequest.newBuilder()
                .setUserId(userId != null ? userId : "")
                .build();
    }
}
