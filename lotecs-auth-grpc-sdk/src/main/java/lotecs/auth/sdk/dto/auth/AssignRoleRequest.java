package lotecs.auth.sdk.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AssignRoleRequest {
    private String userId;
    private String tenantId;
    private String roleId;
    private String statusCode;
    private String validFrom;
    private String validUntil;
    private String assignedBy;

    public com.lotecs.auth.grpc.AssignRoleRequest toProto() {
        return com.lotecs.auth.grpc.AssignRoleRequest.newBuilder()
                .setUserId(userId != null ? userId : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .setRoleId(roleId != null ? roleId : "")
                .setStatusCode(statusCode != null ? statusCode : "")
                .setValidFrom(validFrom != null ? validFrom : "")
                .setValidUntil(validUntil != null ? validUntil : "")
                .setAssignedBy(assignedBy != null ? assignedBy : "")
                .build();
    }
}
