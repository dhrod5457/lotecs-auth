package lotecs.auth.sdk.dto.tenant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateTenantRequest {
    private String tenantId;
    private String siteName;
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
    private int maxContentItems;
    private int maxStorageMb;
    private int maxUsers;
    private String features;
    private String settings;
    private String updatedBy;

    public com.lotecs.auth.grpc.UpdateTenantRequest toProto() {
        return com.lotecs.auth.grpc.UpdateTenantRequest.newBuilder()
                .setTenantId(tenantId != null ? tenantId : "")
                .setSiteName(siteName != null ? siteName : "")
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
                .setMaxContentItems(maxContentItems)
                .setMaxStorageMb(maxStorageMb)
                .setMaxUsers(maxUsers)
                .setFeatures(features != null ? features : "")
                .setSettings(settings != null ? settings : "")
                .setUpdatedBy(updatedBy != null ? updatedBy : "")
                .build();
    }
}
