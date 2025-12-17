package lotecs.auth.sdk.dto.rolestatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoleStatusResponse {
    private RoleStatusInfo roleStatus;
    private String errorMessage;

    public static RoleStatusResponse fromProto(com.lotecs.auth.grpc.RoleStatusResponse proto) {
        return RoleStatusResponse.builder()
                .roleStatus(proto.hasRoleStatus() ? RoleStatusInfo.fromProto(proto.getRoleStatus()) : null)
                .errorMessage(proto.getErrorMessage())
                .build();
    }
}
