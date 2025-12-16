package lotecs.auth.infrastructure.config;

import jakarta.annotation.PreDestroy;
import lotecs.relay.sdk.auth.RelayAuthClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Relay gRPC 클라이언트 설정.
 */
@Configuration
public class RelayClientConfig {

    private RelayAuthClient relayAuthClient;

    @Bean
    public RelayAuthClient relayAuthClient() {
        this.relayAuthClient = new RelayAuthClient();
        return this.relayAuthClient;
    }

    @PreDestroy
    public void shutdown() {
        if (relayAuthClient != null) {
            relayAuthClient.shutdown();
        }
    }
}
