package lotecs.auth.sdk.dto.role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteRoleResponse {
    private boolean success;
    private String message;

    public static DeleteRoleResponse fromProto(com.lotecs.auth.grpc.DeleteRoleResponse proto) {
        return DeleteRoleResponse.builder()
                .success(proto.getSuccess())
                .message(proto.getMessage())
                .build();
    }
}
