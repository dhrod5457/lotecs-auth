package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LockUserRequest {
    private String userId;
    private String tenantId;
    private String reason;
    private String lockedBy;

    public com.lotecs.auth.grpc.LockUserRequest toProto() {
        return com.lotecs.auth.grpc.LockUserRequest.newBuilder()
                .setUserId(userId != null ? userId : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .setReason(reason != null ? reason : "")
                .setLockedBy(lockedBy != null ? lockedBy : "")
                .build();
    }
}
