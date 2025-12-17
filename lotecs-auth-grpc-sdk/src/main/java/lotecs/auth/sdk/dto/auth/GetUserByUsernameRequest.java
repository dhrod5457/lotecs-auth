package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetUserByUsernameRequest {
    private String username;
    private String tenantId;

    public com.lotecs.auth.grpc.GetUserByUsernameRequest toProto() {
        return com.lotecs.auth.grpc.GetUserByUsernameRequest.newBuilder()
                .setUsername(username != null ? username : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .build();
    }
}
