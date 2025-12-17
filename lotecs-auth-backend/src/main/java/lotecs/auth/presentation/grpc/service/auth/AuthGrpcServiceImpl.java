package lotecs.auth.presentation.grpc.service.auth;

import com.google.protobuf.Struct;
import com.lotecs.auth.grpc.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.application.auth.dto.LoginRequest;
import lotecs.auth.application.auth.dto.LoginResponse;
import lotecs.auth.application.auth.dto.ValidateTokenResponse;
import lotecs.auth.application.auth.service.AuthService;
import lotecs.auth.application.user.dto.CreateUserRequest;
import lotecs.auth.application.user.dto.UpdateUserRequest;
import lotecs.auth.application.user.dto.UserDto;
import lotecs.auth.application.user.service.UserService;
import lotecs.framework.common.grpc.core.util.StructConverter;
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
            com.lotecs.auth.grpc.LoginResponse.Builder grpcResponseBuilder = com.lotecs.auth.grpc.LoginResponse.newBuilder()
                    .setAccessToken(loginResponse.getAccessToken())
                    .setRefreshToken(loginResponse.getRefreshToken())
                    .setExpiresIn(loginResponse.getExpiresIn().intValue())
                    .setUser(toUserInfo(loginResponse.getUser()))
                    .setSsoType(loginResponse.getSsoType() != null ? loginResponse.getSsoType().name() : "INTERNAL");

            // additionalData 추가
            if (loginResponse.getAdditionalData() != null) {
                Struct additionalDataStruct = StructConverter.toStruct(loginResponse.getAdditionalData());
                grpcResponseBuilder.setAdditionalData(additionalDataStruct);
            }

            responseObserver.onNext(grpcResponseBuilder.build());
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
            authService.logout(request.getAccessToken(), request.getUserId());

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
            UserDto user = userService.getUserById(request.getUserId(), request.getTenantId());

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
                    request.getUserId(),
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
            userService.deleteUser(request.getUserId(), request.getTenantId());

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

    // Role Management APIs

    /**
     * 단일 역할 할당
     */
    @Override
    public void assignRole(com.lotecs.auth.grpc.AssignRoleRequest request, StreamObserver<com.lotecs.auth.grpc.AssignRoleResponse> responseObserver) {
        log.info("[gRPC] assignRole 호출: userId={}, roleId={}", request.getUserId(), request.getRoleId());

        try {
            userService.assignRole(
                    request.getUserId(),
                    request.getTenantId(),
                    request.getRoleId(),
                    request.getStatusCode(),
                    request.getAssignedBy()
            );

            com.lotecs.auth.grpc.AssignRoleResponse grpcResponse = com.lotecs.auth.grpc.AssignRoleResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Role assigned successfully")
                    .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] assignRole 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * 다중 역할 할당
     */
    @Override
    public void assignRoles(com.lotecs.auth.grpc.AssignRolesRequest request, StreamObserver<com.lotecs.auth.grpc.AssignRolesResponse> responseObserver) {
        log.info("[gRPC] assignRoles 호출: userId={}, roleCount={}", request.getUserId(), request.getRoleIdsCount());

        try {
            int assignedCount = userService.assignRoles(
                    request.getUserId(),
                    request.getTenantId(),
                    request.getRoleIdsList(),
                    request.getStatusCode(),
                    request.getAssignedBy()
            );

            com.lotecs.auth.grpc.AssignRolesResponse grpcResponse = com.lotecs.auth.grpc.AssignRolesResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Roles assigned successfully")
                    .setAssignedCount(assignedCount)
                    .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] assignRoles 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * 역할 제거
     */
    @Override
    public void revokeRole(com.lotecs.auth.grpc.RevokeRoleRequest request, StreamObserver<com.lotecs.auth.grpc.RevokeRoleResponse> responseObserver) {
        log.info("[gRPC] revokeRole 호출: userId={}, roleId={}", request.getUserId(), request.getRoleId());

        try {
            userService.revokeRole(
                    request.getUserId(),
                    request.getTenantId(),
                    request.getRoleId(),
                    request.getRevokedBy()
            );

            com.lotecs.auth.grpc.RevokeRoleResponse grpcResponse = com.lotecs.auth.grpc.RevokeRoleResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Role revoked successfully")
                    .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] revokeRole 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    // User Status Management APIs

    /**
     * 계정 잠금
     */
    @Override
    public void lockUser(com.lotecs.auth.grpc.LockUserRequest request, StreamObserver<com.lotecs.auth.grpc.LockUserResponse> responseObserver) {
        log.info("[gRPC] lockUser 호출: userId={}, reason={}", request.getUserId(), request.getReason());

        try {
            userService.lockUser(
                    request.getUserId(),
                    request.getTenantId(),
                    request.getReason(),
                    request.getLockedBy()
            );

            com.lotecs.auth.grpc.LockUserResponse grpcResponse = com.lotecs.auth.grpc.LockUserResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("User locked successfully")
                    .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] lockUser 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * 계정 잠금 해제
     */
    @Override
    public void unlockUser(com.lotecs.auth.grpc.UnlockUserRequest request, StreamObserver<com.lotecs.auth.grpc.UnlockUserResponse> responseObserver) {
        log.info("[gRPC] unlockUser 호출: userId={}", request.getUserId());

        try {
            userService.unlockUser(
                    request.getUserId(),
                    request.getTenantId(),
                    request.getUnlockedBy()
            );

            com.lotecs.auth.grpc.UnlockUserResponse grpcResponse = com.lotecs.auth.grpc.UnlockUserResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("User unlocked successfully")
                    .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] unlockUser 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * 비밀번호 변경
     */
    @Override
    public void changePassword(com.lotecs.auth.grpc.ChangePasswordRequest request, StreamObserver<com.lotecs.auth.grpc.ChangePasswordResponse> responseObserver) {
        log.info("[gRPC] changePassword 호출: userId={}", request.getUserId());

        try {
            userService.changePassword(
                    request.getUserId(),
                    request.getTenantId(),
                    request.getCurrentPassword(),
                    request.getNewPassword()
            );

            com.lotecs.auth.grpc.ChangePasswordResponse grpcResponse = com.lotecs.auth.grpc.ChangePasswordResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Password changed successfully")
                    .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] changePassword 실패: {}", e.getMessage(), e);
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
