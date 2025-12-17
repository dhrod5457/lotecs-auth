package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private UserInfo user;
    private String errorMessage;

    public static UserResponse fromProto(com.lotecs.auth.grpc.UserResponse proto) {
        return UserResponse.builder()
                .user(proto.hasUser() ? UserInfo.fromProto(proto.getUser()) : null)
                .errorMessage(proto.getErrorMessage())
                .build();
    }
}
