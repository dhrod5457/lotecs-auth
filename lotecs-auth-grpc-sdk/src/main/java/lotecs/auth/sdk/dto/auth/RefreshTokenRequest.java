package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshTokenRequest {
    private String refreshToken;

    public com.lotecs.auth.grpc.RefreshTokenRequest toProto() {
        return com.lotecs.auth.grpc.RefreshTokenRequest.newBuilder()
                .setRefreshToken(refreshToken != null ? refreshToken : "")
                .build();
    }
}
