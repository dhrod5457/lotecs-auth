package lotecs.auth.sdk.dto.userorganization;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ListUserOrganizationsResponse {
    private List<UserOrganizationInfo> userOrganizations;

    public static ListUserOrganizationsResponse fromProto(com.lotecs.auth.grpc.ListUserOrganizationsResponse proto) {
        return ListUserOrganizationsResponse.builder()
                .userOrganizations(proto.getUserOrganizationsList().stream()
                        .map(UserOrganizationInfo::fromProto)
                        .collect(Collectors.toList()))
                .build();
    }
}
