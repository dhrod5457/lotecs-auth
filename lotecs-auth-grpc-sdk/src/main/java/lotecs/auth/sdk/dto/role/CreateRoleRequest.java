package lotecs.auth.sdk.dto.role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateRoleRequest {
    private String tenantId;
    private String roleName;
    private String displayName;
    private String description;
    private Integer priority;
    private String createdBy;

    public com.lotecs.auth.grpc.CreateRoleRequest toProto() {
        com.lotecs.auth.grpc.CreateRoleRequest.Builder builder = com.lotecs.auth.grpc.CreateRoleRequest.newBuilder()
                .setTenantId(tenantId != null ? tenantId : "")
                .setRoleName(roleName != null ? roleName : "")
                .setDisplayName(displayName != null ? displayName : "")
                .setDescription(description != null ? description : "")
                .setCreatedBy(createdBy != null ? createdBy : "");
        if (priority != null) {
            builder.setPriority(priority);
        }
        return builder.build();
    }
}
