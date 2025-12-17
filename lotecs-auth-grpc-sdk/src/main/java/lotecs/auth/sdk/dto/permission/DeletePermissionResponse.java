package lotecs.auth.sdk.dto.permission;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeletePermissionResponse {
    private boolean success;
    private String message;

    public static DeletePermissionResponse fromProto(com.lotecs.auth.grpc.DeletePermissionResponse proto) {
        return DeletePermissionResponse.builder()
                .success(proto.getSuccess())
                .message(proto.getMessage())
                .build();
    }
}
