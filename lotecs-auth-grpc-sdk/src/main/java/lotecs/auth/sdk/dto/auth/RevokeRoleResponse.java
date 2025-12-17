package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RevokeRoleResponse {
    private boolean success;
    private String message;

    public static RevokeRoleResponse fromProto(com.lotecs.auth.grpc.RevokeRoleResponse proto) {
        return RevokeRoleResponse.builder()
                .success(proto.getSuccess())
                .message(proto.getMessage())
                .build();
    }
}
