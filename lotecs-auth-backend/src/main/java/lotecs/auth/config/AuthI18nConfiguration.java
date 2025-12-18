package lotecs.auth.config;

import lombok.extern.slf4j.Slf4j;
import lotecs.framework.common.i18n.MessageBundleRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auth 서비스 다국어 메시지 설정
 */
@Slf4j
@Configuration
public class AuthI18nConfiguration {

    @Bean
    public MessageBundleRegistryCustomizer authModuleMessages() {
        return registry -> {
            log.debug("[AUTH-I18N] Auth 에러 메시지 번들 등록: messages/auth-errors");
            registry.registerBundle("classpath:messages/auth-errors");
        };
    }
}
