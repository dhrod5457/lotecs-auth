package lotecs.auth.application.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.application.user.dto.CreateUserRequest;
import lotecs.auth.application.user.dto.UpdateUserRequest;
import lotecs.auth.application.user.dto.UserDto;
import lotecs.auth.application.user.mapper.UserDtoMapper;
import lotecs.auth.domain.user.model.Role;
import lotecs.auth.domain.user.model.User;
import lotecs.auth.domain.user.model.UserStatus;
import lotecs.auth.domain.user.repository.RoleRepository;
import lotecs.auth.domain.user.repository.UserRepository;
import lotecs.auth.exception.auth.InvalidCredentialsException;
import lotecs.auth.exception.role.RoleNotFoundException;
import lotecs.auth.exception.user.UserAlreadyExistsException;
import lotecs.auth.exception.user.UserNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDtoMapper userDtoMapper;

    /**
     * 사용자 생성
     */
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        log.info("[USER-001] 사용자 생성: tenant={}, username={}",
                request.getTenantId(), request.getUsername());

        // 중복 확인
        if (userRepository.findByUsernameAndTenantId(request.getUsername(), request.getTenantId()).isPresent()) {
            log.warn("[USER-002] 중복된 사용자: username={}, tenant={}",
                    request.getUsername(), request.getTenantId());
            throw UserAlreadyExistsException.byUsername(request.getUsername());
        }

        // 사용자 생성
        User user = User.builder()
                .tenantId(request.getTenantId())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .status(UserStatus.ACTIVE)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .failedLoginAttempts(0)
                .createdAt(LocalDateTime.now())
                .build();

        // 역할 할당
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            List<Role> roles = request.getRoles().stream()
                    .map(roleName -> roleRepository.findByRoleNameAndTenantId(roleName, request.getTenantId())
                            .orElseThrow(() -> RoleNotFoundException.byName(roleName)))
                    .collect(Collectors.toList());
            user.setRoles(roles);
        }

        user = userRepository.save(user);

        log.info("[USER-003] 사용자 생성 완료: userId={}", user.getUserId());

        return userDtoMapper.toDto(user);
    }

    /**
     * 사용자 조회 (ID)
     */
    @Transactional(readOnly = true)
    public UserDto getUserById(String userId, String tenantId) {
        log.debug("[USER-004] 사용자 조회: userId={}, tenant={}", userId, tenantId);

        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> {
                    log.warn("[USER-005] 사용자를 찾을 수 없음: userId={}", userId);
                    return UserNotFoundException.byId(userId);
                });

        return userDtoMapper.toDto(user);
    }

    /**
     * 사용자 조회 (Username)
     */
    @Transactional(readOnly = true)
    public UserDto getUserByUsername(String username, String tenantId) {
        log.debug("[USER-006] 사용자 조회: username={}, tenant={}", username, tenantId);

        User user = userRepository.findByUsernameAndTenantId(username, tenantId)
                .orElseThrow(() -> {
                    log.warn("[USER-007] 사용자를 찾을 수 없음: username={}", username);
                    return UserNotFoundException.byUsername(username);
                });

        return userDtoMapper.toDto(user);
    }

    /**
     * 사용자 목록 조회
     */
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(String tenantId, int page, int size) {
        log.debug("[USER-008] 사용자 목록 조회: tenant={}, page={}, size={}",
                tenantId, page, size);

        List<User> users = userRepository.findByTenantId(tenantId, page, size);

        return userDtoMapper.toDtoList(users);
    }

    /**
     * 사용자 수정
     */
    @Transactional
    public UserDto updateUser(String userId, String tenantId, UpdateUserRequest request) {
        log.info("[USER-009] 사용자 수정: userId={}, tenant={}", userId, tenantId);

        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> {
                    log.warn("[USER-010] 사용자를 찾을 수 없음: userId={}", userId);
                    return UserNotFoundException.byId(userId);
                });

        // 정보 업데이트
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getStatus() != null) {
            user.setStatus(UserStatus.valueOf(request.getStatus()));
        }

        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        log.info("[USER-011] 사용자 수정 완료: userId={}", userId);

        return userDtoMapper.toDto(user);
    }

    /**
     * 사용자 삭제
     */
    @Transactional
    public void deleteUser(String userId, String tenantId) {
        log.info("[USER-012] 사용자 삭제: userId={}, tenant={}", userId, tenantId);

        userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> {
                    log.warn("[USER-013] 사용자를 찾을 수 없음: userId={}", userId);
                    return UserNotFoundException.byId(userId);
                });

        userRepository.delete(userId);

        log.info("[USER-014] 사용자 삭제 완료: userId={}", userId);
    }

    /**
     * 단일 역할 할당
     */
    @Transactional
    public void assignRole(String userId, String tenantId, String roleId, String statusCode, String assignedBy) {
        log.info("[USER-015] 역할 할당: userId={}, tenant={}, roleId={}", userId, tenantId, roleId);

        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));

        Role role = roleRepository.findByIdAndTenantId(roleId, tenantId)
                .orElseThrow(() -> RoleNotFoundException.byId(roleId));

        user.addRole(role);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("[USER-016] 역할 할당 완료: userId={}, roleId={}", userId, roleId);
    }

    /**
     * 다중 역할 할당
     */
    @Transactional
    public int assignRoles(String userId, String tenantId, List<String> roleIds, String statusCode, String assignedBy) {
        log.info("[USER-017] 다중 역할 할당: userId={}, tenant={}, roleCount={}", userId, tenantId, roleIds.size());

        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));

        int assignedCount = 0;
        for (String roleId : roleIds) {
            try {
                Role role = roleRepository.findByIdAndTenantId(roleId, tenantId)
                        .orElseThrow(() -> RoleNotFoundException.byId(roleId));
                user.addRole(role);
                assignedCount++;
            } catch (Exception e) {
                log.warn("[USER-018] 역할 할당 실패: roleId={}, error={}", roleId, e.getMessage());
            }
        }

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("[USER-019] 다중 역할 할당 완료: userId={}, assignedCount={}", userId, assignedCount);
        return assignedCount;
    }

    /**
     * 역할 제거
     */
    @Transactional
    public void revokeRole(String userId, String tenantId, String roleId, String revokedBy) {
        log.info("[USER-020] 역할 제거: userId={}, tenant={}, roleId={}", userId, tenantId, roleId);

        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));

        Role role = roleRepository.findByIdAndTenantId(roleId, tenantId)
                .orElseThrow(() -> RoleNotFoundException.byId(roleId));

        user.removeRole(role);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("[USER-021] 역할 제거 완료: userId={}, roleId={}", userId, roleId);
    }

    /**
     * 계정 잠금
     */
    @Transactional
    public void lockUser(String userId, String tenantId, String reason, String lockedBy) {
        log.info("[USER-022] 계정 잠금: userId={}, tenant={}, reason={}", userId, tenantId, reason);

        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));

        user.setAccountNonLocked(false);
        user.setStatus(UserStatus.LOCKED);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("[USER-023] 계정 잠금 완료: userId={}", userId);
    }

    /**
     * 계정 잠금 해제
     */
    @Transactional
    public void unlockUser(String userId, String tenantId, String unlockedBy) {
        log.info("[USER-024] 계정 잠금 해제: userId={}, tenant={}", userId, tenantId);

        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));

        user.setAccountNonLocked(true);
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedLoginAttempts(0);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("[USER-025] 계정 잠금 해제 완료: userId={}", userId);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(String userId, String tenantId, String currentPassword, String newPassword) {
        log.info("[USER-026] 비밀번호 변경: userId={}, tenant={}", userId, tenantId);

        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            log.warn("[USER-027] 현재 비밀번호 불일치: userId={}", userId);
            throw InvalidCredentialsException.passwordMismatch();
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("[USER-028] 비밀번호 변경 완료: userId={}", userId);
    }
}
