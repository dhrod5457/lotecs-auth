package lotecs.auth.application.tenant.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.application.tenant.dto.TenantDto;
import lotecs.auth.application.tenant.mapper.TenantDtoMapper;
import lotecs.auth.domain.tenant.model.Tenant;
import lotecs.auth.domain.tenant.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantDtoMapper tenantDtoMapper;

    @Transactional(readOnly = true)
    public TenantDto getTenant(String tenantId) {
        log.debug("[TENANT-001] 테넌트 조회: tenantId={}", tenantId);

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> {
                    log.warn("[TENANT-002] 테넌트를 찾을 수 없음: tenantId={}", tenantId);
                    return new IllegalArgumentException("Tenant not found: " + tenantId);
                });

        return tenantDtoMapper.toDto(tenant);
    }

    @Transactional(readOnly = true)
    public TenantDto getTenantBySiteCode(String siteCode) {
        log.debug("[TENANT-003] 사이트 코드로 테넌트 조회: siteCode={}", siteCode);

        Tenant tenant = tenantRepository.findBySiteCode(siteCode)
                .orElseThrow(() -> {
                    log.warn("[TENANT-004] 테넌트를 찾을 수 없음: siteCode={}", siteCode);
                    return new IllegalArgumentException("Tenant not found: " + siteCode);
                });

        return tenantDtoMapper.toDto(tenant);
    }

    @Transactional(readOnly = true)
    public List<TenantDto> getAllTenants() {
        log.debug("[TENANT-005] 전체 테넌트 목록 조회");
        List<Tenant> tenants = tenantRepository.findAll();
        return tenantDtoMapper.toDtoList(tenants);
    }

    @Transactional(readOnly = true)
    public List<TenantDto> getActiveTenants() {
        log.debug("[TENANT-006] 활성 테넌트 목록 조회");
        List<Tenant> tenants = tenantRepository.findActive();
        return tenantDtoMapper.toDtoList(tenants);
    }

    @Transactional
    public TenantDto createTenant(TenantDto request, String createdBy) {
        log.info("[TENANT-007] 테넌트 생성: siteCode={}", request.getSiteCode());

        // 중복 검사
        if (tenantRepository.findBySiteCode(request.getSiteCode()).isPresent()) {
            log.warn("[TENANT-008] 이미 존재하는 사이트 코드: siteCode={}", request.getSiteCode());
            throw new IllegalArgumentException("Site code already exists: " + request.getSiteCode());
        }

        Tenant tenant = Tenant.create(
                request.getSiteName(),
                request.getSiteCode(),
                request.getDescription(),
                createdBy
        );

        // 추가 필드 설정
        if (request.getPrimaryDomain() != null) {
            tenant.setPrimaryDomain(request.getPrimaryDomain());
        }
        if (request.getAdditionalDomains() != null) {
            tenant.setAdditionalDomains(request.getAdditionalDomains());
        }
        if (request.getSiteTitle() != null) {
            tenant.setSiteTitle(request.getSiteTitle());
        }
        if (request.getSiteDescription() != null) {
            tenant.setSiteDescription(request.getSiteDescription());
        }
        if (request.getThemeName() != null) {
            tenant.setThemeName(request.getThemeName());
        }
        if (request.getDefaultLanguage() != null) {
            tenant.setDefaultLanguage(request.getDefaultLanguage());
        }
        if (request.getTimezone() != null) {
            tenant.setTimezone(request.getTimezone());
        }
        if (request.getOwnerEmail() != null) {
            tenant.setOwnerEmail(request.getOwnerEmail());
        }
        if (request.getAdminEmail() != null) {
            tenant.setAdminEmail(request.getAdminEmail());
        }
        if (request.getContactPhone() != null) {
            tenant.setContactPhone(request.getContactPhone());
        }
        if (request.getParentTenantId() != null) {
            tenant.setParentTenantId(request.getParentTenantId());
        }
        if (request.getSiteLevel() != null) {
            tenant.setSiteLevel(request.getSiteLevel());
        }
        if (request.getMaxContentItems() != null) {
            tenant.setMaxContentItems(request.getMaxContentItems());
        }
        if (request.getMaxStorageMb() != null) {
            tenant.setMaxStorageMb(request.getMaxStorageMb());
        }
        if (request.getMaxUsers() != null) {
            tenant.setMaxUsers(request.getMaxUsers());
        }
        if (request.getFeatures() != null) {
            tenant.setFeatures(request.getFeatures());
        }
        if (request.getSettings() != null) {
            tenant.setSettings(request.getSettings());
        }
        if (request.getSubscriptionPlanCode() != null) {
            tenant.setSubscriptionPlanCode(request.getSubscriptionPlanCode());
        }
        if (request.getPlanStartDate() != null) {
            tenant.setPlanStartDate(request.getPlanStartDate());
        }
        if (request.getPlanEndDate() != null) {
            tenant.setPlanEndDate(request.getPlanEndDate());
        }

        tenant.validate();
        tenant = tenantRepository.save(tenant);

        log.info("[TENANT-009] 테넌트 생성 완료: tenantId={}", tenant.getTenantId());
        return tenantDtoMapper.toDto(tenant);
    }

    @Transactional
    public TenantDto updateTenant(String tenantId, TenantDto request, String updatedBy) {
        log.info("[TENANT-010] 테넌트 수정: tenantId={}", tenantId);

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> {
                    log.warn("[TENANT-011] 테넌트를 찾을 수 없음: tenantId={}", tenantId);
                    return new IllegalArgumentException("Tenant not found: " + tenantId);
                });

        tenantDtoMapper.updateEntity(request, tenant);
        tenant.setUpdatedBy(updatedBy);
        tenant.setUpdatedAt(LocalDateTime.now());

        tenant = tenantRepository.save(tenant);

        log.info("[TENANT-012] 테넌트 수정 완료: tenantId={}", tenantId);
        return tenantDtoMapper.toDto(tenant);
    }

    @Transactional
    public void deleteTenant(String tenantId) {
        log.info("[TENANT-013] 테넌트 삭제: tenantId={}", tenantId);

        if (tenantRepository.findById(tenantId).isEmpty()) {
            log.warn("[TENANT-014] 테넌트를 찾을 수 없음: tenantId={}", tenantId);
            throw new IllegalArgumentException("Tenant not found: " + tenantId);
        }

        tenantRepository.delete(tenantId);

        log.info("[TENANT-015] 테넌트 삭제 완료: tenantId={}", tenantId);
    }

    @Transactional
    public TenantDto publishTenant(String tenantId, String publishedBy) {
        log.info("[TENANT-016] 테넌트 게시: tenantId={}", tenantId);

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> {
                    log.warn("[TENANT-017] 테넌트를 찾을 수 없음: tenantId={}", tenantId);
                    return new IllegalArgumentException("Tenant not found: " + tenantId);
                });

        tenant.publish(publishedBy);
        tenant = tenantRepository.save(tenant);

        log.info("[TENANT-018] 테넌트 게시 완료: tenantId={}, status={}", tenantId, tenant.getStatus());
        return tenantDtoMapper.toDto(tenant);
    }

    @Transactional
    public TenantDto unpublishTenant(String tenantId, String unpublishedBy, String reason) {
        log.info("[TENANT-019] 테넌트 게시 중단: tenantId={}, reason={}", tenantId, reason);

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> {
                    log.warn("[TENANT-020] 테넌트를 찾을 수 없음: tenantId={}", tenantId);
                    return new IllegalArgumentException("Tenant not found: " + tenantId);
                });

        tenant.unpublish(unpublishedBy, reason);
        tenant = tenantRepository.save(tenant);

        log.info("[TENANT-021] 테넌트 게시 중단 완료: tenantId={}, status={}", tenantId, tenant.getStatus());
        return tenantDtoMapper.toDto(tenant);
    }

    @Transactional
    public TenantDto suspendTenant(String tenantId, String suspendedBy, String reason) {
        log.info("[TENANT-022] 테넌트 일시중지: tenantId={}, reason={}", tenantId, reason);

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> {
                    log.warn("[TENANT-023] 테넌트를 찾을 수 없음: tenantId={}", tenantId);
                    return new IllegalArgumentException("Tenant not found: " + tenantId);
                });

        tenant.suspend(suspendedBy, reason);
        tenant = tenantRepository.save(tenant);

        log.info("[TENANT-024] 테넌트 일시중지 완료: tenantId={}, status={}", tenantId, tenant.getStatus());
        return tenantDtoMapper.toDto(tenant);
    }

    @Transactional
    public TenantDto resumeTenant(String tenantId, String resumedBy) {
        log.info("[TENANT-025] 테넌트 재개: tenantId={}", tenantId);

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> {
                    log.warn("[TENANT-026] 테넌트를 찾을 수 없음: tenantId={}", tenantId);
                    return new IllegalArgumentException("Tenant not found: " + tenantId);
                });

        tenant.resume(resumedBy);
        tenant = tenantRepository.save(tenant);

        log.info("[TENANT-027] 테넌트 재개 완료: tenantId={}, status={}", tenantId, tenant.getStatus());
        return tenantDtoMapper.toDto(tenant);
    }

    @Transactional
    public TenantDto archiveTenant(String tenantId, String archivedBy) {
        log.info("[TENANT-028] 테넌트 보관: tenantId={}", tenantId);

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> {
                    log.warn("[TENANT-029] 테넌트를 찾을 수 없음: tenantId={}", tenantId);
                    return new IllegalArgumentException("Tenant not found: " + tenantId);
                });

        tenant.archive(archivedBy);
        tenant = tenantRepository.save(tenant);

        log.info("[TENANT-030] 테넌트 보관 완료: tenantId={}, status={}", tenantId, tenant.getStatus());
        return tenantDtoMapper.toDto(tenant);
    }
}
