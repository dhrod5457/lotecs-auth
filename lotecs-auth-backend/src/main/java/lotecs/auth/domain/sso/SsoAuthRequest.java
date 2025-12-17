package lotecs.auth.domain.sso;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoAuthRequest {

    @NotBlank
    private String tenantId;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String ipAddress;

    /**
     * SSO 토큰 (CAS ticket 등)
     */
    private String ssoToken;

    /**
     * 사용자 구분 코드 (REST_TOKEN용)
     */
    private String userDivision;

    /**
     * 대학 사용자 구분 코드 (REST_TOKEN용)
     */
    private String universityUserDivision;

    /**
     * 모바일 여부 (REST_TOKEN용)
     */
    @Builder.Default
    private boolean mobile = false;

    /**
     * 추가 파라미터
     */
    private Map<String, String> extraParams;
}
