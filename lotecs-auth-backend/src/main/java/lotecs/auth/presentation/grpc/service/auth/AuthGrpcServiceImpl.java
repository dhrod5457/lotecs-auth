package lotecs.auth.presentation.grpc.service.auth;

import com.lotecs.auth.grpc.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.application.auth.dto.LoginRequest;
import lotecs.auth.application.auth.dto.LoginResponse;
import lotecs.auth.application.auth.service.AuthService;
import lotecs.auth.application.token.dto.ValidateTokenResponse;
import lotecs.auth.application.user.dto.CreateUserRequest;
import lotecs.auth.application.user.dto.UpdateUserRequest;
import lotecs.auth.application.user.dto.UserDto;
import lotecs.auth.application.user.service.UserService;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AuthGrpcServiceImpl extends AuthServiceGrpc.AuthServiceImplBase {

    private final AuthService authService;
    private final UserService userService;

    /**
     * 로그인
     */
    @Override
    public void login(com.lotecs.auth.grpc.LoginRequest request, StreamObserver<com.lotecs.auth.grpc.LoginResponse> responseObserver) {
        log.info("[gRPC] login 호출: username={}, tenant={}", request.getUsername(), request.getTenantId());

        try {
            // DTO 변환
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername(request.getUsername());
            loginRequest.setPassword(request.getPassword());
            loginRequest.setTenantId(request.getTenantId());
            loginRequest.setIpAddress(request.getIpAddress());

            // 로그인 처리
            LoginResponse loginResponse = authService.login(loginRequest);

            // gRPC 응답 생성
            com.lotecs.auth.grpc.LoginResponse grpcResponse = com.lotecs.auth.grpc.LoginResponse.newBuilder()
                    .setAccessToken(loginResponse.getAccessToken())
                    .setRefreshToken(loginResponse.getRefreshToken())
                    .setExpiresIn(loginResponse.getExpiresIn().intValue())
                    .setUser(toUserInfo(loginResponse.getUser()))
                    .setSsoType(loginResponse.getSsoType() != null ? loginResponse.getSsoType().name() : "INTERNAL")
                    .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] login 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * 로그아웃
     */
    @Override
    public void logout(com.lotecs.auth.grpc.LogoutRequest request, StreamObserver<com.lotecs.auth.grpc.LogoutResponse> responseObserver) {
        log.info("[gRPC] logout 호출: userId={}, tenant={}", request.getUserId(), request.getTenantId());

        try {
            // 로그아웃 처리
            authService.logout(Long.parseLong(request.getUserId()));

            // gRPC 응답 생성
            com.lotecs.auth.grpc.LogoutResponse grpcResponse = com.lotecs.auth.grpc.LogoutResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Logout successful")
                    .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] logout 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * 토큰 갱신
     */
    @Override
    public void refreshToken(com.lotecs.auth.grpc.RefreshTokenRequest request, StreamObserver<com.lotecs.auth.grpc.RefreshTokenResponse> responseObserver) {
        log.info("[gRPC] refreshToken 호출");

        try {
            // 토큰 갱신
            LoginResponse loginResponse = authService.refresh(request.getRefreshToken());

            // gRPC 응답 생성
            com.lotecs.auth.grpc.RefreshTokenResponse grpcResponse = com.lotecs.auth.grpc.RefreshTokenResponse.newBuilder()
                    .setAccessToken(loginResponse.getAccessToken())
                    .setRefreshToken(loginResponse.getRefreshToken())
                    .setExpiresIn(loginResponse.getExpiresIn().intValue())
                    .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] refreshToken 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * 토큰 검증
     */
    @Override
    public void validateToken(com.lotecs.auth.grpc.ValidateTokenRequest request, StreamObserver<com.lotecs.auth.grpc.ValidateTokenResponse> responseObserver) {
        log.debug("[gRPC] validateToken 호출");

        try {
            // 토큰 검증
            ValidateTokenResponse validateResponse = authService.validate(request.getAccessToken());

            // gRPC 응답 생성
            com.lotecs.auth.grpc.ValidateTokenResponse.Builder grpcResponseBuilder = com.lotecs.auth.grpc.ValidateTokenResponse.newBuilder()
                    .setValid(validateResponse.getValid());

            if (validateResponse.getUser() != null) {
                grpcResponseBuilder.setUser(toUserInfo(validateResponse.getUser()));
            }

            if (validateResponse.getErrorMessage() != null) {
                grpcResponseBuilder.setErrorMessage(validateResponse.getErrorMessage());
            }

            responseObserver.onNext(grpcResponseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] validateToken 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * 사용자 조회 (ID)
     */
    @Override
    public void getUserById(com.lotecs.auth.grpc.GetUserByIdRequest request, StreamObserver<com.lotecs.auth.grpc.UserResponse> responseObserver) {
        log.debug("[gRPC] getUserById 호출: userId={}", request.getUserId());

        try {
            UserDto user = userService.getUserById(Long.parseLong(request.getUserId()), request.getTenantId());

            com.lotecs.auth.grpc.UserResponse grpcResponse = com.lotecs.auth.grpc.UserResponse.newBuilder()
                    .setUser(toUserInfo(user))
                    .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] getUserById 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * 사용자 조회 (Username)
     */
    @Override
    public void getUserByUsername(com.lotecs.auth.grpc.GetUserByUsernameRequest request, StreamObserver<com.lotecs.auth.grpc.UserResponse> responseObserver) {
        log.debug("[gRPC] getUserByUsername 호출: username={}", request.getUsername());

        try {
            UserDto user = userService.getUserByUsername(request.getUsername(), request.getTenantId());

            com.lotecs.auth.grpc.UserResponse grpcResponse = com.lotecs.auth.grpc.UserResponse.newBuilder()
                    .setUser(toUserInfo(user))
                    .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] getUserByUsername 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * 사용자 목록 조회
     */
    @Override
    public void getUsers(com.lotecs.auth.grpc.GetUsersRequest request, StreamObserver<com.lotecs.auth.grpc.GetUsersResponse> responseObserver) {
        log.debug("[gRPC] getUsers 호출: tenant={}", request.getTenantId());

        try {
            List<UserDto> users = userService.getUsers(
                    request.getTenantId(),
                    request.getPage(),
                    request.getSize()
            );

            List<com.lotecs.auth.grpc.UserInfo> userInfos = users.stream()
                    .map(this::toUserInfo)
                    .collect(Collectors.toList());

            com.lotecs.auth.grpc.GetUsersResponse grpcResponse = com.lotecs.auth.grpc.GetUsersResponse.newBuilder()
                    .addAllUsers(userInfos)
                    .setTotal(users.size())
                    .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] getUsers 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * 사용자 생성
     */
    @Override
    public void createUser(com.lotecs.auth.grpc.CreateUserRequest request, StreamObserver<com.lotecs.auth.grpc.UserResponse> responseObserver) {
        log.info("[gRPC] createUser 호출: username={}, tenant={}", request.getUsername(), request.getTenantId());

        try {
            // DTO 변환
            CreateUserRequest createRequest = CreateUserRequest.builder()
                    .tenantId(request.getTenantId())
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .email(request.getEmail())
                    .fullName(request.getFullName())
                    .roles(request.getRolesList())
                    .build();

            // 사용자 생성
            UserDto user = userService.createUser(createRequest);

            com.lotecs.auth.grpc.UserResponse grpcResponse = com.lotecs.auth.grpc.UserResponse.newBuilder()
                    .setUser(toUserInfo(user))
                    .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] createUser 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * 사용자 수정
     */
    @Override
    public void updateUser(com.lotecs.auth.grpc.UpdateUserRequest request, StreamObserver<com.lotecs.auth.grpc.UserResponse> responseObserver) {
        log.info("[gRPC] updateUser 호출: userId={}", request.getUserId());

        try {
            // DTO 변환
            UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                    .email(request.getEmail())
                    .fullName(request.getFullName())
                    .status(request.getStatus())
                    .build();

            // 사용자 수정
            UserDto user = userService.updateUser(
                    Long.parseLong(request.getUserId()),
                    request.getTenantId(),
                    updateRequest
            );

            com.lotecs.auth.grpc.UserResponse grpcResponse = com.lotecs.auth.grpc.UserResponse.newBuilder()
                    .setUser(toUserInfo(user))
                    .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] updateUser 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * 사용자 삭제
     */
    @Override
    public void deleteUser(com.lotecs.auth.grpc.DeleteUserRequest request, StreamObserver<com.lotecs.auth.grpc.DeleteUserResponse> responseObserver) {
        log.info("[gRPC] deleteUser 호출: userId={}", request.getUserId());

        try {
            userService.deleteUser(Long.parseLong(request.getUserId()), request.getTenantId());

            com.lotecs.auth.grpc.DeleteUserResponse grpcResponse = com.lotecs.auth.grpc.DeleteUserResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("User deleted successfully")
                    .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] deleteUser 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * 권한 확인
     */
    @Override
    public void checkPermission(com.lotecs.auth.grpc.PermissionCheckRequest request, StreamObserver<com.lotecs.auth.grpc.PermissionCheckResponse> responseObserver) {
        log.debug("[gRPC] checkPermission 호출: userId={}, permission={}",
                request.getUserId(), request.getPermissionCode());

        try {
            // TODO: 권한 확인 로직 구현
            boolean hasPermission = false;

            com.lotecs.auth.grpc.PermissionCheckResponse grpcResponse = com.lotecs.auth.grpc.PermissionCheckResponse.newBuilder()
                    .setHasPermission(hasPermission)
                    .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] checkPermission 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * UserDto -> UserInfo 변환
     */
    private com.lotecs.auth.grpc.UserInfo toUserInfo(UserDto user) {
        com.lotecs.auth.grpc.UserInfo.Builder builder = com.lotecs.auth.grpc.UserInfo.newBuilder()
                .setUserId(String.valueOf(user.getUserId()))
                .setTenantId(user.getTenantId())
                .setUsername(user.getUsername());

        if (user.getEmail() != null) {
            builder.setEmail(user.getEmail());
        }
        if (user.getFullName() != null) {
            builder.setFullName(user.getFullName());
        }
        if (user.getStatus() != null) {
            builder.setStatus(user.getStatus());
        }
        if (user.getRoles() != null) {
            builder.addAllRoles(user.getRoles());
        }
        if (user.getExternalUserId() != null) {
            builder.setExternalUserId(user.getExternalUserId());
        }
        if (user.getExternalSystem() != null) {
            builder.setExternalSystem(user.getExternalSystem());
        }

        return builder.build();
    }
}
