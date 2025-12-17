package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LockUserResponse {
    private boolean success;
    private String message;

    public static LockUserResponse fromProto(com.lotecs.auth.grpc.LockUserResponse proto) {
        return LockUserResponse.builder()
                .success(proto.getSuccess())
                .message(proto.getMessage())
                .build();
    }
}
