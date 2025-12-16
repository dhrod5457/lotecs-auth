package lotecs.auth.presentation.grpc.service.organization;

import com.lotecs.auth.grpc.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.application.organization.dto.UserOrganizationDto;
import lotecs.auth.application.organization.service.UserOrganizationService;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserOrganizationGrpcServiceImpl extends UserOrganizationServiceGrpc.UserOrganizationServiceImplBase {

    private final UserOrganizationService userOrganizationService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void getUserOrganizations(GetUserOrganizationsRequest request, StreamObserver<ListUserOrganizationsResponse> responseObserver) {
        log.debug("[gRPC] getUserOrganizations 호출: userId={}", request.getUserId());

        try {
            List<UserOrganizationDto> userOrganizations = userOrganizationService.getUserOrganizations(request.getUserId());

            ListUserOrganizationsResponse response = ListUserOrganizationsResponse.newBuilder()
                    .addAllUserOrganizations(userOrganizations.stream().map(this::toUserOrganizationInfo).collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] getUserOrganizations 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getActiveUserOrganizations(GetActiveUserOrganizationsRequest request, StreamObserver<ListUserOrganizationsResponse> responseObserver) {
        log.debug("[gRPC] getActiveUserOrganizations 호출: userId={}", request.getUserId());

        try {
            List<UserOrganizationDto> userOrganizations = userOrganizationService.getActiveUserOrganizations(request.getUserId());

            ListUserOrganizationsResponse response = ListUserOrganizationsResponse.newBuilder()
                    .addAllUserOrganizations(userOrganizations.stream().map(this::toUserOrganizationInfo).collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] getActiveUserOrganizations 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getPrimaryUserOrganization(GetPrimaryUserOrganizationRequest request, StreamObserver<UserOrganizationResponse> responseObserver) {
        log.debug("[gRPC] getPrimaryUserOrganization 호출: userId={}", request.getUserId());

        try {
            UserOrganizationDto userOrganization = userOrganizationService.getPrimaryUserOrganization(request.getUserId());

            UserOrganizationResponse response = UserOrganizationResponse.newBuilder()
                    .setUserOrganization(toUserOrganizationInfo(userOrganization))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] getPrimaryUserOrganization 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getOrganizationUsers(GetOrganizationUsersRequest request, StreamObserver<ListUserOrganizationsResponse> responseObserver) {
        log.debug("[gRPC] getOrganizationUsers 호출: organizationId={}", request.getOrganizationId());

        try {
            List<UserOrganizationDto> userOrganizations = userOrganizationService.getOrganizationUsers(request.getOrganizationId());

            ListUserOrganizationsResponse response = ListUserOrganizationsResponse.newBuilder()
                    .addAllUserOrganizations(userOrganizations.stream().map(this::toUserOrganizationInfo).collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] getOrganizationUsers 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getActiveOrganizationUsers(GetActiveOrganizationUsersRequest request, StreamObserver<ListUserOrganizationsResponse> responseObserver) {
        log.debug("[gRPC] getActiveOrganizationUsers 호출: organizationId={}", request.getOrganizationId());

        try {
            List<UserOrganizationDto> userOrganizations = userOrganizationService.getActiveOrganizationUsers(request.getOrganizationId());

            ListUserOrganizationsResponse response = ListUserOrganizationsResponse.newBuilder()
                    .addAllUserOrganizations(userOrganizations.stream().map(this::toUserOrganizationInfo).collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] getActiveOrganizationUsers 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void syncUserOrganization(SyncUserOrganizationRequest request, StreamObserver<UserOrganizationResponse> responseObserver) {
        log.info("[gRPC] syncUserOrganization 호출: userId={}, organizationId={}", request.getUserId(), request.getOrganizationId());

        try {
            UserOrganizationDto dto = UserOrganizationDto.builder()
                    .tenantId(request.getTenantId())
                    .userId(request.getUserId())
                    .organizationId(request.getOrganizationId())
                    .roleId(request.getRoleId())
                    .isPrimary(request.getIsPrimary())
                    .position(request.getPosition())
                    .startDate(parseDate(request.getStartDate()))
                    .endDate(parseDate(request.getEndDate()))
                    .active(request.getActive())
                    .build();

            UserOrganizationDto synced = userOrganizationService.syncUserOrganization(dto, request.getSyncBy());

            UserOrganizationResponse response = UserOrganizationResponse.newBuilder()
                    .setUserOrganization(toUserOrganizationInfo(synced))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] syncUserOrganization 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void deleteUserOrganization(DeleteUserOrganizationRequest request, StreamObserver<DeleteUserOrganizationResponse> responseObserver) {
        log.info("[gRPC] deleteUserOrganization 호출: id={}", request.getId());

        try {
            userOrganizationService.deleteUserOrganization(request.getId());

            DeleteUserOrganizationResponse response = DeleteUserOrganizationResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("User organization deleted successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] deleteUserOrganization 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void deleteAllUserOrganizations(DeleteAllUserOrganizationsRequest request, StreamObserver<DeleteUserOrganizationResponse> responseObserver) {
        log.info("[gRPC] deleteAllUserOrganizations 호출: userId={}", request.getUserId());

        try {
            userOrganizationService.deleteAllUserOrganizations(request.getUserId());

            DeleteUserOrganizationResponse response = DeleteUserOrganizationResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("All user organizations deleted successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] deleteAllUserOrganizations 실패: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    private UserOrganizationInfo toUserOrganizationInfo(UserOrganizationDto uo) {
        UserOrganizationInfo.Builder builder = UserOrganizationInfo.newBuilder();

        if (uo.getId() != null) builder.setId(uo.getId());
        if (uo.getTenantId() != null) builder.setTenantId(uo.getTenantId());
        if (uo.getUserId() != null) builder.setUserId(uo.getUserId());
        if (uo.getOrganizationId() != null) builder.setOrganizationId(uo.getOrganizationId());
        if (uo.getRoleId() != null) builder.setRoleId(uo.getRoleId());
        if (uo.getIsPrimary() != null) builder.setIsPrimary(uo.getIsPrimary());
        if (uo.getPosition() != null) builder.setPosition(uo.getPosition());
        if (uo.getStartDate() != null) builder.setStartDate(uo.getStartDate().format(DATE_FORMATTER));
        if (uo.getEndDate() != null) builder.setEndDate(uo.getEndDate().format(DATE_FORMATTER));
        if (uo.getActive() != null) builder.setActive(uo.getActive());
        if (uo.getCreatedBy() != null) builder.setCreatedBy(uo.getCreatedBy());
        if (uo.getCreatedAt() != null) builder.setCreatedAt(uo.getCreatedAt().format(DATETIME_FORMATTER));
        if (uo.getUpdatedBy() != null) builder.setUpdatedBy(uo.getUpdatedBy());
        if (uo.getUpdatedAt() != null) builder.setUpdatedAt(uo.getUpdatedAt().format(DATETIME_FORMATTER));

        return builder.build();
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
}
