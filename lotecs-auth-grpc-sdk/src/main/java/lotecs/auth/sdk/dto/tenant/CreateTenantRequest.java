package lotecs.auth.sdk.dto.tenant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateTenantRequest {
    private String siteName;
    private String siteCode;
    private String description;
    private String primaryDomain;
    private String additionalDomains;
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
    private String createdBy;

    public com.lotecs.auth.grpc.CreateTenantRequest toProto() {
        return com.lotecs.auth.grpc.CreateTenantRequest.newBuilder()
                .setSiteName(siteName != null ? siteName : "")
                .setSiteCode(siteCode != null ? siteCode : "")
                .setDescription(description != null ? description : "")
                .setPrimaryDomain(primaryDomain != null ? primaryDomain : "")
                .setAdditionalDomains(additionalDomains != null ? additionalDomains : "")
                .setSiteTitle(siteTitle != null ? siteTitle : "")
                .setSiteDescription(siteDescription != null ? siteDescription : "")
                .setThemeName(themeName != null ? themeName : "")
                .setDefaultLanguage(defaultLanguage != null ? defaultLanguage : "")
                .setTimezone(timezone != null ? timezone : "")
                .setOwnerEmail(ownerEmail != null ? ownerEmail : "")
                .setAdminEmail(adminEmail != null ? adminEmail : "")
                .setContactPhone(contactPhone != null ? contactPhone : "")
                .setParentTenantId(parentTenantId != null ? parentTenantId : "")
                .setSiteLevel(siteLevel)
                .setMaxContentItems(maxContentItems)
                .setMaxStorageMb(maxStorageMb)
                .setMaxUsers(maxUsers)
                .setFeatures(features != null ? features : "")
                .setSettings(settings != null ? settings : "")
                .setCreatedBy(createdBy != null ? createdBy : "")
                .build();
    }
}
