package lotecs.auth.sdk.dto.rolestatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoleStatusInfo {
    private long id;
    private String statusCode;
    private String statusName;
    private String roleCategory;
    private String description;
    private boolean active;
    private int sortOrder;
    private boolean isDefault;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;

    public static RoleStatusInfo fromProto(com.lotecs.auth.grpc.RoleStatusInfo proto) {
        return RoleStatusInfo.builder()
                .id(proto.getId())
                .statusCode(proto.getStatusCode())
                .statusName(proto.getStatusName())
                .roleCategory(proto.getRoleCategory())
                .description(proto.getDescription())
                .active(proto.getIsActive())
                .sortOrder(proto.getSortOrder())
                .isDefault(proto.getIsDefault())
                .createdBy(proto.getCreatedBy())
                .createdAt(proto.getCreatedAt())
                .updatedBy(proto.getUpdatedBy())
                .updatedAt(proto.getUpdatedAt())
                .build();
    }
}
