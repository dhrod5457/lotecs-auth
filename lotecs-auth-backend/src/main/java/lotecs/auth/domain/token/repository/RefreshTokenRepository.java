package lotecs.auth.domain.token.repository;

import lotecs.auth.domain.token.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    /**
     * 토큰 문자열로 조회
     */
    Optional<RefreshToken> findByToken(String refreshToken);

    /**
     * 리프레시 토큰 저장
     */
    RefreshToken save(RefreshToken token);

    /**
     * 사용자 ID로 토큰 삭제
     */
    void deleteByUserId(Long userId);
}
