package lotecs.auth.sdk.dto.rolestatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteRoleStatusRequest {
    private String statusCode;

    public com.lotecs.auth.grpc.DeleteRoleStatusRequest toProto() {
        return com.lotecs.auth.grpc.DeleteRoleStatusRequest.newBuilder()
                .setStatusCode(statusCode != null ? statusCode : "")
                .build();
    }
}
