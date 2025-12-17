package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidateTokenRequest {
    private String accessToken;

    public com.lotecs.auth.grpc.ValidateTokenRequest toProto() {
        return com.lotecs.auth.grpc.ValidateTokenRequest.newBuilder()
                .setAccessToken(accessToken != null ? accessToken : "")
                .build();
    }
}
