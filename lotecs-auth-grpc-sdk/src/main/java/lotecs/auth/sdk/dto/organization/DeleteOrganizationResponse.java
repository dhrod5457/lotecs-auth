package lotecs.auth.sdk.dto.organization;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteOrganizationResponse {
    private boolean success;
    private String message;

    public static DeleteOrganizationResponse fromProto(com.lotecs.auth.grpc.DeleteOrganizationResponse proto) {
        return DeleteOrganizationResponse.builder()
                .success(proto.getSuccess())
                .message(proto.getMessage())
                .build();
    }
}
