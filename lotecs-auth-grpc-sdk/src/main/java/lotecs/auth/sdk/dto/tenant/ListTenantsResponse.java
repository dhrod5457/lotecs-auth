package lotecs.auth.sdk.dto.tenant;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ListTenantsResponse {
    private List<TenantInfo> tenants;

    public static ListTenantsResponse fromProto(com.lotecs.auth.grpc.ListTenantsResponse proto) {
        return ListTenantsResponse.builder()
                .tenants(proto.getTenantsList().stream()
                        .map(TenantInfo::fromProto)
                        .collect(Collectors.toList()))
                .build();
    }
}
