package lotecs.auth.application.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOrganizationDto {

    private Long id;
    private String tenantId;
    private String userId;
    private String organizationId;
    private String roleId;
    private Boolean isPrimary;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
