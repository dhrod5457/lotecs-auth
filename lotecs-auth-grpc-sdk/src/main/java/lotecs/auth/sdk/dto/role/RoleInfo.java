package lotecs.auth.sdk.dto.role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoleInfo {
    private String roleId;
    private String tenantId;
    private String roleName;
    private String displayName;
    private String description;
    private int priority;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;

    public static RoleInfo fromProto(com.lotecs.auth.grpc.RoleInfo proto) {
        return RoleInfo.builder()
                .roleId(proto.getRoleId())
                .tenantId(proto.getTenantId())
                .roleName(proto.getRoleName())
                .displayName(proto.getDisplayName())
                .description(proto.getDescription())
                .priority(proto.getPriority())
                .createdBy(proto.getCreatedBy())
                .createdAt(proto.getCreatedAt())
                .updatedBy(proto.getUpdatedBy())
                .updatedAt(proto.getUpdatedAt())
                .build();
    }
}
