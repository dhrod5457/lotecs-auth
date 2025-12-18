package lotecs.auth.domain.organization.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lotecs.auth.exception.organization.OrganizationValidationException;

/**
 * 조직 도메인 모델
 * 조직의 기본 정보와 계층 구조를 관리
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organization {

    private Long id;
    private String tenantId;
    private String organizationId;
    private String organizationCode;
    private String organizationName;
    private String organizationType;
    private String parentOrganizationId;
    private Integer orgLevel;
    private Integer displayOrder;
    private String description;
    @Getter(lombok.AccessLevel.NONE)
    private Boolean active;

    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    /**
     * 새로운 조직 생성
     */
    public static Organization create(
            String tenantId,
            String organizationId,
            String organizationCode,
            String organizationName,
            String organizationType,
            String parentOrganizationId,
            String createdBy
    ) {
        return Organization.builder()
                .tenantId(tenantId)
                .organizationId(organizationId)
                .organizationCode(organizationCode)
                .organizationName(organizationName)
                .organizationType(organizationType)
                .parentOrganizationId(parentOrganizationId)
                .orgLevel(0)
                .displayOrder(0)
                .active(true)
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 조직 활성화
     */
    public void activate(String updatedBy) {
        this.active = true;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 조직 비활성화
     */
    public void deactivate(String updatedBy) {
        this.active = false;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 조직 수정
     */
    public void update(
            String organizationName,
            String organizationType,
            String description,
            Integer displayOrder,
            String updatedBy
    ) {
        this.organizationName = organizationName;
        this.organizationType = organizationType;
        this.description = description;
        this.displayOrder = displayOrder;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 상위 조직 변경
     */
    public void changeParent(String parentOrganizationId, Integer newOrgLevel, String updatedBy) {
        this.parentOrganizationId = parentOrganizationId;
        this.orgLevel = newOrgLevel;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 최상위 조직 여부 확인
     */
    public boolean isRoot() {
        return this.parentOrganizationId == null;
    }

    /**
     * 활성화 여부 확인
     */
    public Boolean getActive() {
        return this.active;
    }

    /**
     * 유효성 검증
     */
    public void validate() {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw OrganizationValidationException.tenantRequired();
        }
        if (organizationId == null || organizationId.trim().isEmpty()) {
            throw OrganizationValidationException.idRequired();
        }
        if (organizationCode == null || organizationCode.trim().isEmpty()) {
            throw OrganizationValidationException.codeRequired();
        }
        if (organizationName == null || organizationName.trim().isEmpty()) {
            throw OrganizationValidationException.nameRequired();
        }
        if (organizationType == null || organizationType.trim().isEmpty()) {
            throw OrganizationValidationException.typeRequired();
        }
        if (orgLevel == null || orgLevel < 0) {
            throw OrganizationValidationException.levelInvalid();
        }
    }
}
