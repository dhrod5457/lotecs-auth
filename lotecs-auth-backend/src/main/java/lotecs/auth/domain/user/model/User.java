package lotecs.auth.domain.user.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lotecs.auth.exception.user.UserValidationException;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String userId;
    private String tenantId;
    private String username;
    private String password;
    private String email;

    private String phoneNumber;
    private String fullName;

    private UserStatus status;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    private LocalDateTime lastLoginAt;
    private String lastLoginIp;
    private int failedLoginAttempts;
    private LocalDateTime lockedAt;
    private LocalDateTime passwordChangedAt;

    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @Builder.Default
    private List<Role> roles = new ArrayList<>();

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int PASSWORD_EXPIRY_DAYS = 90;

    public void recordLoginSuccess(String ipAddress) {
        this.lastLoginAt = LocalDateTime.now();
        this.lastLoginIp = ipAddress;
        this.failedLoginAttempts = 0;
        if (this.accountNonLocked == false) {
            this.accountNonLocked = true;
            this.lockedAt = null;
        }
    }

    public void recordLoginFailure() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= MAX_FAILED_ATTEMPTS) {
            this.accountNonLocked = false;
            this.lockedAt = LocalDateTime.now();
        }
    }

    public void recordLogout() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isPasswordExpired() {
        if (passwordChangedAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(passwordChangedAt.plusDays(PASSWORD_EXPIRY_DAYS));
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
        this.passwordChangedAt = LocalDateTime.now();
        this.credentialsNonExpired = true;
    }

    public void lock(String reason) {
        this.accountNonLocked = false;
        this.lockedAt = LocalDateTime.now();
    }

    public void unlock() {
        this.accountNonLocked = true;
        this.failedLoginAttempts = 0;
        this.lockedAt = null;
    }

    public void suspend(String reason) {
        this.status = UserStatus.SUSPENDED;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    public void validate() {
        if (username == null || username.trim().isEmpty()) {
            throw UserValidationException.usernameRequired();
        }
        if (password == null || password.trim().isEmpty()) {
            throw UserValidationException.passwordRequired();
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw UserValidationException.emailInvalid();
        }
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw UserValidationException.tenantRequired();
        }
    }

    /**
     * 외부 시스템으로부터 사용자 생성
     */
    public static User createFromExternal(String tenantId, String username, String email, String fullName) {
        return User.builder()
                .userId(UUID.randomUUID().toString())
                .tenantId(tenantId)
                .username(username)
                .password(UUID.randomUUID().toString())
                .email(email)
                .fullName(fullName)
                .status(UserStatus.ACTIVE)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .failedLoginAttempts(0)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 외부 시스템 정보로 사용자 업데이트
     */
    public void updateFromExternal(String email, String fullName) {
        this.email = email;
        this.fullName = fullName;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 사용자 역할 업데이트
     */
    public void updateRoles(List<Role> roles) {
        this.roles.clear();
        this.roles.addAll(roles);
    }

    /**
     * 역할 추가
     */
    public void addRole(Role role) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        if (!this.roles.contains(role)) {
            this.roles.add(role);
        }
    }

    /**
     * 역할 제거
     */
    public void removeRole(Role role) {
        if (this.roles != null) {
            this.roles.remove(role);
        }
    }

    /**
     * 삭제 여부 확인
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

}
