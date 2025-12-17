package lotecs.auth.sdk.client;

import com.lotecs.auth.grpc.PermissionServiceGrpc;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.sdk.dto.permission.*;
import lotecs.auth.sdk.exception.AuthGrpcException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PermissionServiceClient {

    @GrpcClient("lotecs-auth")
    private PermissionServiceGrpc.PermissionServiceBlockingStub permissionServiceStub;

    public PermissionResponse getPermission(GetPermissionRequest request) {
        try {
            log.debug("gRPC getPermission request: permissionId={}, tenantId={}", request.getPermissionId(), request.getTenantId());
            com.lotecs.auth.grpc.PermissionResponse response = permissionServiceStub.getPermission(request.toProto());
            return PermissionResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC getPermission failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public ListPermissionsResponse listPermissions(ListPermissionsRequest request) {
        try {
            log.debug("gRPC listPermissions request: tenantId={}", request.getTenantId());
            com.lotecs.auth.grpc.ListPermissionsResponse response = permissionServiceStub.listPermissions(request.toProto());
            return ListPermissionsResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC listPermissions failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public PermissionResponse createPermission(CreatePermissionRequest request) {
        try {
            log.debug("gRPC createPermission request: permissionCode={}, tenantId={}", request.getPermissionCode(), request.getTenantId());
            com.lotecs.auth.grpc.PermissionResponse response = permissionServiceStub.createPermission(request.toProto());
            return PermissionResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC createPermission failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public PermissionResponse updatePermission(UpdatePermissionRequest request) {
        try {
            log.debug("gRPC updatePermission request: permissionId={}, tenantId={}", request.getPermissionId(), request.getTenantId());
            com.lotecs.auth.grpc.PermissionResponse response = permissionServiceStub.updatePermission(request.toProto());
            return PermissionResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC updatePermission failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public DeletePermissionResponse deletePermission(DeletePermissionRequest request) {
        try {
            log.debug("gRPC deletePermission request: permissionId={}, tenantId={}", request.getPermissionId(), request.getTenantId());
            com.lotecs.auth.grpc.DeletePermissionResponse response = permissionServiceStub.deletePermission(request.toProto());
            return DeletePermissionResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC deletePermission failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public ListPermissionsResponse getRolePermissions(GetRolePermissionsRequest request) {
        try {
            log.debug("gRPC getRolePermissions request: roleId={}, tenantId={}", request.getRoleId(), request.getTenantId());
            com.lotecs.auth.grpc.ListPermissionsResponse response = permissionServiceStub.getRolePermissions(request.toProto());
            return ListPermissionsResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC getRolePermissions failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public AssignPermissionsToRoleResponse assignPermissionsToRole(AssignPermissionsToRoleRequest request) {
        try {
            log.debug("gRPC assignPermissionsToRole request: roleId={}, tenantId={}, permissionCount={}",
                    request.getRoleId(), request.getTenantId(),
                    request.getPermissionIds() != null ? request.getPermissionIds().size() : 0);
            com.lotecs.auth.grpc.AssignPermissionsToRoleResponse response = permissionServiceStub.assignPermissionsToRole(request.toProto());
            return AssignPermissionsToRoleResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC assignPermissionsToRole failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public RevokePermissionFromRoleResponse revokePermissionFromRole(RevokePermissionFromRoleRequest request) {
        try {
            log.debug("gRPC revokePermissionFromRole request: roleId={}, tenantId={}, permissionId={}",
                    request.getRoleId(), request.getTenantId(), request.getPermissionId());
            com.lotecs.auth.grpc.RevokePermissionFromRoleResponse response = permissionServiceStub.revokePermissionFromRole(request.toProto());
            return RevokePermissionFromRoleResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC revokePermissionFromRole failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }
}
