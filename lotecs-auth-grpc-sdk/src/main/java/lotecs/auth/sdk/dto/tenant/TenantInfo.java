package lotecs.auth.sdk.dto.tenant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TenantInfo {
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
    private int siteLevel;
    private int maxContentItems;
    private int maxStorageMb;
    private int maxUsers;
    private String features;
    private String settings;
    private String status;
    private String publishedAt;
    private String unpublishedAt;
    private String subscriptionPlanCode;
    private String planStartDate;
    private String planEndDate;
    private long version;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;

    public static TenantInfo fromProto(com.lotecs.auth.grpc.TenantInfo proto) {
        return TenantInfo.builder()
                .tenantId(proto.getTenantId())
                .siteName(proto.getSiteName())
                .siteCode(proto.getSiteCode())
                .primaryDomain(proto.getPrimaryDomain())
                .additionalDomains(proto.getAdditionalDomains())
                .description(proto.getDescription())
                .siteTitle(proto.getSiteTitle())
                .siteDescription(proto.getSiteDescription())
                .themeName(proto.getThemeName())
                .defaultLanguage(proto.getDefaultLanguage())
                .timezone(proto.getTimezone())
                .ownerEmail(proto.getOwnerEmail())
                .adminEmail(proto.getAdminEmail())
                .contactPhone(proto.getContactPhone())
                .parentTenantId(proto.getParentTenantId())
                .siteLevel(proto.getSiteLevel())
                .maxContentItems(proto.getMaxContentItems())
                .maxStorageMb(proto.getMaxStorageMb())
                .maxUsers(proto.getMaxUsers())
                .features(proto.getFeatures())
                .settings(proto.getSettings())
                .status(proto.getStatus())
                .publishedAt(proto.getPublishedAt())
                .unpublishedAt(proto.getUnpublishedAt())
                .subscriptionPlanCode(proto.getSubscriptionPlanCode())
                .planStartDate(proto.getPlanStartDate())
                .planEndDate(proto.getPlanEndDate())
                .version(proto.getVersion())
                .createdBy(proto.getCreatedBy())
                .createdAt(proto.getCreatedAt())
                .updatedBy(proto.getUpdatedBy())
                .updatedAt(proto.getUpdatedAt())
                .build();
    }
}
