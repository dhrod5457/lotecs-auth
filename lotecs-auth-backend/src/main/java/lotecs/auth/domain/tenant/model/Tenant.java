package lotecs.auth.domain.tenant.model;

import java.time.LocalDateTime;
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
            throw new IllegalStateException("DRAFT 상태에서만 게시할 수 있습니다. 현재 상태: " + status);
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
            throw new IllegalStateException("PUBLISHED 상태에서만 게시 중단할 수 있습니다. 현재 상태: " + status);
        }
        this.unpublishedAt = LocalDateTime.now();
        this.status = SiteStatus.DRAFT;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void suspend(String updatedBy, String reason) {
        if (!status.canSuspend()) {
            throw new IllegalStateException("PUBLISHED 상태에서만 일시중지할 수 있습니다. 현재 상태: " + status);
        }
        this.status = SiteStatus.SUSPENDED;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void resume(String updatedBy) {
        if (!status.canResume()) {
            throw new IllegalStateException("SUSPENDED 상태에서만 재개할 수 있습니다. 현재 상태: " + status);
        }
        this.status = SiteStatus.PUBLISHED;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void archive(String updatedBy) {
        if (!status.canArchive()) {
            throw new IllegalStateException("DRAFT 또는 SUSPENDED 상태에서만 보관할 수 있습니다. 현재 상태: " + status);
        }
        this.status = SiteStatus.ARCHIVED;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void changePrimaryDomain(String newDomain, String updatedBy) {
        if (newDomain == null || newDomain.trim().isEmpty()) {
            throw new IllegalArgumentException("주 도메인은 필수입니다.");
        }
        this.primaryDomain = newDomain;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeTheme(String newTheme, String updatedBy) {
        if (newTheme == null || newTheme.trim().isEmpty()) {
            throw new IllegalArgumentException("테마는 필수입니다.");
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
            throw new IllegalArgumentException("플랜 코드는 필수입니다.");
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
            throw new IllegalArgumentException("Tenant ID는 필수입니다.");
        }
        if (siteName == null || siteName.trim().isEmpty()) {
            throw new IllegalArgumentException("사이트 이름은 필수입니다.");
        }
        if (siteCode == null || siteCode.trim().isEmpty()) {
            throw new IllegalArgumentException("사이트 코드는 필수입니다.");
        }
        if (siteLevel != null && (siteLevel < 0 || siteLevel > 5)) {
            throw new IllegalArgumentException("사이트 레벨은 0~5 범위여야 합니다.");
        }
    }
}
