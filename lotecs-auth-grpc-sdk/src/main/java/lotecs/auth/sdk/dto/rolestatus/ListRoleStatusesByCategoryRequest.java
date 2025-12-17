package lotecs.auth.sdk.dto.rolestatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ListRoleStatusesByCategoryRequest {
    private String roleCategory;

    public com.lotecs.auth.grpc.ListRoleStatusesByCategoryRequest toProto() {
        return com.lotecs.auth.grpc.ListRoleStatusesByCategoryRequest.newBuilder()
                .setRoleCategory(roleCategory != null ? roleCategory : "")
                .build();
    }
}
