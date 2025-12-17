package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateUserRequest {
    private String userId;
    private String tenantId;
    private String email;
    private String fullName;
    private String status;

    public com.lotecs.auth.grpc.UpdateUserRequest toProto() {
        return com.lotecs.auth.grpc.UpdateUserRequest.newBuilder()
                .setUserId(userId != null ? userId : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .setEmail(email != null ? email : "")
                .setFullName(fullName != null ? fullName : "")
                .setStatus(status != null ? status : "")
                .build();
    }
}
