package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetUsersRequest {
    private String tenantId;
    private int page;
    private int size;

    public com.lotecs.auth.grpc.GetUsersRequest toProto() {
        return com.lotecs.auth.grpc.GetUsersRequest.newBuilder()
                .setTenantId(tenantId != null ? tenantId : "")
                .setPage(page)
                .setSize(size)
                .build();
    }
}
