package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetUserByIdRequest {
    private String userId;
    private String tenantId;

    public com.lotecs.auth.grpc.GetUserByIdRequest toProto() {
        return com.lotecs.auth.grpc.GetUserByIdRequest.newBuilder()
                .setUserId(userId != null ? userId : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .build();
    }
}
