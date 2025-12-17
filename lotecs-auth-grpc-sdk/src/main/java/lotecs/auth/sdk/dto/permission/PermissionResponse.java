package lotecs.auth.sdk.dto.permission;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PermissionResponse {
    private PermissionInfo permission;
    private String errorMessage;

    public static PermissionResponse fromProto(com.lotecs.auth.grpc.PermissionResponse proto) {
        return PermissionResponse.builder()
                .permission(proto.hasPermission() ? PermissionInfo.fromProto(proto.getPermission()) : null)
                .errorMessage(proto.getErrorMessage())
                .build();
    }
}
