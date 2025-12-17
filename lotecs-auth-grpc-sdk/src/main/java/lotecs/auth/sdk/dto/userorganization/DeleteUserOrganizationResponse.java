package lotecs.auth.sdk.dto.userorganization;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteUserOrganizationResponse {
    private boolean success;
    private String message;

    public static DeleteUserOrganizationResponse fromProto(com.lotecs.auth.grpc.DeleteUserOrganizationResponse proto) {
        return DeleteUserOrganizationResponse.builder()
                .success(proto.getSuccess())
                .message(proto.getMessage())
                .build();
    }
}
