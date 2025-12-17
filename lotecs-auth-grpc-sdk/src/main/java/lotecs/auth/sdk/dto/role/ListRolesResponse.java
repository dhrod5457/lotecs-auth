package lotecs.auth.sdk.dto.role;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ListRolesResponse {
    private List<RoleInfo> roles;

    public static ListRolesResponse fromProto(com.lotecs.auth.grpc.ListRolesResponse proto) {
        return ListRolesResponse.builder()
                .roles(proto.getRolesList().stream()
                        .map(RoleInfo::fromProto)
                        .collect(Collectors.toList()))
                .build();
    }
}
