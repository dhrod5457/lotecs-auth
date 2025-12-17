package lotecs.auth.sdk.client;

import com.lotecs.auth.grpc.UserOrganizationServiceGrpc;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.sdk.dto.userorganization.DeleteAllUserOrganizationsRequest;
import lotecs.auth.sdk.dto.userorganization.DeleteUserOrganizationRequest;
import lotecs.auth.sdk.dto.userorganization.DeleteUserOrganizationResponse;
import lotecs.auth.sdk.dto.userorganization.GetOrganizationUsersRequest;
import lotecs.auth.sdk.dto.userorganization.GetUserOrganizationsRequest;
import lotecs.auth.sdk.dto.userorganization.ListUserOrganizationsResponse;
import lotecs.auth.sdk.dto.userorganization.SyncUserOrganizationRequest;
import lotecs.auth.sdk.dto.userorganization.UserOrganizationResponse;
import lotecs.auth.sdk.exception.AuthGrpcException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserOrganizationServiceClient {

    @GrpcClient("lotecs-auth")
    private UserOrganizationServiceGrpc.UserOrganizationServiceBlockingStub userOrganizationServiceStub;

    public ListUserOrganizationsResponse getUserOrganizations(GetUserOrganizationsRequest request) {
        try {
            log.debug("gRPC getUserOrganizations request: userId={}", request.getUserId());
            com.lotecs.auth.grpc.ListUserOrganizationsResponse response = userOrganizationServiceStub.getUserOrganizations(request.toProto());
            return ListUserOrganizationsResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC getUserOrganizations failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public ListUserOrganizationsResponse getActiveUserOrganizations(String userId) {
        try {
            log.debug("gRPC getActiveUserOrganizations request: userId={}", userId);
            com.lotecs.auth.grpc.ListUserOrganizationsResponse response = userOrganizationServiceStub.getActiveUserOrganizations(
                    com.lotecs.auth.grpc.GetActiveUserOrganizationsRequest.newBuilder()
                            .setUserId(userId != null ? userId : "")
                            .build());
            return ListUserOrganizationsResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC getActiveUserOrganizations failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public UserOrganizationResponse getPrimaryUserOrganization(String userId) {
        try {
            log.debug("gRPC getPrimaryUserOrganization request: userId={}", userId);
            com.lotecs.auth.grpc.UserOrganizationResponse response = userOrganizationServiceStub.getPrimaryUserOrganization(
                    com.lotecs.auth.grpc.GetPrimaryUserOrganizationRequest.newBuilder()
                            .setUserId(userId != null ? userId : "")
                            .build());
            return UserOrganizationResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC getPrimaryUserOrganization failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public ListUserOrganizationsResponse getOrganizationUsers(GetOrganizationUsersRequest request) {
        try {
            log.debug("gRPC getOrganizationUsers request: organizationId={}", request.getOrganizationId());
            com.lotecs.auth.grpc.ListUserOrganizationsResponse response = userOrganizationServiceStub.getOrganizationUsers(request.toProto());
            return ListUserOrganizationsResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC getOrganizationUsers failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public ListUserOrganizationsResponse getActiveOrganizationUsers(String organizationId) {
        try {
            log.debug("gRPC getActiveOrganizationUsers request: organizationId={}", organizationId);
            com.lotecs.auth.grpc.ListUserOrganizationsResponse response = userOrganizationServiceStub.getActiveOrganizationUsers(
                    com.lotecs.auth.grpc.GetActiveOrganizationUsersRequest.newBuilder()
                            .setOrganizationId(organizationId != null ? organizationId : "")
                            .build());
            return ListUserOrganizationsResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC getActiveOrganizationUsers failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public UserOrganizationResponse syncUserOrganization(SyncUserOrganizationRequest request) {
        try {
            log.debug("gRPC syncUserOrganization request: userId={}, organizationId={}", request.getUserId(), request.getOrganizationId());
            com.lotecs.auth.grpc.UserOrganizationResponse response = userOrganizationServiceStub.syncUserOrganization(request.toProto());
            return UserOrganizationResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC syncUserOrganization failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public DeleteUserOrganizationResponse deleteUserOrganization(DeleteUserOrganizationRequest request) {
        try {
            log.debug("gRPC deleteUserOrganization request: id={}", request.getId());
            com.lotecs.auth.grpc.DeleteUserOrganizationResponse response = userOrganizationServiceStub.deleteUserOrganization(request.toProto());
            return DeleteUserOrganizationResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC deleteUserOrganization failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public DeleteUserOrganizationResponse deleteAllUserOrganizations(DeleteAllUserOrganizationsRequest request) {
        try {
            log.debug("gRPC deleteAllUserOrganizations request: userId={}", request.getUserId());
            com.lotecs.auth.grpc.DeleteUserOrganizationResponse response = userOrganizationServiceStub.deleteAllUserOrganizations(request.toProto());
            return DeleteUserOrganizationResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC deleteAllUserOrganizations failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }
}
