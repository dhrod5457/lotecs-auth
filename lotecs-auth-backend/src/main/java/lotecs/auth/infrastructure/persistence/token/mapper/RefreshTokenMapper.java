package lotecs.auth.infrastructure.persistence.token.mapper;

import lotecs.auth.domain.token.model.RefreshToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RefreshTokenMapper {

    /**
     * 토큰 ID로 조회
     */
    Optional<RefreshToken> findById(@Param("tokenId") String tokenId);

    /**
     * 토큰 해시로 조회
     */
    Optional<RefreshToken> findByTokenHash(@Param("tokenHash") String tokenHash);

    /**
     * 사용자 ID로 활성 토큰 목록 조회
     */
    List<RefreshToken> findActiveByUserId(@Param("userId") String userId);

    /**
     * 사용자 ID로 모든 토큰 조회
     */
    List<RefreshToken> findByUserId(@Param("userId") String userId);

    /**
     * 토큰 패밀리로 조회
     */
    List<RefreshToken> findByTokenFamily(@Param("tokenFamily") String tokenFamily);

    /**
     * 디바이스 ID로 활성 토큰 조회
     */
    Optional<RefreshToken> findActiveByDeviceId(@Param("userId") String userId, @Param("deviceId") String deviceId);

    /**
     * 토큰 저장
     */
    void insert(RefreshToken refreshToken);

    /**
     * 토큰 사용 기록 업데이트
     */
    void updateUsage(@Param("tokenId") String tokenId);

    /**
     * 토큰 회수
     */
    void revoke(@Param("tokenId") String tokenId, @Param("reason") String reason);

    /**
     * 토큰 패밀리 전체 회수
     */
    void revokeByFamily(@Param("tokenFamily") String tokenFamily, @Param("reason") String reason);

    /**
     * 사용자의 모든 토큰 회수
     */
    void revokeAllByUserId(@Param("userId") String userId, @Param("reason") String reason);

    /**
     * 만료된 토큰 삭제
     */
    int deleteExpiredTokens(@Param("beforeDate") java.time.LocalDateTime beforeDate);

    /**
     * 토큰 삭제
     */
    void delete(@Param("tokenId") String tokenId);
}
