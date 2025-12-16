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
            throw new IllegalArgumentException("Username already exists");
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
                            .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName)))
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
                    return new IllegalArgumentException("User not found");
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
                    return new IllegalArgumentException("User not found");
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
                    return new IllegalArgumentException("User not found");
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
                    return new IllegalArgumentException("User not found");
                });

        userRepository.delete(userId);

        log.info("[USER-014] 사용자 삭제 완료: userId={}", userId);
    }
}
