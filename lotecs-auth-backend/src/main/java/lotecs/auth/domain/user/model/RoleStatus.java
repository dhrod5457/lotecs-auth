package lotecs.auth.domain.user.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleStatus {

    private Long id;
    private String statusCode;
    private String statusName;
    private String roleCategory;
    private Boolean isActive;
    private String description;
    private Integer sortOrder;
    private Boolean isDefault;

    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    public void validate() {
        if (statusCode == null || statusCode.trim().isEmpty()) {
            throw new IllegalArgumentException("상태 코드는 필수입니다.");
        }
        if (statusName == null || statusName.trim().isEmpty()) {
            throw new IllegalArgumentException("상태 이름은 필수입니다.");
        }
        if (roleCategory == null || roleCategory.trim().isEmpty()) {
            throw new IllegalArgumentException("역할 카테고리는 필수입니다.");
        }
        if (!isValidCategory(roleCategory)) {
            throw new IllegalArgumentException("유효하지 않은 역할 카테고리입니다: " + roleCategory);
        }
    }

    private boolean isValidCategory(String category) {
        return "STUDENT".equals(category)
            || "PROFESSOR".equals(category)
            || "STAFF".equals(category)
            || "COMMON".equals(category);
    }

    public void updateInfo(String statusName, String description, String updatedBy) {
        this.statusName = statusName;
        this.description = description;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public static RoleStatus create(String statusCode, String statusName, String roleCategory,
                                    String description, String createdBy) {
        return RoleStatus.builder()
                .statusCode(statusCode)
                .statusName(statusName)
                .roleCategory(roleCategory)
                .isActive(true)
                .description(description)
                .sortOrder(0)
                .isDefault(false)
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public boolean isActiveStatus() {
        return Boolean.TRUE.equals(this.isActive);
    }

    public boolean isDefaultStatus() {
        return Boolean.TRUE.equals(this.isDefault);
    }
}
