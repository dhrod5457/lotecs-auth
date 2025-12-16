package lotecs.auth.domain.user.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    private Long permissionId;

    @NotBlank
    private String permissionCode;

    private String description;

    private String resourceType;

    private LocalDateTime createdAt;
}
