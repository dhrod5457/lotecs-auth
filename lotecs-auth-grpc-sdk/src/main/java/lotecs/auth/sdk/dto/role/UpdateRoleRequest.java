package lotecs.auth.sdk.dto.role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateRoleRequest {
    private String roleId;
    private String tenantId;
    private String displayName;
    private String description;
    private Integer priority;
    private String updatedBy;

    public com.lotecs.auth.grpc.UpdateRoleRequest toProto() {
        com.lotecs.auth.grpc.UpdateRoleRequest.Builder builder = com.lotecs.auth.grpc.UpdateRoleRequest.newBuilder()
                .setRoleId(roleId != null ? roleId : "")
                .setTenantId(tenantId != null ? tenantId : "")
                .setDisplayName(displayName != null ? displayName : "")
                .setDescription(description != null ? description : "")
                .setUpdatedBy(updatedBy != null ? updatedBy : "");
        if (priority != null) {
            builder.setPriority(priority);
        }
        return builder.build();
    }
}
