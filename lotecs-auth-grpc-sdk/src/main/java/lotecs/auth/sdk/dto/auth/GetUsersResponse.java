package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class GetUsersResponse {
    private List<UserInfo> users;
    private long total;

    public static GetUsersResponse fromProto(com.lotecs.auth.grpc.GetUsersResponse proto) {
        return GetUsersResponse.builder()
                .users(proto.getUsersList().stream()
                        .map(UserInfo::fromProto)
                        .collect(Collectors.toList()))
                .total(proto.getTotal())
                .build();
    }
}
