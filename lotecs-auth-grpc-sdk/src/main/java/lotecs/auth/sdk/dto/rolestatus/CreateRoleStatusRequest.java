package lotecs.auth.sdk.dto.rolestatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateRoleStatusRequest {
    private String statusCode;
    private String statusName;
    private String roleCategory;
    private String description;
    private boolean active;
    private int sortOrder;
    private boolean isDefault;
    private String createdBy;

    public com.lotecs.auth.grpc.CreateRoleStatusRequest toProto() {
        return com.lotecs.auth.grpc.CreateRoleStatusRequest.newBuilder()
                .setStatusCode(statusCode != null ? statusCode : "")
                .setStatusName(statusName != null ? statusName : "")
                .setRoleCategory(roleCategory != null ? roleCategory : "")
                .setDescription(description != null ? description : "")
                .setIsActive(active)
                .setSortOrder(sortOrder)
                .setIsDefault(isDefault)
                .setCreatedBy(createdBy != null ? createdBy : "")
                .build();
    }
}
