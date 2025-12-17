package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginRequest {
    private String username;
    private String password;
    private String tenantId;
    private String ipAddress;

    public com.lotecs.auth.grpc.LoginRequest toProto() {
        return com.lotecs.auth.grpc.LoginRequest.newBuilder()
                .setUsername(username != null ? username : "")
                .setPassword(password != null ? password : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .setIpAddress(ipAddress != null ? ipAddress : "")
                .build();
    }
}
