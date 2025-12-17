package lotecs.auth.sdk.autoconfigure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuthGrpcSdkAutoConfiguration 통합 테스트")
class AuthGrpcSdkAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AuthGrpcSdkAutoConfiguration.class));

    @Test
    @DisplayName("기본 설정으로 AutoConfiguration이 로드되어야 한다")
    void shouldLoadAutoConfigurationWithDefaultSettings() {
        contextRunner
                .withPropertyValues("lotecs.auth.grpc.enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(AuthGrpcSdkProperties.class);
                    assertThat(context).hasSingleBean(AuthGrpcSdkAutoConfiguration.class);
                });
    }

    @Test
    @DisplayName("enabled=false일 때 AutoConfiguration이 로드되지 않아야 한다")
    void shouldNotLoadAutoConfigurationWhenDisabled() {
        contextRunner
                .withPropertyValues("lotecs.auth.grpc.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(AuthGrpcSdkAutoConfiguration.class);
                });
    }

    @Test
    @DisplayName("Properties가 올바르게 바인딩되어야 한다")
    void shouldBindPropertiesCorrectly() {
        contextRunner
                .withPropertyValues(
                        "lotecs.auth.grpc.enabled=true",
                        "lotecs.auth.grpc.address=static://auth-server:9090",
                        "lotecs.auth.grpc.deadline-ms=10000",
                        "lotecs.auth.grpc.negotiation-type=TLS",
                        "lotecs.auth.grpc.enable-retry=true",
                        "lotecs.auth.grpc.max-retry-attempts=5"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(AuthGrpcSdkProperties.class);
                    AuthGrpcSdkProperties properties = context.getBean(AuthGrpcSdkProperties.class);
                    assertThat(properties.isEnabled()).isTrue();
                    assertThat(properties.getAddress()).isEqualTo("static://auth-server:9090");
                    assertThat(properties.getDeadlineMs()).isEqualTo(10000);
                    assertThat(properties.getNegotiationType()).isEqualTo("TLS");
                    assertThat(properties.isEnableRetry()).isTrue();
                    assertThat(properties.getMaxRetryAttempts()).isEqualTo(5);
                });
    }

    @Test
    @DisplayName("기본값이 올바르게 설정되어야 한다")
    void shouldHaveCorrectDefaultValues() {
        contextRunner
                .withPropertyValues("lotecs.auth.grpc.enabled=true")
                .run(context -> {
                    AuthGrpcSdkProperties properties = context.getBean(AuthGrpcSdkProperties.class);
                    assertThat(properties.isEnabled()).isTrue();
                    assertThat(properties.getAddress()).isEqualTo("static://localhost:9090");
                    assertThat(properties.getDeadlineMs()).isEqualTo(5000);
                    assertThat(properties.getNegotiationType()).isEqualTo("PLAINTEXT");
                    assertThat(properties.isEnableRetry()).isFalse();
                    assertThat(properties.getMaxRetryAttempts()).isEqualTo(3);
                });
    }
}
