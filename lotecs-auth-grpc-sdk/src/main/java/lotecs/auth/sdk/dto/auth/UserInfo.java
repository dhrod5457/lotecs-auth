package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserInfo {
    private String userId;
    private String tenantId;
    private String username;
    private String email;
    private String fullName;
    private String status;
    private List<String> roles;
    private List<String> permissions;
    private String externalUserId;
    private String externalSystem;

    public static UserInfo fromProto(com.lotecs.auth.grpc.UserInfo proto) {
        return UserInfo.builder()
                .userId(proto.getUserId())
                .tenantId(proto.getTenantId())
                .username(proto.getUsername())
                .email(proto.getEmail())
                .fullName(proto.getFullName())
                .status(proto.getStatus())
                .roles(proto.getRolesList())
                .permissions(proto.getPermissionsList())
                .externalUserId(proto.getExternalUserId())
                .externalSystem(proto.getExternalSystem())
                .build();
    }
}
