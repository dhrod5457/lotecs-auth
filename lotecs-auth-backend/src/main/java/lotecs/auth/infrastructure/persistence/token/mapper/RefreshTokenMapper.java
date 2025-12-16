package lotecs.auth.infrastructure.persistence.token.mapper;

import lotecs.auth.domain.token.model.RefreshToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface RefreshTokenMapper {

    /**
     * 토큰으로 조회
     */
    Optional<RefreshToken> findByToken(@Param("token") String token);

    /**
     * 사용자 ID로 토큰 조회
     */
    Optional<RefreshToken> findByUserId(@Param("userId") Long userId);

    /**
     * 토큰 등록
     */
    void insert(RefreshToken refreshToken);

    /**
     * 사용자 ID로 토큰 삭제
     */
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 토큰으로 삭제
     */
    void deleteByToken(@Param("token") String token);

    /**
     * 만료된 토큰 삭제
     */
    void deleteExpiredTokens();
}
