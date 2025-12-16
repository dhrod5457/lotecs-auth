package lotecs.auth.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 설정 (CORS, Path Prefix 등)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api/v1/ath",
                HandlerTypePredicate.forBasePackage("lotecs.batch.admin.presentation.job")
                        .or(HandlerTypePredicate.forBasePackage("lotecs.batch.admin.presentation.execution"))
                        .or(HandlerTypePredicate.forBasePackage("lotecs.batch.admin.presentation.workflow"))
                        .or(HandlerTypePredicate.forBasePackage("lotecs.batch.admin.presentation.schedule"))
                        .or(HandlerTypePredicate.forBasePackage("lotecs.batch.admin.presentation.dashboard"))
                        .or(HandlerTypePredicate.forBasePackage("lotecs.batch.admin.presentation.monitoring")));
    }
}
