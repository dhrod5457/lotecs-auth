package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
    private int expiresIn;

    public static RefreshTokenResponse fromProto(com.lotecs.auth.grpc.RefreshTokenResponse proto) {
        return RefreshTokenResponse.builder()
                .accessToken(proto.getAccessToken())
                .refreshToken(proto.getRefreshToken())
                .expiresIn(proto.getExpiresIn())
                .build();
    }
}
