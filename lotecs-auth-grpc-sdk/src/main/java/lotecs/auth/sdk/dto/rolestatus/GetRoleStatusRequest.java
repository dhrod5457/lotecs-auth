package lotecs.auth.sdk.dto.rolestatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetRoleStatusRequest {
    private String statusCode;

    public com.lotecs.auth.grpc.GetRoleStatusRequest toProto() {
        return com.lotecs.auth.grpc.GetRoleStatusRequest.newBuilder()
                .setStatusCode(statusCode != null ? statusCode : "")
                .build();
    }
}
