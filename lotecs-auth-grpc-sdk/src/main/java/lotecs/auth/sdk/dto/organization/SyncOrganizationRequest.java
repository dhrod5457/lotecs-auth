package lotecs.auth.sdk.dto.organization;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SyncOrganizationRequest {
    private String tenantId;
    private String organizationId;
    private String organizationCode;
    private String organizationName;
    private String organizationType;
    private String parentOrganizationId;
    private int orgLevel;
    private int displayOrder;
    private String description;
    private boolean active;
    private String syncBy;

    public com.lotecs.auth.grpc.SyncOrganizationRequest toProto() {
        return com.lotecs.auth.grpc.SyncOrganizationRequest.newBuilder()
                .setTenantId(tenantId != null ? tenantId : "")
                .setOrganizationId(organizationId != null ? organizationId : "")
                .setOrganizationCode(organizationCode != null ? organizationCode : "")
                .setOrganizationName(organizationName != null ? organizationName : "")
                .setOrganizationType(organizationType != null ? organizationType : "")
                .setParentOrganizationId(parentOrganizationId != null ? parentOrganizationId : "")
                .setOrgLevel(orgLevel)
                .setDisplayOrder(displayOrder)
                .setDescription(description != null ? description : "")
                .setActive(active)
                .setSyncBy(syncBy != null ? syncBy : "")
                .build();
    }
}
