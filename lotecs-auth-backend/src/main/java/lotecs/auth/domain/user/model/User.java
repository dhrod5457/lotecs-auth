package lotecs.auth.domain.user.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long userId;

    @NotBlank
    private String tenantId;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String email;

    private String phoneNumber;

    private String fullName;

    @NotBlank
    private String status;

    @NotNull
    @Builder.Default
    private Boolean accountNonLocked = true;

    @NotNull
    @Builder.Default
    private Boolean credentialsNonExpired = true;

    @NotNull
    @Builder.Default
    private Boolean enabled = true;

    private LocalDateTime lastLoginAt;

    private String lastLoginIp;

    @NotNull
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    private LocalDateTime lockedAt;

    private LocalDateTime passwordChangedAt;

    private String createdBy;

    private LocalDateTime createdAt;

    private String updatedBy;

    private LocalDateTime updatedAt;

    @Builder.Default
    private List<Role> roles = new ArrayList<>();

    /**
     * 로그인 정보 업데이트
     */
    public void updateLoginInfo(String ipAddress) {
        this.lastLoginAt = LocalDateTime.now();
        this.lastLoginIp = ipAddress;
        this.failedLoginAttempts = 0;
    }

    /**
     * 로그인 실패 시도 증가
     */
    public void incrementFailedAttempts() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            lock();
        }
    }

    /**
     * 계정 잠금
     */
    public void lock() {
        this.accountNonLocked = false;
        this.lockedAt = LocalDateTime.now();
    }

    /**
     * 계정 잠금 해제
     */
    public void unlock() {
        this.accountNonLocked = true;
        this.failedLoginAttempts = 0;
        this.lockedAt = null;
    }

    /**
     * 외부 시스템으로부터 사용자 생성
     */
    public static User createFromExternal(String tenantId, String username, String email, String fullName) {
        return User.builder()
                .tenantId(tenantId)
                .username(username)
                .password(UUID.randomUUID().toString())
                .email(email)
                .fullName(fullName)
                .status("ACTIVE")
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
}
