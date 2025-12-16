package lotecs.auth.domain.user.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    private String permissionId;
    private String tenantId;
    private String permissionName;
    private String resource;
    private String action;
    private String description;

    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    /**
     * 권한을 Authority 문자열로 변환
     */
    public String toAuthority() {
        return resource + ":" + action;
    }

    public void validate() {
        if (permissionName == null || permissionName.trim().isEmpty()) {
            throw new IllegalArgumentException("권한 이름은 필수입니다.");
        }
        if (resource == null || resource.trim().isEmpty()) {
            throw new IllegalArgumentException("리소스는 필수입니다.");
        }
        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException("액션은 필수입니다.");
        }
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("테넌트 ID는 필수입니다.");
        }
    }

    public void updateInfo(String description, String updatedBy) {
        this.description = description;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 새 권한 생성
     */
    public static Permission create(String tenantId, String permissionName, String resource, String action, String description, String createdBy) {
        return Permission.builder()
                .permissionId(UUID.randomUUID().toString())
                .tenantId(tenantId)
                .permissionName(permissionName)
                .resource(resource)
                .action(action)
                .description(description)
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 삭제 여부 확인
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
