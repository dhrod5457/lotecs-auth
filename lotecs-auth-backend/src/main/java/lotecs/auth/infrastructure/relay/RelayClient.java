package lotecs.auth.infrastructure.relay;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import com.lotecs.auth.grpc.AuthServiceGrpc;
import com.lotecs.auth.grpc.LoginRequest;
import com.lotecs.auth.grpc.LoginResponse;
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
            request.username(), request.tenantId(), relayEndpoint);

        ManagedChannel channel = getOrCreateChannel(relayEndpoint);
        AuthServiceGrpc.AuthServiceBlockingStub stub = AuthServiceGrpc.newBlockingStub(channel)
            .withDeadlineAfter(5, TimeUnit.SECONDS);

        try {
            LoginRequest loginRequest = LoginRequest.newBuilder()
                .setUsername(request.username())
                .setPassword(request.password())
                .setTenantId(request.tenantId())
                .build();

            LoginResponse response = stub.login(loginRequest);

            if (response.hasUser()) {
                log.debug("Authentication successful for user '{}'", request.username());
                return RelayAuthResponse.success(
                    response.getUser().getUserId(),
                    request.username(),
                    response.getUser().getFullName(),
                    List.copyOf(response.getUser().getRolesList())
                );
            } else {
                log.warn("Authentication failed for user '{}'", request.username());
                return RelayAuthResponse.failure(
                    "AUTH_FAILED",
                    "Authentication failed"
                );
            }
        } catch (StatusRuntimeException e) {
            log.error("gRPC error during authentication for user '{}': {}",
                request.username(), e.getStatus(), e);
            return RelayAuthResponse.failure(
                e.getStatus().getCode().name(),
                e.getStatus().getDescription() != null ? e.getStatus().getDescription() : "gRPC communication error"
            );
        } catch (Exception e) {
            log.error("Unexpected error during authentication for user '{}'",
                request.username(), e);
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
