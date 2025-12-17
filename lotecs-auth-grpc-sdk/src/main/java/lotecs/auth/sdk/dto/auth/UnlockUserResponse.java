package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UnlockUserResponse {
    private boolean success;
    private String message;

    public static UnlockUserResponse fromProto(com.lotecs.auth.grpc.UnlockUserResponse proto) {
        return UnlockUserResponse.builder()
                .success(proto.getSuccess())
                .message(proto.getMessage())
                .build();
    }
}
