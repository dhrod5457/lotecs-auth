package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AssignRoleResponse {
    private boolean success;
    private String message;

    public static AssignRoleResponse fromProto(com.lotecs.auth.grpc.AssignRoleResponse proto) {
        return AssignRoleResponse.builder()
                .success(proto.getSuccess())
                .message(proto.getMessage())
                .build();
    }
}
