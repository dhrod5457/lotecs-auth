package lotecs.auth.sdk.dto.userorganization;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteUserOrganizationRequest {
    private long id;

    public com.lotecs.auth.grpc.DeleteUserOrganizationRequest toProto() {
        return com.lotecs.auth.grpc.DeleteUserOrganizationRequest.newBuilder()
                .setId(id)
                .build();
    }
}
