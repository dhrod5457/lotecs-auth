package lotecs.auth.sdk.dto.permission;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RevokePermissionFromRoleResponse {
    private boolean success;
    private String message;

    public static RevokePermissionFromRoleResponse fromProto(com.lotecs.auth.grpc.RevokePermissionFromRoleResponse proto) {
        return RevokePermissionFromRoleResponse.builder()
                .success(proto.getSuccess())
                .message(proto.getMessage())
                .build();
    }
}
