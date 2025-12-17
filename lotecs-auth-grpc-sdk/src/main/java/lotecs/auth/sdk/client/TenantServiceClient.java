package lotecs.auth.sdk.client;

import com.lotecs.auth.grpc.TenantServiceGrpc;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.sdk.dto.tenant.CreateTenantRequest;
import lotecs.auth.sdk.dto.tenant.DeleteTenantRequest;
import lotecs.auth.sdk.dto.tenant.DeleteTenantResponse;
import lotecs.auth.sdk.dto.tenant.GetTenantBySiteCodeRequest;
import lotecs.auth.sdk.dto.tenant.GetTenantRequest;
import lotecs.auth.sdk.dto.tenant.ListTenantsResponse;
import lotecs.auth.sdk.dto.tenant.PublishTenantRequest;
import lotecs.auth.sdk.dto.tenant.TenantResponse;
import lotecs.auth.sdk.dto.tenant.UnpublishTenantRequest;
import lotecs.auth.sdk.dto.tenant.UpdateTenantRequest;
import lotecs.auth.sdk.exception.AuthGrpcException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TenantServiceClient {

    @GrpcClient("lotecs-auth")
    private TenantServiceGrpc.TenantServiceBlockingStub tenantServiceStub;

    public TenantResponse getTenant(GetTenantRequest request) {
        try {
            log.debug("gRPC getTenant request: tenantId={}", request.getTenantId());
            com.lotecs.auth.grpc.TenantResponse response = tenantServiceStub.getTenant(request.toProto());
            return TenantResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC getTenant failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public TenantResponse getTenantBySiteCode(GetTenantBySiteCodeRequest request) {
        try {
            log.debug("gRPC getTenantBySiteCode request: siteCode={}", request.getSiteCode());
            com.lotecs.auth.grpc.TenantResponse response = tenantServiceStub.getTenantBySiteCode(request.toProto());
            return TenantResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC getTenantBySiteCode failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public ListTenantsResponse listTenants() {
        try {
            log.debug("gRPC listTenants request");
            com.lotecs.auth.grpc.ListTenantsResponse response = tenantServiceStub.listTenants(
                    com.lotecs.auth.grpc.ListTenantsRequest.newBuilder().build());
            return ListTenantsResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC listTenants failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public ListTenantsResponse listActiveTenants() {
        try {
            log.debug("gRPC listActiveTenants request");
            com.lotecs.auth.grpc.ListTenantsResponse response = tenantServiceStub.listActiveTenants(
                    com.lotecs.auth.grpc.ListActiveTenantsRequest.newBuilder().build());
            return ListTenantsResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC listActiveTenants failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public TenantResponse createTenant(CreateTenantRequest request) {
        try {
            log.debug("gRPC createTenant request: siteName={}, siteCode={}", request.getSiteName(), request.getSiteCode());
            com.lotecs.auth.grpc.TenantResponse response = tenantServiceStub.createTenant(request.toProto());
            return TenantResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC createTenant failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public TenantResponse updateTenant(UpdateTenantRequest request) {
        try {
            log.debug("gRPC updateTenant request: tenantId={}", request.getTenantId());
            com.lotecs.auth.grpc.TenantResponse response = tenantServiceStub.updateTenant(request.toProto());
            return TenantResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC updateTenant failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public DeleteTenantResponse deleteTenant(DeleteTenantRequest request) {
        try {
            log.debug("gRPC deleteTenant request: tenantId={}", request.getTenantId());
            com.lotecs.auth.grpc.DeleteTenantResponse response = tenantServiceStub.deleteTenant(request.toProto());
            return DeleteTenantResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC deleteTenant failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public TenantResponse publishTenant(PublishTenantRequest request) {
        try {
            log.debug("gRPC publishTenant request: tenantId={}", request.getTenantId());
            com.lotecs.auth.grpc.TenantResponse response = tenantServiceStub.publishTenant(request.toProto());
            return TenantResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC publishTenant failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }

    public TenantResponse unpublishTenant(UnpublishTenantRequest request) {
        try {
            log.debug("gRPC unpublishTenant request: tenantId={}", request.getTenantId());
            com.lotecs.auth.grpc.TenantResponse response = tenantServiceStub.unpublishTenant(request.toProto());
            return TenantResponse.fromProto(response);
        } catch (StatusRuntimeException e) {
            log.error("gRPC unpublishTenant failed: {}", e.getStatus(), e);
            throw AuthGrpcException.fromStatusRuntimeException(e);
        }
    }
}
