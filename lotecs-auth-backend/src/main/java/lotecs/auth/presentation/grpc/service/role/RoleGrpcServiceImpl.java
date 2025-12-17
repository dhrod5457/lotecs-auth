package lotecs.auth.presentation.grpc.service.role;

import com.lotecs.auth.grpc.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.application.role.dto.RoleDto;
import lotecs.auth.application.role.service.RoleAppService;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RoleGrpcServiceImpl extends RoleServiceGrpc.RoleServiceImplBase {

    private final RoleAppService roleAppService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void getRole(GetRoleRequest request, StreamObserver<RoleResponse> responseObserver) {
        log.debug("[gRPC] getRole 호출: roleId={}, tenantId={}", request.getRoleId(), request.getTenantId());

        try {
            RoleDto role = roleAppService.getRole(request.getRoleId(), request.getTenantId());

            RoleResponse response = RoleResponse.newBuilder()
                    .setRole(toRoleInfo(role))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] getRole 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getRoleByName(GetRoleByNameRequest request, StreamObserver<RoleResponse> responseObserver) {
        log.debug("[gRPC] getRoleByName 호출: roleName={}, tenantId={}", request.getRoleName(), request.getTenantId());

        try {
            RoleDto role = roleAppService.getRoleByName(request.getRoleName(), request.getTenantId());

            RoleResponse response = RoleResponse.newBuilder()
                    .setRole(toRoleInfo(role))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] getRoleByName 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void listRoles(ListRolesRequest request, StreamObserver<ListRolesResponse> responseObserver) {
        log.debug("[gRPC] listRoles 호출: tenantId={}", request.getTenantId());

        try {
            List<RoleDto> roles = roleAppService.listRoles(request.getTenantId());

            ListRolesResponse response = ListRolesResponse.newBuilder()
                    .addAllRoles(roles.stream().map(this::toRoleInfo).collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] listRoles 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void createRole(CreateRoleRequest request, StreamObserver<RoleResponse> responseObserver) {
        log.debug("[gRPC] createRole 호출: roleName={}, tenantId={}", request.getRoleName(), request.getTenantId());

        try {
            lotecs.auth.application.role.dto.CreateRoleRequest createRequest =
                    lotecs.auth.application.role.dto.CreateRoleRequest.builder()
                            .tenantId(request.getTenantId())
                            .roleName(request.getRoleName())
                            .displayName(request.getDisplayName())
                            .description(request.getDescription())
                            .priority(request.getPriority())
                            .createdBy(request.getCreatedBy())
                            .build();

            RoleDto role = roleAppService.createRole(createRequest);

            RoleResponse response = RoleResponse.newBuilder()
                    .setRole(toRoleInfo(role))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] createRole 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void updateRole(UpdateRoleRequest request, StreamObserver<RoleResponse> responseObserver) {
        log.debug("[gRPC] updateRole 호출: roleId={}, tenantId={}", request.getRoleId(), request.getTenantId());

        try {
            lotecs.auth.application.role.dto.UpdateRoleRequest updateRequest =
                    lotecs.auth.application.role.dto.UpdateRoleRequest.builder()
                            .roleId(request.getRoleId())
                            .tenantId(request.getTenantId())
                            .displayName(request.getDisplayName())
                            .description(request.getDescription())
                            .priority(request.getPriority())
                            .updatedBy(request.getUpdatedBy())
                            .build();

            RoleDto role = roleAppService.updateRole(updateRequest);

            RoleResponse response = RoleResponse.newBuilder()
                    .setRole(toRoleInfo(role))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] updateRole 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void deleteRole(DeleteRoleRequest request, StreamObserver<DeleteRoleResponse> responseObserver) {
        log.debug("[gRPC] deleteRole 호출: roleId={}, tenantId={}", request.getRoleId(), request.getTenantId());

        try {
            roleAppService.deleteRole(request.getRoleId(), request.getTenantId());

            DeleteRoleResponse response = DeleteRoleResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("역할이 삭제되었습니다.")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] deleteRole 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getUserRoles(GetUserRolesRequest request, StreamObserver<ListRolesResponse> responseObserver) {
        log.debug("[gRPC] getUserRoles 호출: userId={}, tenantId={}", request.getUserId(), request.getTenantId());

        try {
            List<RoleDto> roles = roleAppService.getUserRoles(request.getUserId(), request.getTenantId());

            ListRolesResponse response = ListRolesResponse.newBuilder()
                    .addAllRoles(roles.stream().map(this::toRoleInfo).collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] getUserRoles 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    private RoleInfo toRoleInfo(RoleDto dto) {
        RoleInfo.Builder builder = RoleInfo.newBuilder()
                .setRoleId(dto.getRoleId() != null ? dto.getRoleId() : "")
                .setTenantId(dto.getTenantId() != null ? dto.getTenantId() : "")
                .setRoleName(dto.getRoleName() != null ? dto.getRoleName() : "")
                .setDisplayName(dto.getDisplayName() != null ? dto.getDisplayName() : "")
                .setDescription(dto.getDescription() != null ? dto.getDescription() : "")
                .setPriority(dto.getPriority())
                .setCreatedBy(dto.getCreatedBy() != null ? dto.getCreatedBy() : "")
                .setUpdatedBy(dto.getUpdatedBy() != null ? dto.getUpdatedBy() : "");

        if (dto.getCreatedAt() != null) {
            builder.setCreatedAt(dto.getCreatedAt().format(FORMATTER));
        }
        if (dto.getUpdatedAt() != null) {
            builder.setUpdatedAt(dto.getUpdatedAt().format(FORMATTER));
        }

        return builder.build();
    }
}
