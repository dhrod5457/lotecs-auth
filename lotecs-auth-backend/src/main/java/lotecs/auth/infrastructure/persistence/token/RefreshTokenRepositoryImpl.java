package lotecs.auth.infrastructure.persistence.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.token.model.RefreshToken;
import lotecs.auth.domain.token.repository.RefreshTokenRepository;
import lotecs.auth.infrastructure.persistence.token.mapper.RefreshTokenMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenMapper refreshTokenMapper;

    @Override
    public Optional<RefreshToken> findByToken(String refreshToken) {
        log.debug("Finding refresh token by token value");
        return refreshTokenMapper.findByToken(refreshToken);
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        log.debug("Saving refresh token for userId: {}", token.getUserId());
        refreshTokenMapper.insert(token);
        return token;
    }

    @Override
    public void deleteByUserId(Long userId) {
        log.debug("Deleting refresh token by userId: {}", userId);
        refreshTokenMapper.deleteByUserId(userId);
    }
}
