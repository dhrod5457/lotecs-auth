package lotecs.auth.sdk.dto.tenant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TenantResponse {
    private TenantInfo tenant;
    private String errorMessage;

    public static TenantResponse fromProto(com.lotecs.auth.grpc.TenantResponse proto) {
        return TenantResponse.builder()
                .tenant(proto.hasTenant() ? TenantInfo.fromProto(proto.getTenant()) : null)
                .errorMessage(proto.getErrorMessage())
                .build();
    }
}
