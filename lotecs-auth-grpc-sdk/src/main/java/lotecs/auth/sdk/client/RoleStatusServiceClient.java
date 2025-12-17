package lotecs.auth.sdk.client;

import com.lotecs.auth.grpc.RoleStatusServiceGrpc;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.sdk.dto.rolestatus.CreateRoleStatusRequest;
import lotecs.auth.sdk.dto.rolestatus.DeleteRoleStatusRequest;
import lotecs.auth.sdk.dto.rolestatus.DeleteRoleStatusResponse;
import lotecs.auth.sdk.dto.rolestatus.GetRoleStatusRequest;
import lotecs.auth.sdk.dto.rolestatus.ListRoleStatusesByCategoryRequest;
import lotecs.auth.sdk.dto.rolestatus.ListRoleStatusesResponse;
import lotecs.auth.sdk.dto.rolestatus.RoleStatusResponse;
import lotecs.auth.sdk.dto.rolestatus.UpdateRoleStatusRequest;
import lotecs.auth.sdk.exception.AuthGrpcException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RoleStatusServiceClient {

    @GrpcClient("lotecs-auth")
    private RoleStatusServiceGrpc.RoleStatusServiceBlockingStub roleStatusServiceStub;

    public RoleStatusResponse getRoleStatus(GetRoleStatusRequest request) {
        try {
            log.debug("gRPC getRoleStatus request: statusCode={}", request.getStatusCode());
            com.lotecs.auth.grpc.RoleStatusResponse response = roleStatusServiceStub.getRoleStatus(request.toProto());
            return RoleStatusResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC getRoleStatus failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public ListRoleStatusesResponse listRoleStatuses() {
        try {
            log.debug("gRPC listRoleStatuses request");
            com.lotecs.auth.grpc.ListRoleStatusesResponse response = roleStatusServiceStub.listRoleStatuses(
                    com.lotecs.auth.grpc.ListRoleStatusesRequest.newBuilder().build());
            return ListRoleStatusesResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC listRoleStatuses failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public ListRoleStatusesResponse listActiveRoleStatuses() {
        try {
            log.debug("gRPC listActiveRoleStatuses request");
            com.lotecs.auth.grpc.ListRoleStatusesResponse response = roleStatusServiceStub.listActiveRoleStatuses(
                    com.lotecs.auth.grpc.ListActiveRoleStatusesRequest.newBuilder().build());
            return ListRoleStatusesResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC listActiveRoleStatuses failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public ListRoleStatusesResponse listRoleStatusesByCategory(ListRoleStatusesByCategoryRequest request) {
        try {
            log.debug("gRPC listRoleStatusesByCategory request: roleCategory={}", request.getRoleCategory());
            com.lotecs.auth.grpc.ListRoleStatusesResponse response = roleStatusServiceStub.listRoleStatusesByCategory(request.toProto());
            return ListRoleStatusesResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC listRoleStatusesByCategory failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public RoleStatusResponse createRoleStatus(CreateRoleStatusRequest request) {
        try {
            log.debug("gRPC createRoleStatus request: statusCode={}", request.getStatusCode());
            com.lotecs.auth.grpc.RoleStatusResponse response = roleStatusServiceStub.createRoleStatus(request.toProto());
            return RoleStatusResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC createRoleStatus failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public RoleStatusResponse updateRoleStatus(UpdateRoleStatusRequest request) {
        try {
            log.debug("gRPC updateRoleStatus request: statusCode={}", request.getStatusCode());
            com.lotecs.auth.grpc.RoleStatusResponse response = roleStatusServiceStub.updateRoleStatus(request.toProto());
            return RoleStatusResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC updateRoleStatus failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public DeleteRoleStatusResponse deleteRoleStatus(DeleteRoleStatusRequest request) {
        try {
            log.debug("gRPC deleteRoleStatus request: statusCode={}", request.getStatusCode());
            com.lotecs.auth.grpc.DeleteRoleStatusResponse response = roleStatusServiceStub.deleteRoleStatus(request.toProto());
            return DeleteRoleStatusResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC deleteRoleStatus failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }
}
