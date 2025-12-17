package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteUserRequest {
    private String userId;
    private String tenantId;

    public com.lotecs.auth.grpc.DeleteUserRequest toProto() {
        return com.lotecs.auth.grpc.DeleteUserRequest.newBuilder()
                .setUserId(userId != null ? userId : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .build();
    }
}
