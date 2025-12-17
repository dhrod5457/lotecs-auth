package lotecs.auth.sdk.client;

import com.lotecs.auth.grpc.AuthServiceGrpc;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.sdk.dto.auth.*;
import lotecs.auth.sdk.exception.AuthGrpcException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthServiceClient {

    @GrpcClient("lotecs-auth")
    private AuthServiceGrpc.AuthServiceBlockingStub authServiceStub;

    public LoginResponse login(LoginRequest request) {
        try {
            log.debug("gRPC login request: username={}, tenantId={}", request.getUsername(), request.getTenantId());
            com.lotecs.auth.grpc.LoginResponse response = authServiceStub.login(request.toProto());
            return LoginResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC login failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public LogoutResponse logout(LogoutRequest request) {
        try {
            log.debug("gRPC logout request: userId={}, tenantId={}", request.getUserId(), request.getTenantId());
            com.lotecs.auth.grpc.LogoutResponse response = authServiceStub.logout(request.toProto());
            return LogoutResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC logout failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        try {
            log.debug("gRPC refreshToken request");
            com.lotecs.auth.grpc.RefreshTokenResponse response = authServiceStub.refreshToken(request.toProto());
            return RefreshTokenResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC refreshToken failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public ValidateTokenResponse validateToken(ValidateTokenRequest request) {
        try {
            log.debug("gRPC validateToken request");
            com.lotecs.auth.grpc.ValidateTokenResponse response = authServiceStub.validateToken(request.toProto());
            return ValidateTokenResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC validateToken failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public UserResponse getUserById(GetUserByIdRequest request) {
        try {
            log.debug("gRPC getUserById request: userId={}, tenantId={}", request.getUserId(), request.getTenantId());
            com.lotecs.auth.grpc.UserResponse response = authServiceStub.getUserById(request.toProto());
            return UserResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC getUserById failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public UserResponse getUserByUsername(GetUserByUsernameRequest request) {
        try {
            log.debug("gRPC getUserByUsername request: username={}, tenantId={}", request.getUsername(), request.getTenantId());
            com.lotecs.auth.grpc.UserResponse response = authServiceStub.getUserByUsername(request.toProto());
            return UserResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC getUserByUsername failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public GetUsersResponse getUsers(GetUsersRequest request) {
        try {
            log.debug("gRPC getUsers request: tenantId={}, page={}, size={}", request.getTenantId(), request.getPage(), request.getSize());
            com.lotecs.auth.grpc.GetUsersResponse response = authServiceStub.getUsers(request.toProto());
            return GetUsersResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC getUsers failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public UserResponse createUser(CreateUserRequest request) {
        try {
            log.debug("gRPC createUser request: username={}, tenantId={}", request.getUsername(), request.getTenantId());
            com.lotecs.auth.grpc.UserResponse response = authServiceStub.createUser(request.toProto());
            return UserResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC createUser failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public UserResponse updateUser(UpdateUserRequest request) {
        try {
            log.debug("gRPC updateUser request: userId={}, tenantId={}", request.getUserId(), request.getTenantId());
            com.lotecs.auth.grpc.UserResponse response = authServiceStub.updateUser(request.toProto());
            return UserResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC updateUser failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public DeleteUserResponse deleteUser(DeleteUserRequest request) {
        try {
            log.debug("gRPC deleteUser request: userId={}, tenantId={}", request.getUserId(), request.getTenantId());
            com.lotecs.auth.grpc.DeleteUserResponse response = authServiceStub.deleteUser(request.toProto());
            return DeleteUserResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC deleteUser failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public PermissionCheckResponse checkPermission(PermissionCheckRequest request) {
        try {
            log.debug("gRPC checkPermission request: userId={}, tenantId={}, permissionCode={}",
                    request.getUserId(), request.getTenantId(), request.getPermissionCode());
            com.lotecs.auth.grpc.PermissionCheckResponse response = authServiceStub.checkPermission(request.toProto());
            return PermissionCheckResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC checkPermission failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }
}
