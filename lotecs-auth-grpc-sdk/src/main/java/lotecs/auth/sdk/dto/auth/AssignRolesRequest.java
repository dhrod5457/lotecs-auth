package lotecs.auth.sdk.dto.auth;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AssignRolesRequest {
    private String userId;
    private String tenantId;
    private List<String> roleIds;
    private String statusCode;
    private String validFrom;
    private String validUntil;
    private String assignedBy;

    public com.lotecs.auth.grpc.AssignRolesRequest toProto() {
        com.lotecs.auth.grpc.AssignRolesRequest.Builder builder = com.lotecs.auth.grpc.AssignRolesRequest.newBuilder()
                .setUserId(userId != null ? userId : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .setStatusCode(statusCode != null ? statusCode : "")
                .setValidFrom(validFrom != null ? validFrom : "")
                .setValidUntil(validUntil != null ? validUntil : "")
                .setAssignedBy(assignedBy != null ? assignedBy : "");

        if (roleIds != null) {
            builder.addAllRoleIds(roleIds);
        }

        return builder.build();
    }
}
