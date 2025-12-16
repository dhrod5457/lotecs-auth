package lotecs.auth.infrastructure.persistence.tenant;

import lotecs.auth.domain.tenant.model.SiteStatus;
import lotecs.auth.domain.tenant.model.Tenant;
import lotecs.auth.domain.tenant.repository.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("TenantRepository 통합 테스트")
class TenantRepositoryIntegrationTest {

    @Autowired
    private TenantRepository tenantRepository;

    private Tenant testTenant;

    @BeforeEach
    void setUp() {
        testTenant = Tenant.builder()
                .tenantId("test-tenant-" + System.currentTimeMillis())
                .siteName("테스트 사이트")
                .siteCode("TEST-" + System.currentTimeMillis())
                .description("테스트 설명")
                .status(SiteStatus.DRAFT)
                .siteLevel(0)
                .version(1L)
                .createdBy("admin")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("테넌트 저장 성공")
        void save_Success() {
            // when
            Tenant saved = tenantRepository.save(testTenant);

            // then
            assertThat(saved).isNotNull();
            assertThat(saved.getTenantId()).isEqualTo(testTenant.getTenantId());
            assertThat(saved.getSiteName()).isEqualTo("테스트 사이트");
            assertThat(saved.getStatus()).isEqualTo(SiteStatus.DRAFT);
        }

        @Test
        @DisplayName("테넌트 수정 성공")
        void save_Update() {
            // given
            Tenant saved = tenantRepository.save(testTenant);
            saved.setSiteName("수정된 사이트");
            saved.setDescription("수정된 설명");
            saved.setUpdatedBy("updater");
            saved.setUpdatedAt(LocalDateTime.now());

            // when
            Tenant updated = tenantRepository.save(saved);

            // then
            assertThat(updated.getSiteName()).isEqualTo("수정된 사이트");
            assertThat(updated.getDescription()).isEqualTo("수정된 설명");
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("ID로 테넌트 조회 성공")
        void findById_Success() {
            // given
            tenantRepository.save(testTenant);

            // when
            Optional<Tenant> found = tenantRepository.findById(testTenant.getTenantId());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getSiteCode()).isEqualTo(testTenant.getSiteCode());
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional 반환")
        void findById_NotFound() {
            // when
            Optional<Tenant> found = tenantRepository.findById("non-existent-id");

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("findBySiteCode")
    class FindBySiteCode {

        @Test
        @DisplayName("사이트 코드로 테넌트 조회 성공")
        void findBySiteCode_Success() {
            // given
            tenantRepository.save(testTenant);

            // when
            Optional<Tenant> found = tenantRepository.findBySiteCode(testTenant.getSiteCode());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getTenantId()).isEqualTo(testTenant.getTenantId());
        }
    }

    @Nested
    @DisplayName("findAll / findActive")
    class ListTenants {

        @Test
        @DisplayName("전체 테넌트 목록 조회")
        void findAll_Success() {
            // given
            tenantRepository.save(testTenant);

            Tenant anotherTenant = Tenant.builder()
                    .tenantId("another-tenant-" + System.currentTimeMillis())
                    .siteName("다른 사이트")
                    .siteCode("ANOTHER-" + System.currentTimeMillis())
                    .status(SiteStatus.PUBLISHED)
                    .siteLevel(0)
                    .version(1L)
                    .createdBy("admin")
                    .createdAt(LocalDateTime.now())
                    .build();
            tenantRepository.save(anotherTenant);

            // when
            List<Tenant> tenants = tenantRepository.findAll();

            // then
            assertThat(tenants).hasSizeGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("활성(PUBLISHED) 테넌트 목록 조회")
        void findActive_Success() {
            // given
            Tenant publishedTenant = Tenant.builder()
                    .tenantId("published-tenant-" + System.currentTimeMillis())
                    .siteName("게시된 사이트")
                    .siteCode("PUBLISHED-" + System.currentTimeMillis())
                    .status(SiteStatus.PUBLISHED)
                    .publishedAt(LocalDateTime.now())
                    .siteLevel(0)
                    .version(1L)
                    .createdBy("admin")
                    .createdAt(LocalDateTime.now())
                    .build();
            tenantRepository.save(publishedTenant);

            // when
            List<Tenant> activeTenants = tenantRepository.findActive();

            // then
            assertThat(activeTenants)
                    .extracting(Tenant::getStatus)
                    .containsOnly(SiteStatus.PUBLISHED);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("테넌트 삭제 성공")
        void delete_Success() {
            // given
            tenantRepository.save(testTenant);
            assertThat(tenantRepository.findById(testTenant.getTenantId())).isPresent();

            // when
            tenantRepository.delete(testTenant.getTenantId());

            // then
            assertThat(tenantRepository.findById(testTenant.getTenantId())).isEmpty();
        }
    }

    @Nested
    @DisplayName("상태 변경 시나리오")
    class StatusChangeScenarios {

        @Test
        @DisplayName("DRAFT -> PUBLISHED -> DRAFT 상태 변경 흐름")
        void statusChangeFlow() {
            // given
            tenantRepository.save(testTenant);
            assertThat(testTenant.getStatus()).isEqualTo(SiteStatus.DRAFT);

            // when: publish
            testTenant.publish("admin");
            Tenant published = tenantRepository.save(testTenant);

            // then
            assertThat(published.getStatus()).isEqualTo(SiteStatus.PUBLISHED);
            assertThat(published.getPublishedAt()).isNotNull();

            // when: unpublish
            published.unpublish("admin", "테스트 사유");
            Tenant unpublished = tenantRepository.save(published);

            // then
            assertThat(unpublished.getStatus()).isEqualTo(SiteStatus.DRAFT);
            assertThat(unpublished.getUnpublishedAt()).isNotNull();
        }
    }
}
