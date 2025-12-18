package lotecs.auth.presentation.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.application.user.dto.CreateUserRequest;
import lotecs.auth.application.user.dto.UpdateUserRequest;
import lotecs.auth.application.user.dto.UserDto;
import lotecs.auth.application.user.service.UserService;
import lotecs.framework.web.dto.CommonResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    /**
     * POST /api/v1/ath/users - 사용자 생성
     */
    @PostMapping("/users")
    public CommonResponse<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDto user = userService.createUser(request);
        return CommonResponse.success(user);
    }

    /**
     * GET /admin/users/{userId} - 사용자 조회
     */
    @GetMapping("/users/{userId}")
    public CommonResponse<UserDto> getUser(
            @PathVariable String userId,
            @RequestParam String tenantId) {

        UserDto user = userService.getUserById(userId, tenantId);
        return CommonResponse.success(user);
    }

    /**
     * PUT /admin/users/{userId} - 사용자 수정
     */
    @PutMapping("/users/{userId}")
    public CommonResponse<UserDto> updateUser(
            @PathVariable String userId,
            @RequestParam String tenantId,
            @Valid @RequestBody UpdateUserRequest request) {

        UserDto user = userService.updateUser(userId, tenantId, request);
        return CommonResponse.success(user);
    }

    /**
     * DELETE /admin/users/{userId} - 사용자 삭제
     */
    @DeleteMapping("/users/{userId}")
    public CommonResponse<Void> deleteUser(
            @PathVariable String userId,
            @RequestParam String tenantId) {

        userService.deleteUser(userId, tenantId);
        return CommonResponse.success();
    }

    /**
     * GET /admin/users - 사용자 목록 조회
     */
    @GetMapping("/users")
    public CommonResponse<List<UserDto>> getUsers(
            @RequestParam String tenantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<UserDto> users = userService.getUsers(tenantId, page, size);
        return CommonResponse.success(users);
    }
}
