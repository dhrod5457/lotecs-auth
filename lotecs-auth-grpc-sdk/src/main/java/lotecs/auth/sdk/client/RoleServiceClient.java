package lotecs.auth.sdk.client;

import com.lotecs.auth.grpc.RoleServiceGrpc;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.sdk.dto.role.*;
import lotecs.auth.sdk.exception.AuthGrpcException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RoleServiceClient {

    @GrpcClient("lotecs-auth")
    private RoleServiceGrpc.RoleServiceBlockingStub roleServiceStub;

    public RoleResponse getRole(GetRoleRequest request) {
        try {
            log.debug("gRPC getRole request: roleId={}, tenantId={}", request.getRoleId(), request.getTenantId());
            com.lotecs.auth.grpc.RoleResponse response = roleServiceStub.getRole(request.toProto());
            return RoleResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC getRole failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public RoleResponse getRoleByName(GetRoleByNameRequest request) {
        try {
            log.debug("gRPC getRoleByName request: roleName={}, tenantId={}", request.getRoleName(), request.getTenantId());
            com.lotecs.auth.grpc.RoleResponse response = roleServiceStub.getRoleByName(request.toProto());
            return RoleResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC getRoleByName failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public ListRolesResponse listRoles(ListRolesRequest request) {
        try {
            log.debug("gRPC listRoles request: tenantId={}", request.getTenantId());
            com.lotecs.auth.grpc.ListRolesResponse response = roleServiceStub.listRoles(request.toProto());
            return ListRolesResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC listRoles failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public RoleResponse createRole(CreateRoleRequest request) {
        try {
            log.debug("gRPC createRole request: roleName={}, tenantId={}", request.getRoleName(), request.getTenantId());
            com.lotecs.auth.grpc.RoleResponse response = roleServiceStub.createRole(request.toProto());
            return RoleResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC createRole failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public RoleResponse updateRole(UpdateRoleRequest request) {
        try {
            log.debug("gRPC updateRole request: roleId={}, tenantId={}", request.getRoleId(), request.getTenantId());
            com.lotecs.auth.grpc.RoleResponse response = roleServiceStub.updateRole(request.toProto());
            return RoleResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC updateRole failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public DeleteRoleResponse deleteRole(DeleteRoleRequest request) {
        try {
            log.debug("gRPC deleteRole request: roleId={}, tenantId={}", request.getRoleId(), request.getTenantId());
            com.lotecs.auth.grpc.DeleteRoleResponse response = roleServiceStub.deleteRole(request.toProto());
            return DeleteRoleResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC deleteRole failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public ListRolesResponse getUserRoles(GetUserRolesRequest request) {
        try {
            log.debug("gRPC getUserRoles request: userId={}, tenantId={}", request.getUserId(), request.getTenantId());
            com.lotecs.auth.grpc.ListRolesResponse response = roleServiceStub.getUserRoles(request.toProto());
            return ListRolesResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC getUserRoles failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }
}
