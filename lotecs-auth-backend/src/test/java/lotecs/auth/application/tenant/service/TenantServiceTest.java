package lotecs.auth.application.tenant.service;

import lotecs.auth.application.tenant.dto.TenantDto;
import lotecs.auth.application.tenant.mapper.TenantDtoMapper;
import lotecs.auth.domain.tenant.model.SiteStatus;
import lotecs.auth.domain.tenant.model.Tenant;
import lotecs.auth.domain.tenant.repository.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TenantService 단위 테스트")
class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private TenantDtoMapper tenantDtoMapper;

    @InjectMocks
    private TenantService tenantService;

    private Tenant testTenant;
    private TenantDto testTenantDto;

    @BeforeEach
    void setUp() {
        testTenant = Tenant.builder()
                .tenantId("test-tenant-id")
                .siteName("테스트 사이트")
                .siteCode("TEST")
                .description("테스트 설명")
                .status(SiteStatus.DRAFT)
                .siteLevel(0)
                .version(1L)
                .createdBy("admin")
                .createdAt(LocalDateTime.now())
                .build();

        testTenantDto = TenantDto.builder()
                .tenantId("test-tenant-id")
                .siteName("테스트 사이트")
                .siteCode("TEST")
                .description("테스트 설명")
                .status("DRAFT")
                .siteLevel(0)
                .version(1L)
                .createdBy("admin")
                .build();
    }

    @Nested
    @DisplayName("getTenant")
    class GetTenant {

        @Test
        @DisplayName("존재하는 테넌트 조회 성공")
        void getTenant_Success() {
            // given
            given(tenantRepository.findById("test-tenant-id"))
                    .willReturn(Optional.of(testTenant));
            given(tenantDtoMapper.toDto(testTenant))
                    .willReturn(testTenantDto);

            // when
            TenantDto result = tenantService.getTenant("test-tenant-id");

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTenantId()).isEqualTo("test-tenant-id");
            assertThat(result.getSiteName()).isEqualTo("테스트 사이트");

            then(tenantRepository).should().findById("test-tenant-id");
        }

        @Test
        @DisplayName("존재하지 않는 테넌트 조회 시 예외 발생")
        void getTenant_NotFound() {
            // given
            given(tenantRepository.findById("non-existent"))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> tenantService.getTenant("non-existent"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Tenant not found");
        }
    }

    @Nested
    @DisplayName("getTenantBySiteCode")
    class GetTenantBySiteCode {

        @Test
        @DisplayName("사이트 코드로 테넌트 조회 성공")
        void getTenantBySiteCode_Success() {
            // given
            given(tenantRepository.findBySiteCode("TEST"))
                    .willReturn(Optional.of(testTenant));
            given(tenantDtoMapper.toDto(testTenant))
                    .willReturn(testTenantDto);

            // when
            TenantDto result = tenantService.getTenantBySiteCode("TEST");

            // then
            assertThat(result).isNotNull();
            assertThat(result.getSiteCode()).isEqualTo("TEST");
        }

        @Test
        @DisplayName("존재하지 않는 사이트 코드로 조회 시 예외 발생")
        void getTenantBySiteCode_NotFound() {
            // given
            given(tenantRepository.findBySiteCode("UNKNOWN"))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> tenantService.getTenantBySiteCode("UNKNOWN"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Tenant not found");
        }
    }

    @Nested
    @DisplayName("getAllTenants / getActiveTenants")
    class ListTenants {

        @Test
        @DisplayName("전체 테넌트 목록 조회")
        void getAllTenants_Success() {
            // given
            List<Tenant> tenants = Arrays.asList(testTenant);
            List<TenantDto> tenantDtos = Arrays.asList(testTenantDto);

            given(tenantRepository.findAll()).willReturn(tenants);
            given(tenantDtoMapper.toDtoList(tenants)).willReturn(tenantDtos);

            // when
            List<TenantDto> result = tenantService.getAllTenants();

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSiteCode()).isEqualTo("TEST");
        }

        @Test
        @DisplayName("활성 테넌트 목록 조회")
        void getActiveTenants_Success() {
            // given
            List<Tenant> tenants = Arrays.asList(testTenant);
            List<TenantDto> tenantDtos = Arrays.asList(testTenantDto);

            given(tenantRepository.findActive()).willReturn(tenants);
            given(tenantDtoMapper.toDtoList(tenants)).willReturn(tenantDtos);

            // when
            List<TenantDto> result = tenantService.getActiveTenants();

            // then
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("createTenant")
    class CreateTenant {

        @Test
        @DisplayName("테넌트 생성 성공")
        void createTenant_Success() {
            // given
            TenantDto createRequest = TenantDto.builder()
                    .siteName("새 사이트")
                    .siteCode("NEW")
                    .description("새 사이트 설명")
                    .build();

            given(tenantRepository.findBySiteCode("NEW"))
                    .willReturn(Optional.empty());
            given(tenantRepository.save(any(Tenant.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));
            given(tenantDtoMapper.toDto(any(Tenant.class)))
                    .willReturn(TenantDto.builder()
                            .tenantId("generated-id")
                            .siteName("새 사이트")
                            .siteCode("NEW")
                            .status("DRAFT")
                            .build());

            // when
            TenantDto result = tenantService.createTenant(createRequest, "admin");

            // then
            assertThat(result).isNotNull();
            assertThat(result.getSiteCode()).isEqualTo("NEW");
            assertThat(result.getStatus()).isEqualTo("DRAFT");

            then(tenantRepository).should().save(any(Tenant.class));
        }

        @Test
        @DisplayName("중복된 사이트 코드로 생성 시 예외 발생")
        void createTenant_DuplicateSiteCode() {
            // given
            TenantDto createRequest = TenantDto.builder()
                    .siteName("중복 사이트")
                    .siteCode("TEST")
                    .build();

            given(tenantRepository.findBySiteCode("TEST"))
                    .willReturn(Optional.of(testTenant));

            // when & then
            assertThatThrownBy(() -> tenantService.createTenant(createRequest, "admin"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Site code already exists");
        }
    }

    @Nested
    @DisplayName("updateTenant")
    class UpdateTenant {

        @Test
        @DisplayName("테넌트 수정 성공")
        void updateTenant_Success() {
            // given
            TenantDto updateRequest = TenantDto.builder()
                    .siteName("수정된 사이트")
                    .description("수정된 설명")
                    .build();

            given(tenantRepository.findById("test-tenant-id"))
                    .willReturn(Optional.of(testTenant));
            willDoNothing().given(tenantDtoMapper).updateEntity(updateRequest, testTenant);
            given(tenantRepository.save(testTenant)).willReturn(testTenant);
            given(tenantDtoMapper.toDto(testTenant)).willReturn(testTenantDto);

            // when
            TenantDto result = tenantService.updateTenant("test-tenant-id", updateRequest, "admin");

            // then
            assertThat(result).isNotNull();
            then(tenantRepository).should().save(testTenant);
        }
    }

    @Nested
    @DisplayName("deleteTenant")
    class DeleteTenant {

        @Test
        @DisplayName("테넌트 삭제 성공")
        void deleteTenant_Success() {
            // given
            given(tenantRepository.findById("test-tenant-id"))
                    .willReturn(Optional.of(testTenant));
            willDoNothing().given(tenantRepository).delete("test-tenant-id");

            // when
            tenantService.deleteTenant("test-tenant-id");

            // then
            then(tenantRepository).should().delete("test-tenant-id");
        }

        @Test
        @DisplayName("존재하지 않는 테넌트 삭제 시 예외 발생")
        void deleteTenant_NotFound() {
            // given
            given(tenantRepository.findById("non-existent"))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> tenantService.deleteTenant("non-existent"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Tenant not found");
        }
    }

    @Nested
    @DisplayName("publishTenant / unpublishTenant")
    class PublishUnpublishTenant {

        @Test
        @DisplayName("DRAFT 상태 테넌트 게시 성공")
        void publishTenant_Success() {
            // given
            Tenant draftTenant = Tenant.builder()
                    .tenantId("draft-tenant")
                    .siteName("Draft Site")
                    .siteCode("DRAFT")
                    .status(SiteStatus.DRAFT)
                    .build();

            TenantDto publishedDto = TenantDto.builder()
                    .tenantId("draft-tenant")
                    .status("PUBLISHED")
                    .build();

            given(tenantRepository.findById("draft-tenant"))
                    .willReturn(Optional.of(draftTenant));
            given(tenantRepository.save(any(Tenant.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));
            given(tenantDtoMapper.toDto(any(Tenant.class)))
                    .willReturn(publishedDto);

            // when
            TenantDto result = tenantService.publishTenant("draft-tenant", "admin");

            // then
            assertThat(result.getStatus()).isEqualTo("PUBLISHED");
        }

        @Test
        @DisplayName("PUBLISHED 상태 테넌트 게시 시도 시 예외 발생")
        void publishTenant_AlreadyPublished() {
            // given
            Tenant publishedTenant = Tenant.builder()
                    .tenantId("published-tenant")
                    .status(SiteStatus.PUBLISHED)
                    .build();

            given(tenantRepository.findById("published-tenant"))
                    .willReturn(Optional.of(publishedTenant));

            // when & then
            assertThatThrownBy(() -> tenantService.publishTenant("published-tenant", "admin"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("DRAFT 상태에서만");
        }

        @Test
        @DisplayName("PUBLISHED 상태 테넌트 게시 중단 성공")
        void unpublishTenant_Success() {
            // given
            Tenant publishedTenant = Tenant.builder()
                    .tenantId("published-tenant")
                    .siteName("Published Site")
                    .siteCode("PUB")
                    .status(SiteStatus.PUBLISHED)
                    .publishedAt(LocalDateTime.now().minusDays(1))
                    .build();

            TenantDto unpublishedDto = TenantDto.builder()
                    .tenantId("published-tenant")
                    .status("DRAFT")
                    .build();

            given(tenantRepository.findById("published-tenant"))
                    .willReturn(Optional.of(publishedTenant));
            given(tenantRepository.save(any(Tenant.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));
            given(tenantDtoMapper.toDto(any(Tenant.class)))
                    .willReturn(unpublishedDto);

            // when
            TenantDto result = tenantService.unpublishTenant("published-tenant", "admin", "테스트 사유");

            // then
            assertThat(result.getStatus()).isEqualTo("DRAFT");
        }

        @Test
        @DisplayName("DRAFT 상태 테넌트 게시 중단 시도 시 예외 발생")
        void unpublishTenant_NotPublished() {
            // given
            Tenant draftTenant = Tenant.builder()
                    .tenantId("draft-tenant")
                    .status(SiteStatus.DRAFT)
                    .build();

            given(tenantRepository.findById("draft-tenant"))
                    .willReturn(Optional.of(draftTenant));

            // when & then
            assertThatThrownBy(() -> tenantService.unpublishTenant("draft-tenant", "admin", "사유"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PUBLISHED 상태에서만");
        }
    }

    @Nested
    @DisplayName("suspendTenant / resumeTenant / archiveTenant")
    class OtherStatusChanges {

        @Test
        @DisplayName("PUBLISHED 테넌트 일시중지 성공")
        void suspendTenant_Success() {
            // given
            Tenant publishedTenant = Tenant.builder()
                    .tenantId("published-tenant")
                    .status(SiteStatus.PUBLISHED)
                    .build();

            TenantDto suspendedDto = TenantDto.builder()
                    .tenantId("published-tenant")
                    .status("SUSPENDED")
                    .build();

            given(tenantRepository.findById("published-tenant"))
                    .willReturn(Optional.of(publishedTenant));
            given(tenantRepository.save(any(Tenant.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));
            given(tenantDtoMapper.toDto(any(Tenant.class)))
                    .willReturn(suspendedDto);

            // when
            TenantDto result = tenantService.suspendTenant("published-tenant", "admin", "정책 위반");

            // then
            assertThat(result.getStatus()).isEqualTo("SUSPENDED");
        }

        @Test
        @DisplayName("SUSPENDED 테넌트 재개 성공")
        void resumeTenant_Success() {
            // given
            Tenant suspendedTenant = Tenant.builder()
                    .tenantId("suspended-tenant")
                    .status(SiteStatus.SUSPENDED)
                    .build();

            TenantDto resumedDto = TenantDto.builder()
                    .tenantId("suspended-tenant")
                    .status("PUBLISHED")
                    .build();

            given(tenantRepository.findById("suspended-tenant"))
                    .willReturn(Optional.of(suspendedTenant));
            given(tenantRepository.save(any(Tenant.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));
            given(tenantDtoMapper.toDto(any(Tenant.class)))
                    .willReturn(resumedDto);

            // when
            TenantDto result = tenantService.resumeTenant("suspended-tenant", "admin");

            // then
            assertThat(result.getStatus()).isEqualTo("PUBLISHED");
        }

        @Test
        @DisplayName("DRAFT 테넌트 보관 성공")
        void archiveTenant_Success() {
            // given
            Tenant draftTenant = Tenant.builder()
                    .tenantId("draft-tenant")
                    .status(SiteStatus.DRAFT)
                    .build();

            TenantDto archivedDto = TenantDto.builder()
                    .tenantId("draft-tenant")
                    .status("ARCHIVED")
                    .build();

            given(tenantRepository.findById("draft-tenant"))
                    .willReturn(Optional.of(draftTenant));
            given(tenantRepository.save(any(Tenant.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));
            given(tenantDtoMapper.toDto(any(Tenant.class)))
                    .willReturn(archivedDto);

            // when
            TenantDto result = tenantService.archiveTenant("draft-tenant", "admin");

            // then
            assertThat(result.getStatus()).isEqualTo("ARCHIVED");
        }
    }
}
