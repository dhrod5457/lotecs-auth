package lotecs.auth.presentation.grpc.service.permission;

import com.lotecs.auth.grpc.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.application.permission.dto.PermissionDto;
import lotecs.auth.application.permission.service.PermissionAppService;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class PermissionGrpcServiceImpl extends PermissionServiceGrpc.PermissionServiceImplBase {

    private final PermissionAppService permissionAppService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void getPermission(GetPermissionRequest request, StreamObserver<PermissionResponse> responseObserver) {
        log.debug("[gRPC] getPermission 호출: permissionId={}, tenantId={}", request.getPermissionId(), request.getTenantId());

        try {
            PermissionDto permission = permissionAppService.getPermission(request.getPermissionId(), request.getTenantId());

            PermissionResponse response = PermissionResponse.newBuilder()
                    .setPermission(toPermissionInfo(permission))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] getPermission 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void listPermissions(ListPermissionsRequest request, StreamObserver<ListPermissionsResponse> responseObserver) {
        log.debug("[gRPC] listPermissions 호출: tenantId={}", request.getTenantId());

        try {
            List<PermissionDto> permissions = permissionAppService.listPermissions(request.getTenantId());

            ListPermissionsResponse response = ListPermissionsResponse.newBuilder()
                    .addAllPermissions(permissions.stream().map(this::toPermissionInfo).collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] listPermissions 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void createPermission(CreatePermissionRequest request, StreamObserver<PermissionResponse> responseObserver) {
        log.debug("[gRPC] createPermission 호출: permissionName={}, tenantId={}", request.getPermissionName(), request.getTenantId());

        try {
            lotecs.auth.application.permission.dto.CreatePermissionRequest createRequest =
                    lotecs.auth.application.permission.dto.CreatePermissionRequest.builder()
                            .tenantId(request.getTenantId())
                            .permissionName(request.getPermissionName())
                            .resource(request.getResourceType())
                            .action(request.getAction())
                            .description(request.getDescription())
                            .createdBy(request.getCreatedBy())
                            .build();

            PermissionDto permission = permissionAppService.createPermission(createRequest);

            PermissionResponse response = PermissionResponse.newBuilder()
                    .setPermission(toPermissionInfo(permission))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] createPermission 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void updatePermission(UpdatePermissionRequest request, StreamObserver<PermissionResponse> responseObserver) {
        log.debug("[gRPC] updatePermission 호출: permissionId={}, tenantId={}", request.getPermissionId(), request.getTenantId());

        try {
            lotecs.auth.application.permission.dto.UpdatePermissionRequest updateRequest =
                    lotecs.auth.application.permission.dto.UpdatePermissionRequest.builder()
                            .permissionId(request.getPermissionId())
                            .tenantId(request.getTenantId())
                            .description(request.getDescription())
                            .updatedBy(request.getUpdatedBy())
                            .build();

            PermissionDto permission = permissionAppService.updatePermission(updateRequest);

            PermissionResponse response = PermissionResponse.newBuilder()
                    .setPermission(toPermissionInfo(permission))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] updatePermission 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void deletePermission(DeletePermissionRequest request, StreamObserver<DeletePermissionResponse> responseObserver) {
        log.debug("[gRPC] deletePermission 호출: permissionId={}, tenantId={}", request.getPermissionId(), request.getTenantId());

        try {
            permissionAppService.deletePermission(request.getPermissionId(), request.getTenantId());

            DeletePermissionResponse response = DeletePermissionResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("권한이 삭제되었습니다.")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] deletePermission 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getRolePermissions(GetRolePermissionsRequest request, StreamObserver<ListPermissionsResponse> responseObserver) {
        log.debug("[gRPC] getRolePermissions 호출: roleId={}, tenantId={}", request.getRoleId(), request.getTenantId());

        try {
            List<PermissionDto> permissions = permissionAppService.getRolePermissions(request.getRoleId(), request.getTenantId());

            ListPermissionsResponse response = ListPermissionsResponse.newBuilder()
                    .addAllPermissions(permissions.stream().map(this::toPermissionInfo).collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] getRolePermissions 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void assignPermissionsToRole(AssignPermissionsToRoleRequest request, StreamObserver<AssignPermissionsToRoleResponse> responseObserver) {
        log.debug("[gRPC] assignPermissionsToRole 호출: roleId={}, permissionCount={}, tenantId={}",
                request.getRoleId(), request.getPermissionIdsList().size(), request.getTenantId());

        try {
            permissionAppService.assignPermissionsToRole(
                    request.getRoleId(),
                    request.getPermissionIdsList(),
                    request.getTenantId(),
                    request.getAssignedBy()
            );

            AssignPermissionsToRoleResponse response = AssignPermissionsToRoleResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("권한이 역할에 할당되었습니다.")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] assignPermissionsToRole 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void revokePermissionFromRole(RevokePermissionFromRoleRequest request, StreamObserver<RevokePermissionFromRoleResponse> responseObserver) {
        log.debug("[gRPC] revokePermissionFromRole 호출: roleId={}, permissionId={}, tenantId={}",
                request.getRoleId(), request.getPermissionId(), request.getTenantId());

        try {
            permissionAppService.revokePermissionFromRole(
                    request.getRoleId(),
                    request.getPermissionId(),
                    request.getTenantId()
            );

            RevokePermissionFromRoleResponse response = RevokePermissionFromRoleResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("권한이 역할에서 회수되었습니다.")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] revokePermissionFromRole 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    private PermissionInfo toPermissionInfo(PermissionDto dto) {
        PermissionInfo.Builder builder = PermissionInfo.newBuilder()
                .setPermissionId(dto.getPermissionId() != null ? dto.getPermissionId() : "")
                .setTenantId(dto.getTenantId() != null ? dto.getTenantId() : "")
                .setPermissionName(dto.getPermissionName() != null ? dto.getPermissionName() : "")
                .setResourceType(dto.getResource() != null ? dto.getResource() : "")
                .setAction(dto.getAction() != null ? dto.getAction() : "")
                .setDescription(dto.getDescription() != null ? dto.getDescription() : "")
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
