package lotecs.auth.sdk.dto.rolestatus;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ListRoleStatusesResponse {
    private List<RoleStatusInfo> roleStatuses;

    public static ListRoleStatusesResponse fromProto(com.lotecs.auth.grpc.ListRoleStatusesResponse proto) {
        return ListRoleStatusesResponse.builder()
                .roleStatuses(proto.getRoleStatusesList().stream()
                        .map(RoleStatusInfo::fromProto)
                        .collect(Collectors.toList()))
                .build();
    }
}
