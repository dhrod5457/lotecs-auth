package lotecs.auth.sdk.client;

import com.lotecs.auth.grpc.OrganizationServiceGrpc;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.sdk.dto.organization.DeleteOrganizationRequest;
import lotecs.auth.sdk.dto.organization.DeleteOrganizationResponse;
import lotecs.auth.sdk.dto.organization.GetOrganizationRequest;
import lotecs.auth.sdk.dto.organization.ListChildOrganizationsRequest;
import lotecs.auth.sdk.dto.organization.ListOrganizationsRequest;
import lotecs.auth.sdk.dto.organization.ListOrganizationsResponse;
import lotecs.auth.sdk.dto.organization.OrganizationResponse;
import lotecs.auth.sdk.dto.organization.SyncOrganizationRequest;
import lotecs.auth.sdk.exception.AuthGrpcException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrganizationServiceClient {

    @GrpcClient("lotecs-auth")
    private OrganizationServiceGrpc.OrganizationServiceBlockingStub organizationServiceStub;

    public OrganizationResponse getOrganization(GetOrganizationRequest request) {
        try {
            log.debug("gRPC getOrganization request: organizationId={}", request.getOrganizationId());
            com.lotecs.auth.grpc.OrganizationResponse response = organizationServiceStub.getOrganization(request.toProto());
            return OrganizationResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC getOrganization failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public ListOrganizationsResponse listOrganizations(ListOrganizationsRequest request) {
        try {
            log.debug("gRPC listOrganizations request: tenantId={}", request.getTenantId());
            com.lotecs.auth.grpc.ListOrganizationsResponse response = organizationServiceStub.listOrganizations(request.toProto());
            return ListOrganizationsResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC listOrganizations failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public ListOrganizationsResponse listActiveOrganizations(String tenantId) {
        try {
            log.debug("gRPC listActiveOrganizations request: tenantId={}", tenantId);
            com.lotecs.auth.grpc.ListOrganizationsResponse response = organizationServiceStub.listActiveOrganizations(
                    com.lotecs.auth.grpc.ListActiveOrganizationsRequest.newBuilder()
                            .setTenantId(tenantId != null ? tenantId : "")
                            .build());
            return ListOrganizationsResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC listActiveOrganizations failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public ListOrganizationsResponse listRootOrganizations(String tenantId) {
        try {
            log.debug("gRPC listRootOrganizations request: tenantId={}", tenantId);
            com.lotecs.auth.grpc.ListOrganizationsResponse response = organizationServiceStub.listRootOrganizations(
                    com.lotecs.auth.grpc.ListRootOrganizationsRequest.newBuilder()
                            .setTenantId(tenantId != null ? tenantId : "")
                            .build());
            return ListOrganizationsResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC listRootOrganizations failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public ListOrganizationsResponse listChildOrganizations(ListChildOrganizationsRequest request) {
        try {
            log.debug("gRPC listChildOrganizations request: parentOrganizationId={}", request.getParentOrganizationId());
            com.lotecs.auth.grpc.ListOrganizationsResponse response = organizationServiceStub.listChildOrganizations(request.toProto());
            return ListOrganizationsResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC listChildOrganizations failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public OrganizationResponse syncOrganization(SyncOrganizationRequest request) {
        try {
            log.debug("gRPC syncOrganization request: organizationId={}, tenantId={}", request.getOrganizationId(), request.getTenantId());
            com.lotecs.auth.grpc.OrganizationResponse response = organizationServiceStub.syncOrganization(request.toProto());
            return OrganizationResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC syncOrganization failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public DeleteOrganizationResponse deleteOrganization(DeleteOrganizationRequest request) {
        try {
            log.debug("gRPC deleteOrganization request: organizationId={}", request.getOrganizationId());
            com.lotecs.auth.grpc.DeleteOrganizationResponse response = organizationServiceStub.deleteOrganization(request.toProto());
            return DeleteOrganizationResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC deleteOrganization failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }
}
