package lotecs.auth.domain.organization.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lotecs.auth.exception.organization.UserOrganizationValidationException;

/**
 * 사용자-조직 매핑 도메인 모델
 * 사용자와 조직의 다대다 관계를 관리
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOrganization {

    private Long id;
    private String tenantId;
    private String userId;
    private String organizationId;
    private String roleId;
    @Getter(lombok.AccessLevel.NONE)
    private Boolean isPrimary;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
    @Getter(lombok.AccessLevel.NONE)
    private Boolean active;

    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    /**
     * 새로운 사용자-조직 매핑 생성
     */
    public static UserOrganization create(
            String tenantId,
            String userId,
            String organizationId,
            String roleId,
            Boolean isPrimary,
            String position,
            LocalDate startDate,
            LocalDate endDate,
            String createdBy
    ) {
        return UserOrganization.builder()
                .tenantId(tenantId)
                .userId(userId)
                .organizationId(organizationId)
                .roleId(roleId)
                .isPrimary(isPrimary != null ? isPrimary : false)
                .position(position)
                .startDate(startDate)
                .endDate(endDate)
                .active(true)
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 주 소속으로 설정
     */
    public void setPrimaryOrganization(String updatedBy) {
        this.isPrimary = true;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 주 소속 해제
     */
    public void unsetPrimaryOrganization(String updatedBy) {
        this.isPrimary = false;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 소속 정보 수정
     */
    public void update(
            String roleId,
            String position,
            LocalDate startDate,
            LocalDate endDate,
            String updatedBy
    ) {
        this.roleId = roleId;
        this.position = position;
        this.startDate = startDate;
        this.endDate = endDate;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 활성화
     */
    public void activate(String updatedBy) {
        this.active = true;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 비활성화
     */
    public void deactivate(String updatedBy) {
        this.active = false;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 소속 종료
     */
    public void terminate(LocalDate endDate, String updatedBy) {
        this.endDate = endDate;
        this.active = false;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 주 소속 필드 조회 (MyBatis용)
     */
    public Boolean getIsPrimary() {
        return this.isPrimary;
    }

    /**
     * 주 소속 여부 확인
     */
    public boolean isPrimaryOrganization() {
        return Boolean.TRUE.equals(this.isPrimary);
    }

    /**
     * 활성화 필드 조회 (MyBatis용)
     */
    public Boolean getActive() {
        return this.active;
    }

    /**
     * 현재 유효한 소속인지 확인
     */
    public boolean isCurrentlyValid() {
        if (!Boolean.TRUE.equals(this.active)) {
            return false;
        }

        LocalDate now = LocalDate.now();

        // 시작일 체크
        if (startDate != null && now.isBefore(startDate)) {
            return false;
        }

        // 종료일 체크
        if (endDate != null && now.isAfter(endDate)) {
            return false;
        }

        return true;
    }

    /**
     * 유효성 검증
     */
    public void validate() {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw UserOrganizationValidationException.tenantRequired();
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw UserOrganizationValidationException.userRequired();
        }
        if (organizationId == null || organizationId.trim().isEmpty()) {
            throw UserOrganizationValidationException.organizationRequired();
        }
        if (endDate != null && startDate != null && endDate.isBefore(startDate)) {
            throw UserOrganizationValidationException.dateInvalid();
        }
    }
}
