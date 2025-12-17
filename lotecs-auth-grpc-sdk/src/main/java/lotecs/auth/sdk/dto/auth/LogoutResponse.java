package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LogoutResponse {
    private boolean success;
    private String message;

    public static LogoutResponse fromProto(com.lotecs.auth.grpc.LogoutResponse proto) {
        return LogoutResponse.builder()
                .success(proto.getSuccess())
                .message(proto.getMessage())
                .build();
    }
}
