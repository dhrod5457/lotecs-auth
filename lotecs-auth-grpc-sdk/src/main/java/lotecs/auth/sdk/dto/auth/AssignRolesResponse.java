package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AssignRolesResponse {
    private boolean success;
    private String message;
    private int assignedCount;

    public static AssignRolesResponse fromProto(com.lotecs.auth.grpc.AssignRolesResponse proto) {
        return AssignRolesResponse.builder()
                .success(proto.getSuccess())
                .message(proto.getMessage())
                .assignedCount(proto.getAssignedCount())
                .build();
    }
}
