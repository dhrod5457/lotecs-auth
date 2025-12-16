package lotecs.auth.application.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantDto {

    private String tenantId;
    private String siteName;
    private String siteCode;

    private String primaryDomain;
    private String additionalDomains;

    private String description;
    private String siteTitle;
    private String siteDescription;
    private String themeName;
    private String defaultLanguage;
    private String timezone;

    private String ownerEmail;
    private String adminEmail;
    private String contactPhone;

    private String parentTenantId;
    private Integer siteLevel;

    private Integer maxContentItems;
    private Integer maxStorageMb;
    private Integer maxUsers;

    private String features;
    private String settings;

    private String status;

    private LocalDateTime publishedAt;
    private LocalDateTime unpublishedAt;

    private String subscriptionPlanCode;
    private LocalDateTime planStartDate;
    private LocalDateTime planEndDate;

    private Long version;

    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
