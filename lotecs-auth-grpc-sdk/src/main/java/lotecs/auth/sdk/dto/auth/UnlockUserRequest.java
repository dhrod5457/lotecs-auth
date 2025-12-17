package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UnlockUserRequest {
    private String userId;
    private String tenantId;
    private String unlockedBy;

    public com.lotecs.auth.grpc.UnlockUserRequest toProto() {
        return com.lotecs.auth.grpc.UnlockUserRequest.newBuilder()
                .setUserId(userId != null ? userId : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .setUnlockedBy(unlockedBy != null ? unlockedBy : "")
                .build();
    }
}
