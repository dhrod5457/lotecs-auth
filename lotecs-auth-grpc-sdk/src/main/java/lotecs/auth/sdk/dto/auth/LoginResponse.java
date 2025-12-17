package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;
import lotecs.framework.common.grpc.core.util.StructConverter;

import java.util.Map;

@Getter
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private int expiresIn;
    private UserInfo user;
    private String ssoType;
    private Map<String, Object> additionalData;

    public static LoginResponse fromProto(com.lotecs.auth.grpc.LoginResponse proto) {
        return LoginResponse.builder()
                .accessToken(proto.getAccessToken())
                .refreshToken(proto.getRefreshToken())
                .expiresIn(proto.getExpiresIn())
                .user(proto.hasUser() ? UserInfo.fromProto(proto.getUser()) : null)
                .ssoType(proto.getSsoType())
                .additionalData(proto.hasAdditionalData() ? StructConverter.toMap(proto.getAdditionalData()) : null)
                .build();
    }
}
