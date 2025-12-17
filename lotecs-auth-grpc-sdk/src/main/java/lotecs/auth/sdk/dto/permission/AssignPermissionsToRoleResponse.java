package lotecs.auth.sdk.dto.permission;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AssignPermissionsToRoleResponse {
    private boolean success;
    private String message;
    private int assignedCount;

    public static AssignPermissionsToRoleResponse fromProto(com.lotecs.auth.grpc.AssignPermissionsToRoleResponse proto) {
        return AssignPermissionsToRoleResponse.builder()
                .success(proto.getSuccess())
                .message(proto.getMessage())
                .assignedCount(proto.getAssignedCount())
                .build();
    }
}
