package lotecs.auth.sdk.dto.organization;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrganizationInfo {
    private long id;
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
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;

    public static OrganizationInfo fromProto(com.lotecs.auth.grpc.OrganizationInfo proto) {
        return OrganizationInfo.builder()
                .id(proto.getId())
                .tenantId(proto.getTenantId())
                .organizationId(proto.getOrganizationId())
                .organizationCode(proto.getOrganizationCode())
                .organizationName(proto.getOrganizationName())
                .organizationType(proto.getOrganizationType())
                .parentOrganizationId(proto.getParentOrganizationId())
                .orgLevel(proto.getOrgLevel())
                .displayOrder(proto.getDisplayOrder())
                .description(proto.getDescription())
                .active(proto.getActive())
                .createdBy(proto.getCreatedBy())
                .createdAt(proto.getCreatedAt())
                .updatedBy(proto.getUpdatedBy())
                .updatedAt(proto.getUpdatedAt())
                .build();
    }
}
