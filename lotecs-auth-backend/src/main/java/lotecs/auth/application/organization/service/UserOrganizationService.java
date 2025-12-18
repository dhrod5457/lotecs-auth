package lotecs.auth.application.organization.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.application.organization.dto.UserOrganizationDto;
import lotecs.auth.application.organization.mapper.UserOrganizationDtoMapper;
import lotecs.auth.domain.organization.model.UserOrganization;
import lotecs.auth.domain.organization.repository.UserOrganizationRepository;
import lotecs.auth.exception.organization.UserOrganizationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자-조직 매핑 서비스
 * Relay에서 동기화되는 사용자-조직 관계를 관리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserOrganizationService {

    private final UserOrganizationRepository userOrganizationRepository;
    private final UserOrganizationDtoMapper userOrganizationDtoMapper;

    @Transactional(readOnly = true)
    public List<UserOrganizationDto> getUserOrganizations(String userId) {
        log.debug("[USER-ORG-001] 사용자 조직 목록 조회: userId={}", userId);
        List<UserOrganization> userOrganizations = userOrganizationRepository.findByUserId(userId);
        return userOrganizationDtoMapper.toDtoList(userOrganizations);
    }

    @Transactional(readOnly = true)
    public List<UserOrganizationDto> getActiveUserOrganizations(String userId) {
        log.debug("[USER-ORG-002] 사용자 활성 조직 목록 조회: userId={}", userId);
        List<UserOrganization> userOrganizations = userOrganizationRepository.findActiveByUserId(userId);
        return userOrganizationDtoMapper.toDtoList(userOrganizations);
    }

    @Transactional(readOnly = true)
    public UserOrganizationDto getPrimaryUserOrganization(String userId) {
        log.debug("[USER-ORG-003] 사용자 주 소속 조회: userId={}", userId);

        UserOrganization userOrganization = userOrganizationRepository.findPrimaryByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("[USER-ORG-004] 사용자 주 소속을 찾을 수 없음: userId={}", userId);
                    return UserOrganizationNotFoundException.primaryNotFound(userId);
                });

        return userOrganizationDtoMapper.toDto(userOrganization);
    }

    @Transactional(readOnly = true)
    public List<UserOrganizationDto> getOrganizationUsers(String organizationId) {
        log.debug("[USER-ORG-005] 조직 소속 사용자 목록 조회: organizationId={}", organizationId);
        List<UserOrganization> userOrganizations = userOrganizationRepository.findByOrganizationId(organizationId);
        return userOrganizationDtoMapper.toDtoList(userOrganizations);
    }

    @Transactional(readOnly = true)
    public List<UserOrganizationDto> getActiveOrganizationUsers(String organizationId) {
        log.debug("[USER-ORG-006] 조직 활성 소속 사용자 목록 조회: organizationId={}", organizationId);
        List<UserOrganization> userOrganizations = userOrganizationRepository.findActiveByOrganizationId(organizationId);
        return userOrganizationDtoMapper.toDtoList(userOrganizations);
    }

    /**
     * Relay에서 사용자-조직 매핑 동기화
     */
    @Transactional
    public UserOrganizationDto syncUserOrganization(UserOrganizationDto request, String syncBy) {
        log.info("[USER-ORG-007] 사용자-조직 매핑 동기화: userId={}, organizationId={}",
                request.getUserId(), request.getOrganizationId());

        // 기존 매핑 조회 (userId + organizationId로 유일 식별)
        UserOrganization userOrganization = userOrganizationRepository.findByUserId(request.getUserId())
                .stream()
                .filter(uo -> uo.getOrganizationId().equals(request.getOrganizationId()))
                .findFirst()
                .orElse(null);

        if (userOrganization == null) {
            // 신규 생성
            userOrganization = UserOrganization.create(
                    request.getTenantId(),
                    request.getUserId(),
                    request.getOrganizationId(),
                    request.getRoleId(),
                    request.getIsPrimary(),
                    request.getPosition(),
                    request.getStartDate(),
                    request.getEndDate(),
                    syncBy
            );

            if (request.getActive() != null) {
                userOrganization.setActive(request.getActive());
            }

            userOrganization.validate();
            userOrganization = userOrganizationRepository.save(userOrganization);
            log.info("[USER-ORG-008] 사용자-조직 매핑 동기화 완료 (신규): id={}", userOrganization.getId());
        } else {
            // 업데이트
            userOrganizationDtoMapper.updateEntity(request, userOrganization);
            userOrganization.setUpdatedBy(syncBy);
            userOrganization.setUpdatedAt(LocalDateTime.now());

            userOrganization = userOrganizationRepository.save(userOrganization);
            log.info("[USER-ORG-009] 사용자-조직 매핑 동기화 완료 (수정): id={}", userOrganization.getId());
        }

        return userOrganizationDtoMapper.toDto(userOrganization);
    }

    /**
     * 사용자-조직 매핑 삭제 (Relay 동기화용)
     */
    @Transactional
    public void deleteUserOrganization(Long id) {
        log.info("[USER-ORG-010] 사용자-조직 매핑 삭제: id={}", id);

        if (userOrganizationRepository.findById(id).isEmpty()) {
            log.warn("[USER-ORG-011] 사용자-조직 매핑을 찾을 수 없음: id={}", id);
            throw UserOrganizationNotFoundException.byId(id);
        }

        userOrganizationRepository.delete(id);
        log.info("[USER-ORG-012] 사용자-조직 매핑 삭제 완료: id={}", id);
    }

    /**
     * 사용자의 모든 조직 매핑 삭제 (Relay 동기화용)
     */
    @Transactional
    public void deleteAllUserOrganizations(String userId) {
        log.info("[USER-ORG-013] 사용자의 모든 조직 매핑 삭제: userId={}", userId);
        userOrganizationRepository.deleteByUserId(userId);
        log.info("[USER-ORG-014] 사용자의 모든 조직 매핑 삭제 완료: userId={}", userId);
    }
}
