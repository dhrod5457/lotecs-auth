package lotecs.auth.sdk.dto.tenant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteTenantResponse {
    private boolean success;
    private String message;

    public static DeleteTenantResponse fromProto(com.lotecs.auth.grpc.DeleteTenantResponse proto) {
        return DeleteTenantResponse.builder()
                .success(proto.getSuccess())
                .message(proto.getMessage())
                .build();
    }
}
