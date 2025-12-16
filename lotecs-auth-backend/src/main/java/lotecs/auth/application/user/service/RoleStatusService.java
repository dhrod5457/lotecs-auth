package lotecs.auth.application.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.application.user.dto.RoleStatusDto;
import lotecs.auth.application.user.mapper.RoleStatusDtoMapper;
import lotecs.auth.domain.user.model.RoleStatus;
import lotecs.auth.domain.user.repository.RoleStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleStatusService {

    private final RoleStatusRepository roleStatusRepository;
    private final RoleStatusDtoMapper roleStatusDtoMapper;

    @Transactional(readOnly = true)
    public RoleStatusDto getRoleStatus(String statusCode) {
        log.debug("[ROLE-STATUS-001] 역할 상태 조회: statusCode={}", statusCode);

        RoleStatus roleStatus = roleStatusRepository.findByStatusCode(statusCode)
                .orElseThrow(() -> {
                    log.warn("[ROLE-STATUS-002] 역할 상태를 찾을 수 없음: statusCode={}", statusCode);
                    return new IllegalArgumentException("Role status not found: " + statusCode);
                });

        return roleStatusDtoMapper.toDto(roleStatus);
    }

    @Transactional(readOnly = true)
    public List<RoleStatusDto> getAllRoleStatuses() {
        log.debug("[ROLE-STATUS-003] 전체 역할 상태 목록 조회");
        List<RoleStatus> roleStatuses = roleStatusRepository.findAll();
        return roleStatusDtoMapper.toDtoList(roleStatuses);
    }

    @Transactional(readOnly = true)
    public List<RoleStatusDto> getActiveRoleStatuses() {
        log.debug("[ROLE-STATUS-004] 활성 역할 상태 목록 조회");
        List<RoleStatus> roleStatuses = roleStatusRepository.findActive();
        return roleStatusDtoMapper.toDtoList(roleStatuses);
    }

    @Transactional(readOnly = true)
    public List<RoleStatusDto> getRoleStatusesByCategory(String roleCategory) {
        log.debug("[ROLE-STATUS-005] 카테고리별 역할 상태 목록 조회: roleCategory={}", roleCategory);
        List<RoleStatus> roleStatuses = roleStatusRepository.findByRoleCategory(roleCategory);
        return roleStatusDtoMapper.toDtoList(roleStatuses);
    }

    @Transactional
    public RoleStatusDto createRoleStatus(RoleStatusDto request, String createdBy) {
        log.info("[ROLE-STATUS-006] 역할 상태 생성: statusCode={}", request.getStatusCode());

        if (roleStatusRepository.findByStatusCode(request.getStatusCode()).isPresent()) {
            log.warn("[ROLE-STATUS-007] 이미 존재하는 상태 코드: statusCode={}", request.getStatusCode());
            throw new IllegalArgumentException("Status code already exists: " + request.getStatusCode());
        }

        RoleStatus roleStatus = RoleStatus.create(
                request.getStatusCode(),
                request.getStatusName(),
                request.getRoleCategory(),
                request.getDescription(),
                createdBy
        );

        if (request.getIsActive() != null) {
            roleStatus.setIsActive(request.getIsActive());
        }
        if (request.getSortOrder() != null) {
            roleStatus.setSortOrder(request.getSortOrder());
        }
        if (request.getIsDefault() != null) {
            roleStatus.setIsDefault(request.getIsDefault());
        }

        roleStatus.validate();
        roleStatus = roleStatusRepository.save(roleStatus);

        log.info("[ROLE-STATUS-008] 역할 상태 생성 완료: statusCode={}", roleStatus.getStatusCode());
        return roleStatusDtoMapper.toDto(roleStatus);
    }

    @Transactional
    public RoleStatusDto updateRoleStatus(String statusCode, RoleStatusDto request, String updatedBy) {
        log.info("[ROLE-STATUS-009] 역할 상태 수정: statusCode={}", statusCode);

        RoleStatus roleStatus = roleStatusRepository.findByStatusCode(statusCode)
                .orElseThrow(() -> {
                    log.warn("[ROLE-STATUS-010] 역할 상태를 찾을 수 없음: statusCode={}", statusCode);
                    return new IllegalArgumentException("Role status not found: " + statusCode);
                });

        roleStatusDtoMapper.updateEntity(request, roleStatus);
        roleStatus.setUpdatedBy(updatedBy);
        roleStatus.setUpdatedAt(LocalDateTime.now());

        roleStatus = roleStatusRepository.save(roleStatus);

        log.info("[ROLE-STATUS-011] 역할 상태 수정 완료: statusCode={}", statusCode);
        return roleStatusDtoMapper.toDto(roleStatus);
    }

    @Transactional
    public void deleteRoleStatus(String statusCode) {
        log.info("[ROLE-STATUS-012] 역할 상태 삭제: statusCode={}", statusCode);

        if (roleStatusRepository.findByStatusCode(statusCode).isEmpty()) {
            log.warn("[ROLE-STATUS-013] 역할 상태를 찾을 수 없음: statusCode={}", statusCode);
            throw new IllegalArgumentException("Role status not found: " + statusCode);
        }

        roleStatusRepository.delete(statusCode);

        log.info("[ROLE-STATUS-014] 역할 상태 삭제 완료: statusCode={}", statusCode);
    }
}
