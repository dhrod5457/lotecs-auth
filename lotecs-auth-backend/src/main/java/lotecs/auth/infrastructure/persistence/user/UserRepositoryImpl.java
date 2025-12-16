package lotecs.auth.infrastructure.persistence.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.user.model.User;
import lotecs.auth.domain.user.repository.UserRepository;
import lotecs.auth.infrastructure.persistence.user.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public Optional<User> findById(String userId) {
        log.debug("Finding user by id: {}", userId);
        return userMapper.findById(userId);
    }

    @Override
    public Optional<User> findByIdAndTenantId(String userId, String tenantId) {
        log.debug("Finding user by id and tenantId: userId={}, tenantId={}", userId, tenantId);
        return userMapper.findById(userId)
                .filter(user -> tenantId.equals(user.getTenantId()));
    }

    @Override
    public Optional<User> findByUsernameAndTenantId(String username, String tenantId) {
        log.debug("Finding user by username and tenantId: username={}, tenantId={}", username, tenantId);
        return userMapper.findByUsernameAndTenantId(username, tenantId);
    }

    @Override
    public List<User> findByTenantId(String tenantId, int page, int size) {
        log.debug("Finding users by tenantId: tenantId={}, page={}, size={}", tenantId, page, size);
        int offset = page * size;
        return userMapper.findByTenantId(tenantId, offset, size);
    }

    @Override
    public User save(User user) {
        if (user.getUserId() == null) {
            log.debug("Inserting new user: username={}", user.getUsername());
            userMapper.insert(user);
        } else {
            log.debug("Updating user: userId={}", user.getUserId());
            userMapper.update(user);
        }
        return user;
    }

    @Override
    public void delete(String userId) {
        log.debug("Deleting user: userId={}", userId);
        userMapper.delete(userId);
    }
}
