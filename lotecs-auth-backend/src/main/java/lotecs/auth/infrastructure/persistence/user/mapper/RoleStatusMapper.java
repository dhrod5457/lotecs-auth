package lotecs.auth.infrastructure.persistence.user.mapper;

import lotecs.auth.domain.user.model.RoleStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RoleStatusMapper {

    /**
     * 상태 코드로 조회
     */
    Optional<RoleStatus> findByStatusCode(@Param("statusCode") String statusCode);

    /**
     * 전체 역할 상태 목록 조회
     */
    List<RoleStatus> findAll();

    /**
     * 활성 상태 목록 조회
     */
    List<RoleStatus> findActive();

    /**
     * 역할 카테고리별 상태 목록 조회
     */
    List<RoleStatus> findByRoleCategory(@Param("roleCategory") String roleCategory);

    /**
     * 기본 상태 조회
     */
    Optional<RoleStatus> findDefaultByRoleCategory(@Param("roleCategory") String roleCategory);

    /**
     * 역할 상태 등록
     */
    void insert(RoleStatus roleStatus);

    /**
     * 역할 상태 수정
     */
    void update(RoleStatus roleStatus);

    /**
     * 역할 상태 삭제
     */
    void delete(@Param("statusCode") String statusCode);
}
