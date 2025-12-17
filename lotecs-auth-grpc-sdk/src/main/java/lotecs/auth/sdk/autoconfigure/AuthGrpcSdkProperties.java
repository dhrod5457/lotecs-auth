package lotecs.auth.sdk.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "lotecs.auth.grpc")
public class AuthGrpcSdkProperties {

    private boolean enabled = true;

    private String address = "static://localhost:9090";

    private long deadlineMs = 5000;

    private String negotiationType = "PLAINTEXT";

    private boolean enableRetry = false;

    private int maxRetryAttempts = 3;
}
