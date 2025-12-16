package lotecs.auth.infrastructure.relay;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.infrastructure.grpc.StructConverter;
import lotecs.relay.auth.grpc.v1.AuthenticateRequest;
import lotecs.relay.auth.grpc.v1.AuthenticateResponse;
import lotecs.relay.auth.grpc.v1.RelayAuthServiceGrpc;
import lotecs.relay.auth.grpc.v1.RelayUserInfo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RelayClient {

    private final Map<String, ManagedChannel> channelCache = new ConcurrentHashMap<>();

    public RelayAuthResponse authenticate(String relayEndpoint, RelayAuthRequest request) {
        log.debug("Authenticating user '{}' for tenant '{}' via relay endpoint: {}",
            request.getUsername(), request.getTenantId(), relayEndpoint);

        ManagedChannel channel = getOrCreateChannel(relayEndpoint);
        RelayAuthServiceGrpc.RelayAuthServiceBlockingStub stub = RelayAuthServiceGrpc.newBlockingStub(channel)
            .withDeadlineAfter(5, TimeUnit.SECONDS);

        try {
            AuthenticateRequest authenticateRequest = AuthenticateRequest.newBuilder()
                .setTenantId(request.getTenantId())
                .setUserId(request.getUsername())
                .setPassword(request.getPassword())
                .setIpAddress(request.getIpAddress() != null ? request.getIpAddress() : "")
                .build();

            AuthenticateResponse response = stub.authenticate(authenticateRequest);

            if (response.getSuccess()) {
                RelayUserInfo userInfo = response.getUserInfo();
                log.debug("Authentication successful for user '{}'", request.getUsername());

                Map<String, Object> additionalData = userInfo.hasAdditionalData()
                    ? StructConverter.toMap(userInfo.getAdditionalData())
                    : null;

                return RelayAuthResponse.success(
                    userInfo.getUserId(),
                    userInfo.getUserName(),
                    userInfo.getUserName(),
                    userInfo.getUserType(),
                    userInfo.getDepartment(),
                    List.copyOf(userInfo.getRolesList()),
                    additionalData
                );
            } else {
                log.warn("Authentication failed for user '{}': {} - {}",
                    request.getUsername(), response.getErrorCode(), response.getErrorMessage());
                return RelayAuthResponse.failure(
                    response.getErrorCode(),
                    response.getErrorMessage()
                );
            }
        } catch (StatusRuntimeException e) {
            log.error("gRPC error during authentication for user '{}': {}",
                request.getUsername(), e.getStatus(), e);
            return RelayAuthResponse.failure(
                e.getStatus().getCode().name(),
                e.getStatus().getDescription() != null ? e.getStatus().getDescription() : "gRPC communication error"
            );
        } catch (Exception e) {
            log.error("Unexpected error during authentication for user '{}'",
                request.getUsername(), e);
            return RelayAuthResponse.failure(
                "INTERNAL_ERROR",
                "An unexpected error occurred during authentication"
            );
        }
    }

    private ManagedChannel getOrCreateChannel(String relayEndpoint) {
        return channelCache.computeIfAbsent(relayEndpoint, endpoint -> {
            log.info("Creating new gRPC channel for relay endpoint: {}", endpoint);
            return ManagedChannelBuilder.forTarget(endpoint)
                .usePlaintext()
                .keepAliveTime(30, TimeUnit.SECONDS)
                .keepAliveTimeout(10, TimeUnit.SECONDS)
                .keepAliveWithoutCalls(true)
                .build();
        });
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down {} relay gRPC channels", channelCache.size());
        channelCache.values().forEach(channel -> {
            try {
                channel.shutdown();
                if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.warn("Channel did not terminate gracefully, forcing shutdown");
                    channel.shutdownNow();
                }
            } catch (InterruptedException e) {
                log.error("Interrupted during channel shutdown", e);
                channel.shutdownNow();
                Thread.currentThread().interrupt();
            }
        });
        channelCache.clear();
    }
}
