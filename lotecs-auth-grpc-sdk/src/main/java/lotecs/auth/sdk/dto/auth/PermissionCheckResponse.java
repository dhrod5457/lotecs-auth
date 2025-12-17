package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PermissionCheckResponse {
    private boolean hasPermission;

    public static PermissionCheckResponse fromProto(com.lotecs.auth.grpc.PermissionCheckResponse proto) {
        return PermissionCheckResponse.builder()
                .hasPermission(proto.getHasPermission())
                .build();
    }
}
