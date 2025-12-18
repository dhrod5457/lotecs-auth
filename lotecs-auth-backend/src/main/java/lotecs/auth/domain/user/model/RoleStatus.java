package lotecs.auth.domain.user.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lotecs.auth.exception.role.RoleStatusValidationException;

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
            throw RoleStatusValidationException.codeRequired();
        }
        if (statusName == null || statusName.trim().isEmpty()) {
            throw RoleStatusValidationException.nameRequired();
        }
        if (roleCategory == null || roleCategory.trim().isEmpty()) {
            throw RoleStatusValidationException.categoryRequired();
        }
        if (!isValidCategory(roleCategory)) {
            throw RoleStatusValidationException.categoryInvalid(roleCategory);
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
