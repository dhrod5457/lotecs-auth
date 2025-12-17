package lotecs.auth.sdk.dto.userorganization;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SyncUserOrganizationRequest {
    private String tenantId;
    private String userId;
    private String organizationId;
    private String roleId;
    private boolean primary;
    private String position;
    private String startDate;
    private String endDate;
    private boolean active;
    private String syncBy;

    public com.lotecs.auth.grpc.SyncUserOrganizationRequest toProto() {
        return com.lotecs.auth.grpc.SyncUserOrganizationRequest.newBuilder()
                .setTenantId(tenantId != null ? tenantId : "")
                .setUserId(userId != null ? userId : "")
                .setOrganizationId(organizationId != null ? organizationId : "")
                .setRoleId(roleId != null ? roleId : "")
                .setIsPrimary(primary)
                .setPosition(position != null ? position : "")
                .setStartDate(startDate != null ? startDate : "")
                .setEndDate(endDate != null ? endDate : "")
                .setActive(active)
                .setSyncBy(syncBy != null ? syncBy : "")
                .build();
    }
}
