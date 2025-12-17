package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LogoutRequest {
    private String accessToken;
    private String userId;
    private String tenantId;

    public com.lotecs.auth.grpc.LogoutRequest toProto() {
        return com.lotecs.auth.grpc.LogoutRequest.newBuilder()
                .setAccessToken(accessToken != null ? accessToken : "")
                .setUserId(userId != null ? userId : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .build();
    }
}
