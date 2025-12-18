package lotecs.auth.application.sso.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.application.sso.dto.SsoConfigDto;
import lotecs.auth.application.sso.mapper.SsoConfigDtoMapper;
import lotecs.auth.domain.sso.SsoType;
import lotecs.auth.domain.sso.model.TenantSsoConfig;
import lotecs.auth.domain.sso.repository.TenantSsoConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SsoConfigService {

    private final TenantSsoConfigRepository ssoConfigRepository;
    private final SsoConfigDtoMapper ssoConfigDtoMapper;

    /**
     * SSO 설정 조회
     */
    @Transactional(readOnly = true)
    public SsoConfigDto getSsoConfig(String tenantId) {
        log.debug("[SSO-001] SSO 설정 조회: tenant={}", tenantId);

        TenantSsoConfig config = ssoConfigRepository.findByTenantId(tenantId)
                .orElseThrow(() -> {
                    log.warn("[SSO-002] SSO 설정을 찾을 수 없음: tenant={}", tenantId);
                    return new IllegalArgumentException("SSO config not found");
                });

        return ssoConfigDtoMapper.toDto(config);
    }

    /**
     * SSO 설정 수정
     */
    @Transactional
    public SsoConfigDto updateSsoConfig(String tenantId, SsoConfigDto request) {
        log.info("[SSO-003] SSO 설정 수정: tenant={}", tenantId);

        TenantSsoConfig config = ssoConfigRepository.findByTenantId(tenantId)
                .orElse(TenantSsoConfig.builder()
                        .tenantId(tenantId)
                        .build());

        // 설정 업데이트
        if (request.getSsoType() != null) {
            config.setSsoType(SsoType.valueOf(request.getSsoType()));
        }
        if (request.getSsoEnabled() != null) {
            config.setSsoEnabled(request.getSsoEnabled());
        }
        if (request.getSsoServerUrl() != null) {
            config.setSsoServerUrl(request.getSsoServerUrl());
        }
        if (request.getSsoRealm() != null) {
            config.setSsoRealm(request.getSsoRealm());
        }
        if (request.getSsoClientId() != null) {
            config.setSsoClientId(request.getSsoClientId());
        }
        if (request.getUserSyncEnabled() != null) {
            config.setUserSyncEnabled(request.getUserSyncEnabled());
        }
        if (request.getRoleMappingEnabled() != null) {
            config.setRoleMappingEnabled(request.getRoleMappingEnabled());
        }

        config.setUpdatedAt(LocalDateTime.now());
        config = ssoConfigRepository.save(config);

        log.info("[SSO-004] SSO 설정 수정 완료: tenant={}", tenantId);

        return ssoConfigDtoMapper.toDto(config);
    }

    /**
     * SSO 연결 테스트
     */
    @Transactional(readOnly = true)
    public boolean testSsoConnection(String tenantId) {
        log.info("[SSO-005] SSO 연결 테스트: tenant={}", tenantId);

        TenantSsoConfig config = ssoConfigRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("SSO config not found"));

        // TODO: 실제 SSO 연결 테스트 로직 구현
        // KEYCLOAK, LDAP, JWT_SSO, CAS, REST_TOKEN, HTTP_FORM 타입별로 연결 테스트

        log.info("[SSO-006] SSO 연결 테스트 완료: tenant={}", tenantId);

        return true;
    }
}
