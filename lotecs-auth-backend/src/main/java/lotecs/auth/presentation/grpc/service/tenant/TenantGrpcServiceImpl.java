package lotecs.auth.presentation.grpc.service.tenant;

import com.lotecs.auth.grpc.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.application.tenant.dto.TenantDto;
import lotecs.auth.application.tenant.service.TenantService;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class TenantGrpcServiceImpl extends TenantServiceGrpc.TenantServiceImplBase {

    private final TenantService tenantService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void getTenant(GetTenantRequest request, StreamObserver<TenantResponse> responseObserver) {
        log.debug("[gRPC] getTenant 호출: tenantId={}", request.getTenantId());

        try {
            TenantDto tenant = tenantService.getTenant(request.getTenantId());

            TenantResponse response = TenantResponse.newBuilder()
                    .setTenant(toTenantInfo(tenant))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] getTenant 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getTenantBySiteCode(GetTenantBySiteCodeRequest request, StreamObserver<TenantResponse> responseObserver) {
        log.debug("[gRPC] getTenantBySiteCode 호출: siteCode={}", request.getSiteCode());

        try {
            TenantDto tenant = tenantService.getTenantBySiteCode(request.getSiteCode());

            TenantResponse response = TenantResponse.newBuilder()
                    .setTenant(toTenantInfo(tenant))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] getTenantBySiteCode 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void listTenants(ListTenantsRequest request, StreamObserver<ListTenantsResponse> responseObserver) {
        log.debug("[gRPC] listTenants 호출");

        try {
            List<TenantDto> tenants = tenantService.getAllTenants();

            ListTenantsResponse response = ListTenantsResponse.newBuilder()
                    .addAllTenants(tenants.stream().map(this::toTenantInfo).collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] listTenants 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void listActiveTenants(ListActiveTenantsRequest request, StreamObserver<ListTenantsResponse> responseObserver) {
        log.debug("[gRPC] listActiveTenants 호출");

        try {
            List<TenantDto> tenants = tenantService.getActiveTenants();

            ListTenantsResponse response = ListTenantsResponse.newBuilder()
                    .addAllTenants(tenants.stream().map(this::toTenantInfo).collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] listActiveTenants 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void createTenant(CreateTenantRequest request, StreamObserver<TenantResponse> responseObserver) {
        log.info("[gRPC] createTenant 호출: siteCode={}", request.getSiteCode());

        try {
            TenantDto tenantDto = TenantDto.builder()
                    .siteName(request.getSiteName())
                    .siteCode(request.getSiteCode())
                    .description(request.getDescription())
                    .primaryDomain(request.getPrimaryDomain())
                    .additionalDomains(request.getAdditionalDomains())
                    .siteTitle(request.getSiteTitle())
                    .siteDescription(request.getSiteDescription())
                    .themeName(request.getThemeName())
                    .defaultLanguage(request.getDefaultLanguage())
                    .timezone(request.getTimezone())
                    .ownerEmail(request.getOwnerEmail())
                    .adminEmail(request.getAdminEmail())
                    .contactPhone(request.getContactPhone())
                    .parentTenantId(request.getParentTenantId())
                    .siteLevel(request.getSiteLevel())
                    .maxContentItems(request.getMaxContentItems())
                    .maxStorageMb(request.getMaxStorageMb())
                    .maxUsers(request.getMaxUsers())
                    .features(request.getFeatures())
                    .settings(request.getSettings())
                    .build();

            TenantDto created = tenantService.createTenant(tenantDto, request.getCreatedBy());

            TenantResponse response = TenantResponse.newBuilder()
                    .setTenant(toTenantInfo(created))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] createTenant 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void updateTenant(UpdateTenantRequest request, StreamObserver<TenantResponse> responseObserver) {
        log.info("[gRPC] updateTenant 호출: tenantId={}", request.getTenantId());

        try {
            TenantDto tenantDto = TenantDto.builder()
                    .siteName(request.getSiteName())
                    .description(request.getDescription())
                    .primaryDomain(request.getPrimaryDomain())
                    .additionalDomains(request.getAdditionalDomains())
                    .siteTitle(request.getSiteTitle())
                    .siteDescription(request.getSiteDescription())
                    .themeName(request.getThemeName())
                    .defaultLanguage(request.getDefaultLanguage())
                    .timezone(request.getTimezone())
                    .ownerEmail(request.getOwnerEmail())
                    .adminEmail(request.getAdminEmail())
                    .contactPhone(request.getContactPhone())
                    .maxContentItems(request.getMaxContentItems())
                    .maxStorageMb(request.getMaxStorageMb())
                    .maxUsers(request.getMaxUsers())
                    .features(request.getFeatures())
                    .settings(request.getSettings())
                    .build();

            TenantDto updated = tenantService.updateTenant(request.getTenantId(), tenantDto, request.getUpdatedBy());

            TenantResponse response = TenantResponse.newBuilder()
                    .setTenant(toTenantInfo(updated))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] updateTenant 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void deleteTenant(DeleteTenantRequest request, StreamObserver<DeleteTenantResponse> responseObserver) {
        log.info("[gRPC] deleteTenant 호출: tenantId={}", request.getTenantId());

        try {
            tenantService.deleteTenant(request.getTenantId());

            DeleteTenantResponse response = DeleteTenantResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Tenant deleted successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] deleteTenant 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void publishTenant(PublishTenantRequest request, StreamObserver<TenantResponse> responseObserver) {
        log.info("[gRPC] publishTenant 호출: tenantId={}", request.getTenantId());

        try {
            // TODO: TenantService에 publish 메서드 추가 필요
            TenantDto tenant = tenantService.getTenant(request.getTenantId());

            TenantResponse response = TenantResponse.newBuilder()
                    .setTenant(toTenantInfo(tenant))
                    .setErrorMessage("Publish not implemented yet")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] publishTenant 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void unpublishTenant(UnpublishTenantRequest request, StreamObserver<TenantResponse> responseObserver) {
        log.info("[gRPC] unpublishTenant 호출: tenantId={}", request.getTenantId());

        try {
            // TODO: TenantService에 unpublish 메서드 추가 필요
            TenantDto tenant = tenantService.getTenant(request.getTenantId());

            TenantResponse response = TenantResponse.newBuilder()
                    .setTenant(toTenantInfo(tenant))
                    .setErrorMessage("Unpublish not implemented yet")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] unpublishTenant 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    private TenantInfo toTenantInfo(TenantDto tenant) {
        TenantInfo.Builder builder = TenantInfo.newBuilder();

        if (tenant.getTenantId() != null) builder.setTenantId(tenant.getTenantId());
        if (tenant.getSiteName() != null) builder.setSiteName(tenant.getSiteName());
        if (tenant.getSiteCode() != null) builder.setSiteCode(tenant.getSiteCode());
        if (tenant.getPrimaryDomain() != null) builder.setPrimaryDomain(tenant.getPrimaryDomain());
        if (tenant.getAdditionalDomains() != null) builder.setAdditionalDomains(tenant.getAdditionalDomains());
        if (tenant.getDescription() != null) builder.setDescription(tenant.getDescription());
        if (tenant.getSiteTitle() != null) builder.setSiteTitle(tenant.getSiteTitle());
        if (tenant.getSiteDescription() != null) builder.setSiteDescription(tenant.getSiteDescription());
        if (tenant.getThemeName() != null) builder.setThemeName(tenant.getThemeName());
        if (tenant.getDefaultLanguage() != null) builder.setDefaultLanguage(tenant.getDefaultLanguage());
        if (tenant.getTimezone() != null) builder.setTimezone(tenant.getTimezone());
        if (tenant.getOwnerEmail() != null) builder.setOwnerEmail(tenant.getOwnerEmail());
        if (tenant.getAdminEmail() != null) builder.setAdminEmail(tenant.getAdminEmail());
        if (tenant.getContactPhone() != null) builder.setContactPhone(tenant.getContactPhone());
        if (tenant.getParentTenantId() != null) builder.setParentTenantId(tenant.getParentTenantId());
        if (tenant.getSiteLevel() != null) builder.setSiteLevel(tenant.getSiteLevel());
        if (tenant.getMaxContentItems() != null) builder.setMaxContentItems(tenant.getMaxContentItems());
        if (tenant.getMaxStorageMb() != null) builder.setMaxStorageMb(tenant.getMaxStorageMb());
        if (tenant.getMaxUsers() != null) builder.setMaxUsers(tenant.getMaxUsers());
        if (tenant.getFeatures() != null) builder.setFeatures(tenant.getFeatures());
        if (tenant.getSettings() != null) builder.setSettings(tenant.getSettings());
        if (tenant.getStatus() != null) builder.setStatus(tenant.getStatus());
        if (tenant.getPublishedAt() != null) builder.setPublishedAt(tenant.getPublishedAt().format(FORMATTER));
        if (tenant.getUnpublishedAt() != null) builder.setUnpublishedAt(tenant.getUnpublishedAt().format(FORMATTER));
        if (tenant.getSubscriptionPlanCode() != null) builder.setSubscriptionPlanCode(tenant.getSubscriptionPlanCode());
        if (tenant.getPlanStartDate() != null) builder.setPlanStartDate(tenant.getPlanStartDate().format(FORMATTER));
        if (tenant.getPlanEndDate() != null) builder.setPlanEndDate(tenant.getPlanEndDate().format(FORMATTER));
        if (tenant.getVersion() != null) builder.setVersion(tenant.getVersion());
        if (tenant.getCreatedBy() != null) builder.setCreatedBy(tenant.getCreatedBy());
        if (tenant.getCreatedAt() != null) builder.setCreatedAt(tenant.getCreatedAt().format(FORMATTER));
        if (tenant.getUpdatedBy() != null) builder.setUpdatedBy(tenant.getUpdatedBy());
        if (tenant.getUpdatedAt() != null) builder.setUpdatedAt(tenant.getUpdatedAt().format(FORMATTER));

        return builder.build();
    }
}
