package lotecs.auth.application.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.sso.SsoAuthResult;
import lotecs.auth.domain.sso.model.ExternalUserMapping;
import lotecs.auth.domain.sso.model.TenantSsoConfig;
import lotecs.auth.domain.sso.repository.ExternalUserMappingRepository;
import lotecs.auth.domain.user.model.Role;
import lotecs.auth.domain.user.model.User;
import lotecs.auth.domain.user.repository.RoleRepository;
import lotecs.auth.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserSyncService {

    private final UserRepository userRepository;
    private final ExternalUserMappingRepository mappingRepository;
    private final RoleRepository roleRepository;

    /**
     * 외부 SSO 시스템의 사용자 정보를 내부 DB와 동기화
     *
     * @param ssoResult 외부 인증 결과
     * @param config SSO 설정
     * @return 동기화된 사용자
     */
    public User syncUserFromExternal(SsoAuthResult ssoResult, TenantSsoConfig config) {
        log.debug("[SYNC] 사용자 동기화 시작: tenantId={}, externalUserId={}, ssoType={}",
                config.getTenantId(), ssoResult.externalUserId(), config.getSsoType());

        // 1. ExternalUserMapping에서 기존 매핑 조회
        Optional<ExternalUserMapping> mappingOpt = mappingRepository.findByExternalUserId(
                config.getTenantId(),
                ssoResult.externalUserId(),
                config.getSsoType().name()
        );

        User user;

        if (mappingOpt.isPresent()) {
            // 매핑 있으면 User 업데이트
            ExternalUserMapping mapping = mappingOpt.get();
            user = userRepository.findById(mapping.getUserId())
                    .orElseThrow(() -> new IllegalStateException(
                            "매핑된 사용자를 찾을 수 없습니다: userId=" + mapping.getUserId()));

            user.updateFromExternal(ssoResult.email(), ssoResult.fullName());
            user = userRepository.save(user);

            // 매핑 정보 업데이트 (last_synced_at)
            mapping.setLastSyncedAt(LocalDateTime.now());
            mappingRepository.save(mapping);

            log.info("[SYNC] 기존 사용자 업데이트: userId={}, externalUserId={}, username={}",
                    user.getUserId(), ssoResult.externalUserId(), user.getUsername());

        } else {
            // 매핑 없으면 신규 생성
            user = User.createFromExternal(
                    config.getTenantId(),
                    ssoResult.username(),
                    ssoResult.email(),
                    ssoResult.fullName()
            );
            user = userRepository.save(user);

            // 매핑 정보 저장
            ExternalUserMapping mapping = ExternalUserMapping.builder()
                    .tenantId(config.getTenantId())
                    .userId(user.getUserId())
                    .externalUserId(ssoResult.externalUserId())
                    .externalSystem(config.getSsoType().name())
                    .lastSyncedAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .build();

            mappingRepository.save(mapping);

            log.info("[SYNC] 신규 사용자 생성: userId={}, externalUserId={}, username={}",
                    user.getUserId(), ssoResult.externalUserId(), user.getUsername());
        }

        // 2. 역할 동기화 (config.isRoleMappingEnabled() 확인)
        if (config.isRoleMappingEnabled() && ssoResult.roles() != null && !ssoResult.roles().isEmpty()) {
            syncUserRoles(user, ssoResult.roles());
            log.debug("[SYNC] 역할 동기화 완료: userId={}, roles={}", user.getUserId(), ssoResult.roles());
        }

        log.info("[SYNC] 사용자 동기화 완료: userId={}, tenantId={}, externalUserId={}",
                user.getUserId(), user.getTenantId(), ssoResult.externalUserId());

        return user;
    }

    /**
     * 외부 역할을 내부 역할로 매핑
     *
     * @param user 사용자
     * @param externalRoles 외부 역할 목록
     */
    public void syncUserRoles(User user, List<String> externalRoles) {
        if (externalRoles == null || externalRoles.isEmpty()) {
            log.debug("[SYNC] 동기화할 역할이 없습니다: userId={}", user.getUserId());
            return;
        }

        log.debug("[SYNC] 역할 동기화 시작: userId={}, externalRoles={}", user.getUserId(), externalRoles);

        List<Role> roles = new ArrayList<>();

        for (String externalRoleName : externalRoles) {
            Optional<Role> roleOpt = roleRepository.findByRoleName(user.getTenantId(), externalRoleName);

            if (roleOpt.isPresent()) {
                roles.add(roleOpt.get());
                log.debug("[SYNC] 역할 매핑 성공: roleName={}", externalRoleName);
            } else {
                log.warn("[SYNC] 역할을 찾을 수 없습니다: tenantId={}, roleName={}",
                        user.getTenantId(), externalRoleName);
            }
        }

        if (!roles.isEmpty()) {
            user.setRoles(roles);
            log.info("[SYNC] 역할 동기화 완료: userId={}, syncedRoles={}", user.getUserId(), roles.size());
        } else {
            log.warn("[SYNC] 동기화된 역할이 없습니다: userId={}", user.getUserId());
        }
    }
}
