package lotecs.auth.presentation.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.application.auth.dto.LoginRequest;
import lotecs.auth.application.auth.dto.LoginResponse;
import lotecs.auth.application.auth.dto.LogoutRequest;
import lotecs.auth.application.auth.service.AuthService;
import lotecs.auth.application.token.dto.ValidateTokenRequest;
import lotecs.auth.application.token.dto.ValidateTokenResponse;
import lotecs.framework.web.dto.CommonResponse;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /auth/login - 로그인
     */
    @PostMapping("/login")
    public CommonResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor) {

        // IP 주소 설정
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            String ipAddress = forwardedFor.split(",")[0].trim();
            request.setIpAddress(ipAddress);
        }

        LoginResponse response = authService.login(request);

        return CommonResponse.success(response);
    }

    /**
     * POST /auth/logout - 로그아웃
     */
    @PostMapping("/logout")
    public CommonResponse<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.getUserId());
        return CommonResponse.success();
    }

    /**
     * POST /auth/refresh - 토큰 갱신
     */
    @PostMapping("/refresh")
    public CommonResponse<LoginResponse> refresh(@RequestParam String refreshToken) {
        LoginResponse response = authService.refresh(refreshToken);
        return CommonResponse.success(response);
    }

    /**
     * POST /auth/validate - 토큰 검증
     */
    @PostMapping("/validate")
    public CommonResponse<ValidateTokenResponse> validate(@Valid @RequestBody ValidateTokenRequest request) {
        ValidateTokenResponse response = authService.validate(request.getAccessToken());
        return CommonResponse.success(response);
    }
}
