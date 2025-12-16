package lotecs.auth.presentation.grpc.service.rolestatus;

import com.lotecs.auth.grpc.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.application.user.dto.RoleStatusDto;
import lotecs.auth.application.user.service.RoleStatusService;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RoleStatusGrpcServiceImpl extends RoleStatusServiceGrpc.RoleStatusServiceImplBase {

    private final RoleStatusService roleStatusService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void getRoleStatus(GetRoleStatusRequest request, StreamObserver<RoleStatusResponse> responseObserver) {
        log.debug("[gRPC] getRoleStatus 호출: statusCode={}", request.getStatusCode());

        try {
            RoleStatusDto roleStatus = roleStatusService.getRoleStatus(request.getStatusCode());

            RoleStatusResponse response = RoleStatusResponse.newBuilder()
                    .setRoleStatus(toRoleStatusInfo(roleStatus))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] getRoleStatus 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void listRoleStatuses(ListRoleStatusesRequest request, StreamObserver<ListRoleStatusesResponse> responseObserver) {
        log.debug("[gRPC] listRoleStatuses 호출");

        try {
            List<RoleStatusDto> roleStatuses = roleStatusService.getAllRoleStatuses();

            ListRoleStatusesResponse response = ListRoleStatusesResponse.newBuilder()
                    .addAllRoleStatuses(roleStatuses.stream().map(this::toRoleStatusInfo).collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] listRoleStatuses 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void listActiveRoleStatuses(ListActiveRoleStatusesRequest request, StreamObserver<ListRoleStatusesResponse> responseObserver) {
        log.debug("[gRPC] listActiveRoleStatuses 호출");

        try {
            List<RoleStatusDto> roleStatuses = roleStatusService.getActiveRoleStatuses();

            ListRoleStatusesResponse response = ListRoleStatusesResponse.newBuilder()
                    .addAllRoleStatuses(roleStatuses.stream().map(this::toRoleStatusInfo).collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] listActiveRoleStatuses 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void listRoleStatusesByCategory(ListRoleStatusesByCategoryRequest request, StreamObserver<ListRoleStatusesResponse> responseObserver) {
        log.debug("[gRPC] listRoleStatusesByCategory 호출: roleCategory={}", request.getRoleCategory());

        try {
            List<RoleStatusDto> roleStatuses = roleStatusService.getRoleStatusesByCategory(request.getRoleCategory());

            ListRoleStatusesResponse response = ListRoleStatusesResponse.newBuilder()
                    .addAllRoleStatuses(roleStatuses.stream().map(this::toRoleStatusInfo).collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] listRoleStatusesByCategory 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void createRoleStatus(CreateRoleStatusRequest request, StreamObserver<RoleStatusResponse> responseObserver) {
        log.info("[gRPC] createRoleStatus 호출: statusCode={}", request.getStatusCode());

        try {
            RoleStatusDto dto = RoleStatusDto.builder()
                    .statusCode(request.getStatusCode())
                    .statusName(request.getStatusName())
                    .roleCategory(request.getRoleCategory())
                    .description(request.getDescription())
                    .isActive(request.getIsActive())
                    .sortOrder(request.getSortOrder())
                    .isDefault(request.getIsDefault())
                    .build();

            RoleStatusDto created = roleStatusService.createRoleStatus(dto, request.getCreatedBy());

            RoleStatusResponse response = RoleStatusResponse.newBuilder()
                    .setRoleStatus(toRoleStatusInfo(created))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] createRoleStatus 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void updateRoleStatus(UpdateRoleStatusRequest request, StreamObserver<RoleStatusResponse> responseObserver) {
        log.info("[gRPC] updateRoleStatus 호출: statusCode={}", request.getStatusCode());

        try {
            RoleStatusDto dto = RoleStatusDto.builder()
                    .statusName(request.getStatusName())
                    .roleCategory(request.getRoleCategory())
                    .description(request.getDescription())
                    .isActive(request.getIsActive())
                    .sortOrder(request.getSortOrder())
                    .isDefault(request.getIsDefault())
                    .build();

            RoleStatusDto updated = roleStatusService.updateRoleStatus(request.getStatusCode(), dto, request.getUpdatedBy());

            RoleStatusResponse response = RoleStatusResponse.newBuilder()
                    .setRoleStatus(toRoleStatusInfo(updated))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] updateRoleStatus 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void deleteRoleStatus(DeleteRoleStatusRequest request, StreamObserver<DeleteRoleStatusResponse> responseObserver) {
        log.info("[gRPC] deleteRoleStatus 호출: statusCode={}", request.getStatusCode());

        try {
            roleStatusService.deleteRoleStatus(request.getStatusCode());

            DeleteRoleStatusResponse response = DeleteRoleStatusResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Role status deleted successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] deleteRoleStatus 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    private RoleStatusInfo toRoleStatusInfo(RoleStatusDto rs) {
        RoleStatusInfo.Builder builder = RoleStatusInfo.newBuilder();

        if (rs.getId() != null) builder.setId(rs.getId());
        if (rs.getStatusCode() != null) builder.setStatusCode(rs.getStatusCode());
        if (rs.getStatusName() != null) builder.setStatusName(rs.getStatusName());
        if (rs.getRoleCategory() != null) builder.setRoleCategory(rs.getRoleCategory());
        if (rs.getDescription() != null) builder.setDescription(rs.getDescription());
        if (rs.getIsActive() != null) builder.setIsActive(rs.getIsActive());
        if (rs.getSortOrder() != null) builder.setSortOrder(rs.getSortOrder());
        if (rs.getIsDefault() != null) builder.setIsDefault(rs.getIsDefault());
        if (rs.getCreatedBy() != null) builder.setCreatedBy(rs.getCreatedBy());
        if (rs.getCreatedAt() != null) builder.setCreatedAt(rs.getCreatedAt().format(FORMATTER));
        if (rs.getUpdatedBy() != null) builder.setUpdatedBy(rs.getUpdatedBy());
        if (rs.getUpdatedAt() != null) builder.setUpdatedAt(rs.getUpdatedAt().format(FORMATTER));

        return builder.build();
    }
}
