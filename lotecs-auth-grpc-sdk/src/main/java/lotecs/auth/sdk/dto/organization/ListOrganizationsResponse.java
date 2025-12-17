package lotecs.auth.sdk.dto.organization;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ListOrganizationsResponse {
    private List<OrganizationInfo> organizations;

    public static ListOrganizationsResponse fromProto(com.lotecs.auth.grpc.ListOrganizationsResponse proto) {
        return ListOrganizationsResponse.builder()
                .organizations(proto.getOrganizationsList().stream()
                        .map(OrganizationInfo::fromProto)
                        .collect(Collectors.toList()))
                .build();
    }
}
