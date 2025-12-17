package lotecs.auth.sdk.dto.tenant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetTenantBySiteCodeRequest {
    private String siteCode;

    public com.lotecs.auth.grpc.GetTenantBySiteCodeRequest toProto() {
        return com.lotecs.auth.grpc.GetTenantBySiteCodeRequest.newBuilder()
                .setSiteCode(siteCode != null ? siteCode : "")
                .build();
    }
}
