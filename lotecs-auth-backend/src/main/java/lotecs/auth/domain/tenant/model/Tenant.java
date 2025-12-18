package lotecs.auth.domain.tenant.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lotecs.auth.exception.tenant.TenantStateException;
import lotecs.auth.exception.tenant.TenantValidationException;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {

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

    private SiteStatus status;

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

    /**
     * 새로운 테넌트 생성
     */
    public static Tenant create(String siteName, String siteCode, String description, String createdBy) {
        return Tenant.builder()
                .tenantId(java.util.UUID.randomUUID().toString())
                .siteName(siteName)
                .siteCode(siteCode)
                .description(description)
                .status(SiteStatus.DRAFT)
                .siteLevel(0)
                .version(1L)
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void publish(String updatedBy) {
        if (!status.canPublish()) {
            throw TenantStateException.cannotPublish(status.name());
        }
        if (this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
        this.status = SiteStatus.PUBLISHED;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void unpublish(String updatedBy, String reason) {
        if (!status.canUnpublish()) {
            throw TenantStateException.cannotUnpublish(status.name());
        }
        this.unpublishedAt = LocalDateTime.now();
        this.status = SiteStatus.DRAFT;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void suspend(String updatedBy, String reason) {
        if (!status.canSuspend()) {
            throw TenantStateException.cannotSuspend(status.name());
        }
        this.status = SiteStatus.SUSPENDED;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void resume(String updatedBy) {
        if (!status.canResume()) {
            throw TenantStateException.cannotResume(status.name());
        }
        this.status = SiteStatus.PUBLISHED;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void archive(String updatedBy) {
        if (!status.canArchive()) {
            throw TenantStateException.cannotArchive(status.name());
        }
        this.status = SiteStatus.ARCHIVED;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void changePrimaryDomain(String newDomain, String updatedBy) {
        if (newDomain == null || newDomain.trim().isEmpty()) {
            throw TenantValidationException.domainRequired();
        }
        this.primaryDomain = newDomain;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeTheme(String newTheme, String updatedBy) {
        if (newTheme == null || newTheme.trim().isEmpty()) {
            throw TenantValidationException.themeRequired();
        }
        this.themeName = newTheme;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateSiteInfo(String newSiteName, String newDescription, String updatedBy) {
        if (newSiteName != null && !newSiteName.trim().isEmpty()) {
            this.siteName = newSiteName;
        }
        if (newDescription != null) {
            this.description = newDescription;
        }
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePlan(String planCode, LocalDateTime startDate, LocalDateTime endDate, String updatedBy) {
        if (planCode == null || planCode.trim().isEmpty()) {
            throw TenantValidationException.planRequired();
        }
        this.subscriptionPlanCode = planCode;
        this.planStartDate = startDate;
        this.planEndDate = endDate;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isPublished() {
        return status == SiteStatus.PUBLISHED;
    }

    public boolean isRootSite() {
        return parentTenantId == null || siteLevel == 0;
    }

    public boolean isSubSite() {
        return parentTenantId != null && siteLevel > 0;
    }

    public void validate() {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw TenantValidationException.idRequired();
        }
        if (siteName == null || siteName.trim().isEmpty()) {
            throw TenantValidationException.nameRequired();
        }
        if (siteCode == null || siteCode.trim().isEmpty()) {
            throw TenantValidationException.codeRequired();
        }
        if (siteLevel != null && (siteLevel < 0 || siteLevel > 5)) {
            throw TenantValidationException.levelInvalid();
        }
    }
}
