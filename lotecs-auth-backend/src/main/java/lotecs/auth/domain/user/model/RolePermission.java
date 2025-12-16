package lotecs.auth.domain.user.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 역할-권한 매핑 도메인 모델 (ATH_ROLE_PERMISSIONS)
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission {

    private String roleId;
    private String permissionId;
    private String tenantId;
    private LocalDateTime grantedAt;
    private String grantedBy;

    /**
     * 권한 부여 생성
     */
    public static RolePermission grant(String roleId, String permissionId, String tenantId, String grantedBy) {
        return RolePermission.builder()
                .roleId(roleId)
                .permissionId(permissionId)
                .tenantId(tenantId)
                .grantedAt(LocalDateTime.now())
                .grantedBy(grantedBy)
                .build();
    }
}
