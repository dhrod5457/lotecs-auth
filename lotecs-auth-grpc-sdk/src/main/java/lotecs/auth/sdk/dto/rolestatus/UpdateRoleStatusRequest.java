package lotecs.auth.sdk.dto.rolestatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateRoleStatusRequest {
    private String statusCode;
    private String statusName;
    private String roleCategory;
    private String description;
    private boolean active;
    private int sortOrder;
    private boolean isDefault;
    private String updatedBy;

    public com.lotecs.auth.grpc.UpdateRoleStatusRequest toProto() {
        return com.lotecs.auth.grpc.UpdateRoleStatusRequest.newBuilder()
                .setStatusCode(statusCode != null ? statusCode : "")
                .setStatusName(statusName != null ? statusName : "")
                .setRoleCategory(roleCategory != null ? roleCategory : "")
                .setDescription(description != null ? description : "")
                .setIsActive(active)
                .setSortOrder(sortOrder)
                .setIsDefault(isDefault)
                .setUpdatedBy(updatedBy != null ? updatedBy : "")
                .build();
    }
}
