package lotecs.auth.presentation.grpc.service.organization;

import com.lotecs.auth.grpc.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.application.organization.dto.OrganizationDto;
import lotecs.auth.application.organization.service.OrganizationService;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class OrganizationGrpcServiceImpl extends OrganizationServiceGrpc.OrganizationServiceImplBase {

    private final OrganizationService organizationService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void getOrganization(GetOrganizationRequest request, StreamObserver<OrganizationResponse> responseObserver) {
        log.debug("[gRPC] getOrganization 호출: organizationId={}", request.getOrganizationId());

        try {
            OrganizationDto organization = organizationService.getOrganization(request.getOrganizationId());

            OrganizationResponse response = OrganizationResponse.newBuilder()
                    .setOrganization(toOrganizationInfo(organization))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] getOrganization 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void listOrganizations(ListOrganizationsRequest request, StreamObserver<ListOrganizationsResponse> responseObserver) {
        log.debug("[gRPC] listOrganizations 호출: tenantId={}", request.getTenantId());

        try {
            List<OrganizationDto> organizations = organizationService.getOrganizationsByTenantId(request.getTenantId());

            ListOrganizationsResponse response = ListOrganizationsResponse.newBuilder()
                    .addAllOrganizations(organizations.stream().map(this::toOrganizationInfo).collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] listOrganizations 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void listActiveOrganizations(ListActiveOrganizationsRequest request, StreamObserver<ListOrganizationsResponse> responseObserver) {
        log.debug("[gRPC] listActiveOrganizations 호출: tenantId={}", request.getTenantId());

        try {
            List<OrganizationDto> organizations = organizationService.getActiveOrganizationsByTenantId(request.getTenantId());

            ListOrganizationsResponse response = ListOrganizationsResponse.newBuilder()
                    .addAllOrganizations(organizations.stream().map(this::toOrganizationInfo).collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] listActiveOrganizations 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void listRootOrganizations(ListRootOrganizationsRequest request, StreamObserver<ListOrganizationsResponse> responseObserver) {
        log.debug("[gRPC] listRootOrganizations 호출: tenantId={}", request.getTenantId());

        try {
            List<OrganizationDto> organizations = organizationService.getRootOrganizations(request.getTenantId());

            ListOrganizationsResponse response = ListOrganizationsResponse.newBuilder()
                    .addAllOrganizations(organizations.stream().map(this::toOrganizationInfo).collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] listRootOrganizations 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void listChildOrganizations(ListChildOrganizationsRequest request, StreamObserver<ListOrganizationsResponse> responseObserver) {
        log.debug("[gRPC] listChildOrganizations 호출: parentOrganizationId={}", request.getParentOrganizationId());

        try {
            List<OrganizationDto> organizations = organizationService.getChildOrganizations(request.getParentOrganizationId());

            ListOrganizationsResponse response = ListOrganizationsResponse.newBuilder()
                    .addAllOrganizations(organizations.stream().map(this::toOrganizationInfo).collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] listChildOrganizations 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void syncOrganization(SyncOrganizationRequest request, StreamObserver<OrganizationResponse> responseObserver) {
        log.info("[gRPC] syncOrganization 호출: organizationId={}", request.getOrganizationId());

        try {
            OrganizationDto dto = OrganizationDto.builder()
                    .tenantId(request.getTenantId())
                    .organizationId(request.getOrganizationId())
                    .organizationCode(request.getOrganizationCode())
                    .organizationName(request.getOrganizationName())
                    .organizationType(request.getOrganizationType())
                    .parentOrganizationId(request.getParentOrganizationId())
                    .orgLevel(request.getOrgLevel())
                    .displayOrder(request.getDisplayOrder())
                    .description(request.getDescription())
                    .active(request.getActive())
                    .build();

            OrganizationDto synced = organizationService.syncOrganization(dto, request.getSyncBy());

            OrganizationResponse response = OrganizationResponse.newBuilder()
                    .setOrganization(toOrganizationInfo(synced))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] syncOrganization 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void deleteOrganization(DeleteOrganizationRequest request, StreamObserver<DeleteOrganizationResponse> responseObserver) {
        log.info("[gRPC] deleteOrganization 호출: organizationId={}", request.getOrganizationId());

        try {
            organizationService.deleteOrganization(request.getOrganizationId());

            DeleteOrganizationResponse response = DeleteOrganizationResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Organization deleted successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] deleteOrganization 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    private OrganizationInfo toOrganizationInfo(OrganizationDto org) {
        OrganizationInfo.Builder builder = OrganizationInfo.newBuilder();

        if (org.getId() != null) builder.setId(org.getId());
        if (org.getTenantId() != null) builder.setTenantId(org.getTenantId());
        if (org.getOrganizationId() != null) builder.setOrganizationId(org.getOrganizationId());
        if (org.getOrganizationCode() != null) builder.setOrganizationCode(org.getOrganizationCode());
        if (org.getOrganizationName() != null) builder.setOrganizationName(org.getOrganizationName());
        if (org.getOrganizationType() != null) builder.setOrganizationType(org.getOrganizationType());
        if (org.getParentOrganizationId() != null) builder.setParentOrganizationId(org.getParentOrganizationId());
        if (org.getOrgLevel() != null) builder.setOrgLevel(org.getOrgLevel());
        if (org.getDisplayOrder() != null) builder.setDisplayOrder(org.getDisplayOrder());
        if (org.getDescription() != null) builder.setDescription(org.getDescription());
        if (org.getActive() != null) builder.setActive(org.getActive());
        if (org.getCreatedBy() != null) builder.setCreatedBy(org.getCreatedBy());
        if (org.getCreatedAt() != null) builder.setCreatedAt(org.getCreatedAt().format(FORMATTER));
        if (org.getUpdatedBy() != null) builder.setUpdatedBy(org.getUpdatedBy());
        if (org.getUpdatedAt() != null) builder.setUpdatedAt(org.getUpdatedAt().format(FORMATTER));

        return builder.build();
    }
}
