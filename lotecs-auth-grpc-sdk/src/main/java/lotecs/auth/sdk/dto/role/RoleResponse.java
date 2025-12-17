package lotecs.auth.sdk.dto.role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoleResponse {
    private RoleInfo role;
    private String errorMessage;

    public static RoleResponse fromProto(com.lotecs.auth.grpc.RoleResponse proto) {
        return RoleResponse.builder()
                .role(proto.hasRole() ? RoleInfo.fromProto(proto.getRole()) : null)
                .errorMessage(proto.getErrorMessage())
                .build();
    }
}
