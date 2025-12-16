package lotecs.auth.application.sso.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoConfigDto {

    private String tenantId;
    private String ssoType;
    private Boolean ssoEnabled;
    private String relayEndpoint;
    private Integer relayTimeoutMs;
    private String ssoServerUrl;
    private String ssoRealm;
    private String ssoClientId;
    private Boolean userSyncEnabled;
    private Boolean roleMappingEnabled;
}
