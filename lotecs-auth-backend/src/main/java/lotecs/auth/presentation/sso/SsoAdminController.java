package lotecs.auth.presentation.sso;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.application.sso.dto.SsoConfigDto;
import lotecs.auth.application.sso.service.SsoConfigService;
import lotecs.framework.web.dto.CommonResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/sso")
@RequiredArgsConstructor
public class SsoAdminController {

    private final SsoConfigService ssoConfigService;

    /**
     * GET /api/v1/ath/admin/sso/config/{tenantId} - SSO 설정 조회
     */
    @GetMapping("/config/{tenantId}")
    public CommonResponse<SsoConfigDto> getSsoConfig(@PathVariable String tenantId) {
        SsoConfigDto config = ssoConfigService.getSsoConfig(tenantId);
        return CommonResponse.success(config);
    }

    /**
     * PUT /admin/sso/config/{tenantId} - SSO 설정 수정
     */
    @PutMapping("/config/{tenantId}")
    public CommonResponse<SsoConfigDto> updateSsoConfig(
            @PathVariable String tenantId,
            @Valid @RequestBody SsoConfigDto request) {

        SsoConfigDto config = ssoConfigService.updateSsoConfig(tenantId, request);
        return CommonResponse.success(config);
    }

    /**
     * POST /admin/sso/test - SSO 연결 테스트
     */
    @PostMapping("/test")
    public CommonResponse<Map<String, Object>> testSsoConnection(@RequestParam String tenantId) {
        boolean success = ssoConfigService.testSsoConnection(tenantId);

        return CommonResponse.success(Map.of(
                "success", success,
                "message", success ? "SSO connection test successful" : "SSO connection test failed"
        ));
    }
}
