package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CreateUserRequest {
    private String tenantId;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private List<String> roles;

    public com.lotecs.auth.grpc.CreateUserRequest toProto() {
        com.lotecs.auth.grpc.CreateUserRequest.Builder builder = com.lotecs.auth.grpc.CreateUserRequest.newBuilder()
                .setTenantId(tenantId != null ? tenantId : "")
                .setUsername(username != null ? username : "")
                .setPassword(password != null ? password : "")
                .setEmail(email != null ? email : "")
                .setFullName(fullName != null ? fullName : "");
        if (roles != null) {
            builder.addAllRoles(roles);
        }
        return builder.build();
    }
}
