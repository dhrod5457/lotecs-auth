package lotecs.auth.application.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleStatusDto {

    private Long id;
    private String statusCode;
    private String statusName;
    private String roleCategory;
    private Boolean isActive;
    private String description;
    private Integer sortOrder;
    private Boolean isDefault;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
