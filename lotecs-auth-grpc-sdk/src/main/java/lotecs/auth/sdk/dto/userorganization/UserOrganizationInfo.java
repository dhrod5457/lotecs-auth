package lotecs.auth.sdk.dto.userorganization;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserOrganizationInfo {
    private long id;
    private String tenantId;
    private String userId;
    private String organizationId;
    private String roleId;
    private boolean primary;
    private String position;
    private String startDate;
    private String endDate;
    private boolean active;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;

    public static UserOrganizationInfo fromProto(com.lotecs.auth.grpc.UserOrganizationInfo proto) {
        return UserOrganizationInfo.builder()
                .id(proto.getId())
                .tenantId(proto.getTenantId())
                .userId(proto.getUserId())
                .organizationId(proto.getOrganizationId())
                .roleId(proto.getRoleId())
                .primary(proto.getIsPrimary())
                .position(proto.getPosition())
                .startDate(proto.getStartDate())
                .endDate(proto.getEndDate())
                .active(proto.getActive())
                .createdBy(proto.getCreatedBy())
                .createdAt(proto.getCreatedAt())
                .updatedBy(proto.getUpdatedBy())
                .updatedAt(proto.getUpdatedAt())
                .build();
    }
}
