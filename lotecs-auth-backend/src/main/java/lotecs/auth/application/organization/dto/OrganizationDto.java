package lotecs.auth.application.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDto {

    private Long id;
    private String tenantId;
    private String organizationId;
    private String organizationCode;
    private String organizationName;
    private String organizationType;
    private String parentOrganizationId;
    private Integer orgLevel;
    private Integer displayOrder;
    private String description;
    private Boolean active;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
