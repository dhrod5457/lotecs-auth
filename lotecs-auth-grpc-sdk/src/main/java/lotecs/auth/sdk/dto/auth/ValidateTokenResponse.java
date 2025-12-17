package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidateTokenResponse {
    private boolean valid;
    private UserInfo user;
    private String errorMessage;

    public static ValidateTokenResponse fromProto(com.lotecs.auth.grpc.ValidateTokenResponse proto) {
        return ValidateTokenResponse.builder()
                .valid(proto.getValid())
                .user(proto.hasUser() ? UserInfo.fromProto(proto.getUser()) : null)
                .errorMessage(proto.getErrorMessage())
                .build();
    }
}
