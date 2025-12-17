package lotecs.auth.sdk.dto.permission;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ListPermissionsResponse {
    private List<PermissionInfo> permissions;

    public static ListPermissionsResponse fromProto(com.lotecs.auth.grpc.ListPermissionsResponse proto) {
        return ListPermissionsResponse.builder()
                .permissions(proto.getPermissionsList().stream()
                        .map(PermissionInfo::fromProto)
                        .collect(Collectors.toList()))
                .build();
    }
}
