package lotecs.auth.domain.organization.repository;

import lotecs.auth.domain.organization.model.UserOrganization;

import java.util.List;
import java.util.Optional;

public interface UserOrganizationRepository {

    /**
     * 내부 ID로 조회
     */
    Optional<UserOrganization> findById(Long id);

    /**
     * 사용자 ID로 소속 조직 목록 조회
     */
    List<UserOrganization> findByUserId(String userId);

    /**
     * 사용자 ID로 활성 소속 조직 목록 조회
     */
    List<UserOrganization> findActiveByUserId(String userId);

    /**
     * 사용자의 주 소속 조직 조회
     */
    Optional<UserOrganization> findPrimaryByUserId(String userId);

    /**
     * 조직 ID로 소속 사용자 목록 조회
     */
    List<UserOrganization> findByOrganizationId(String organizationId);

    /**
     * 조직 ID로 활성 소속 사용자 목록 조회
     */
    List<UserOrganization> findActiveByOrganizationId(String organizationId);

    /**
     * 테넌트 ID로 전체 조회
     */
    List<UserOrganization> findByTenantId(String tenantId);

    /**
     * 사용자-조직 매핑 저장
     */
    UserOrganization save(UserOrganization userOrganization);

    /**
     * 사용자-조직 매핑 삭제
     */
    void delete(Long id);

    /**
     * 사용자의 모든 조직 매핑 삭제
     */
    void deleteByUserId(String userId);

    /**
     * 조직의 모든 사용자 매핑 삭제
     */
    void deleteByOrganizationId(String organizationId);
}
