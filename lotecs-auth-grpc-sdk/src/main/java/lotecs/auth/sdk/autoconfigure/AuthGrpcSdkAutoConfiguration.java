package lotecs.auth.sdk.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import lotecs.auth.sdk.client.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@AutoConfiguration
@ConditionalOnClass(name = "net.devh.boot.grpc.client.inject.GrpcClient")
@ConditionalOnProperty(prefix = "lotecs.auth.grpc", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(AuthGrpcSdkProperties.class)
@ComponentScan(basePackageClasses = {
        AuthServiceClient.class,
        TenantServiceClient.class,
        RoleStatusServiceClient.class,
        OrganizationServiceClient.class,
        UserOrganizationServiceClient.class
})
public class AuthGrpcSdkAutoConfiguration {

    public AuthGrpcSdkAutoConfiguration() {
        log.debug("AuthGrpcSdkAutoConfiguration loaded");
    }
}
