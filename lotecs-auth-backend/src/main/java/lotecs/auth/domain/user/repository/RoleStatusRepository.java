package lotecs.auth.domain.user.repository;

import lotecs.auth.domain.user.model.RoleStatus;

import java.util.List;
import java.util.Optional;

public interface RoleStatusRepository {

    /**
     * 상태 코드로 조회
     */
    Optional<RoleStatus> findByStatusCode(String statusCode);

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
    List<RoleStatus> findByRoleCategory(String roleCategory);

    /**
     * 기본 상태 조회
     */
    Optional<RoleStatus> findDefaultByRoleCategory(String roleCategory);

    /**
     * 역할 상태 저장
     */
    RoleStatus save(RoleStatus roleStatus);

    /**
     * 역할 상태 삭제
     */
    void delete(String statusCode);
}
