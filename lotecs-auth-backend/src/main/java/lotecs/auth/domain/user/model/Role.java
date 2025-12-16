package lotecs.auth.domain.user.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    private Long roleId;

    @NotBlank
    private String tenantId;

    @NotBlank
    private String roleName;

    private String displayName;

    private String description;

    private Integer priority;

    private String createdBy;

    private LocalDateTime createdAt;

    private String updatedBy;

    private LocalDateTime updatedAt;

    @Builder.Default
    private List<Permission> permissions = new ArrayList<>();
}
