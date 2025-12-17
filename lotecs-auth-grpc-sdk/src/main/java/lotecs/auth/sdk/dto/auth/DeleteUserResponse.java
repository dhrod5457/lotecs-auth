package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteUserResponse {
    private boolean success;
    private String message;

    public static DeleteUserResponse fromProto(com.lotecs.auth.grpc.DeleteUserResponse proto) {
        return DeleteUserResponse.builder()
                .success(proto.getSuccess())
                .message(proto.getMessage())
                .build();
    }
}
