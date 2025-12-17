package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangePasswordRequest {
    private String userId;
    private String tenantId;
    private String currentPassword;
    private String newPassword;

    public com.lotecs.auth.grpc.ChangePasswordRequest toProto() {
        return com.lotecs.auth.grpc.ChangePasswordRequest.newBuilder()
                .setUserId(userId != null ? userId : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .setCurrentPassword(currentPassword != null ? currentPassword : "")
                .setNewPassword(newPassword != null ? newPassword : "")
                .build();
    }
}
