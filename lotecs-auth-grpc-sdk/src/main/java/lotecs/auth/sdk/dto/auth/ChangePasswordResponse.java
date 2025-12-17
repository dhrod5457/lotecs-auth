package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangePasswordResponse {
    private boolean success;
    private String message;

    public static ChangePasswordResponse fromProto(com.lotecs.auth.grpc.ChangePasswordResponse proto) {
        return ChangePasswordResponse.builder()
                .success(proto.getSuccess())
                .message(proto.getMessage())
                .build();
    }
}
