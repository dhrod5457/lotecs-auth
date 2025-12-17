package lotecs.auth.sdk.dto.rolestatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteRoleStatusResponse {
    private boolean success;
    private String message;

    public static DeleteRoleStatusResponse fromProto(com.lotecs.auth.grpc.DeleteRoleStatusResponse proto) {
        return DeleteRoleStatusResponse.builder()
                .success(proto.getSuccess())
                .message(proto.getMessage())
                .build();
    }
}
