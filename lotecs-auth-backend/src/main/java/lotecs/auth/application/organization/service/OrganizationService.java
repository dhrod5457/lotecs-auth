package lotecs.auth.application.organization.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.application.organization.dto.OrganizationDto;
import lotecs.auth.application.organization.mapper.OrganizationDtoMapper;
import lotecs.auth.domain.organization.model.Organization;
import lotecs.auth.domain.organization.repository.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 조직 서비스
 * Relay에서 동기화되는 조직 정보를 관리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationDtoMapper organizationDtoMapper;

    @Transactional(readOnly = true)
    public OrganizationDto getOrganization(String organizationId) {
        log.debug("[ORG-001] 조직 조회: organizationId={}", organizationId);

        Organization organization = organizationRepository.findByOrganizationId(organizationId)
                .orElseThrow(() -> {
                    log.warn("[ORG-002] 조직을 찾을 수 없음: organizationId={}", organizationId);
                    return new IllegalArgumentException("Organization not found: " + organizationId);
                });

        return organizationDtoMapper.toDto(organization);
    }

    @Transactional(readOnly = true)
    public List<OrganizationDto> getOrganizationsByTenantId(String tenantId) {
        log.debug("[ORG-003] 테넌트별 조직 목록 조회: tenantId={}", tenantId);
        List<Organization> organizations = organizationRepository.findByTenantId(tenantId);
        return organizationDtoMapper.toDtoList(organizations);
    }

    @Transactional(readOnly = true)
    public List<OrganizationDto> getActiveOrganizationsByTenantId(String tenantId) {
        log.debug("[ORG-004] 테넌트별 활성 조직 목록 조회: tenantId={}", tenantId);
        List<Organization> organizations = organizationRepository.findActiveByTenantId(tenantId);
        return organizationDtoMapper.toDtoList(organizations);
    }

    @Transactional(readOnly = true)
    public List<OrganizationDto> getRootOrganizations(String tenantId) {
        log.debug("[ORG-005] 최상위 조직 목록 조회: tenantId={}", tenantId);
        List<Organization> organizations = organizationRepository.findRootOrganizations(tenantId);
        return organizationDtoMapper.toDtoList(organizations);
    }

    @Transactional(readOnly = true)
    public List<OrganizationDto> getChildOrganizations(String parentOrganizationId) {
        log.debug("[ORG-006] 하위 조직 목록 조회: parentOrganizationId={}", parentOrganizationId);
        List<Organization> organizations = organizationRepository.findByParentOrganizationId(parentOrganizationId);
        return organizationDtoMapper.toDtoList(organizations);
    }

    /**
     * Relay에서 조직 정보 동기화
     */
    @Transactional
    public OrganizationDto syncOrganization(OrganizationDto request, String syncBy) {
        log.info("[ORG-007] 조직 동기화: organizationId={}", request.getOrganizationId());

        Organization organization = organizationRepository.findByOrganizationId(request.getOrganizationId())
                .orElse(null);

        if (organization == null) {
            // 신규 생성
            organization = Organization.create(
                    request.getTenantId(),
                    request.getOrganizationId(),
                    request.getOrganizationCode(),
                    request.getOrganizationName(),
                    request.getOrganizationType(),
                    request.getParentOrganizationId(),
                    syncBy
            );

            if (request.getOrgLevel() != null) {
                organization.setOrgLevel(request.getOrgLevel());
            }
            if (request.getDisplayOrder() != null) {
                organization.setDisplayOrder(request.getDisplayOrder());
            }
            if (request.getDescription() != null) {
                organization.setDescription(request.getDescription());
            }
            if (request.getActive() != null) {
                organization.setActive(request.getActive());
            }

            organization.validate();
            organization = organizationRepository.save(organization);
            log.info("[ORG-008] 조직 동기화 완료 (신규): organizationId={}", organization.getOrganizationId());
        } else {
            // 업데이트
            organizationDtoMapper.updateEntity(request, organization);
            organization.setUpdatedBy(syncBy);
            organization.setUpdatedAt(LocalDateTime.now());

            organization = organizationRepository.save(organization);
            log.info("[ORG-009] 조직 동기화 완료 (수정): organizationId={}", organization.getOrganizationId());
        }

        return organizationDtoMapper.toDto(organization);
    }

    /**
     * 조직 삭제 (Relay 동기화용)
     */
    @Transactional
    public void deleteOrganization(String organizationId) {
        log.info("[ORG-010] 조직 삭제: organizationId={}", organizationId);

        Organization organization = organizationRepository.findByOrganizationId(organizationId)
                .orElseThrow(() -> {
                    log.warn("[ORG-011] 조직을 찾을 수 없음: organizationId={}", organizationId);
                    return new IllegalArgumentException("Organization not found: " + organizationId);
                });

        organizationRepository.delete(organization.getId());
        log.info("[ORG-012] 조직 삭제 완료: organizationId={}", organizationId);
    }
}
