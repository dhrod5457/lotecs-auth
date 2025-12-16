package lotecs.auth.application.user.service;

import lotecs.auth.application.user.dto.RoleStatusDto;
import lotecs.auth.application.user.mapper.RoleStatusDtoMapper;
import lotecs.auth.domain.user.model.RoleStatus;
import lotecs.auth.domain.user.repository.RoleStatusRepository;
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
@DisplayName("RoleStatusService 단위 테스트")
class RoleStatusServiceTest {

    @Mock
    private RoleStatusRepository roleStatusRepository;

    @Mock
    private RoleStatusDtoMapper roleStatusDtoMapper;

    @InjectMocks
    private RoleStatusService roleStatusService;

    private RoleStatus testRoleStatus;
    private RoleStatusDto testRoleStatusDto;

    @BeforeEach
    void setUp() {
        testRoleStatus = RoleStatus.builder()
                .id(1L)
                .statusCode("ACTIVE")
                .statusName("활성")
                .roleCategory("SYSTEM")
                .description("활성 상태")
                .isActive(true)
                .sortOrder(1)
                .isDefault(true)
                .createdBy("admin")
                .createdAt(LocalDateTime.now())
                .build();

        testRoleStatusDto = RoleStatusDto.builder()
                .id(1L)
                .statusCode("ACTIVE")
                .statusName("활성")
                .roleCategory("SYSTEM")
                .description("활성 상태")
                .isActive(true)
                .sortOrder(1)
                .isDefault(true)
                .createdBy("admin")
                .build();
    }

    @Nested
    @DisplayName("getRoleStatus")
    class GetRoleStatus {

        @Test
        @DisplayName("존재하는 역할 상태 조회 성공")
        void getRoleStatus_Success() {
            // given
            given(roleStatusRepository.findByStatusCode("ACTIVE"))
                    .willReturn(Optional.of(testRoleStatus));
            given(roleStatusDtoMapper.toDto(testRoleStatus))
                    .willReturn(testRoleStatusDto);

            // when
            RoleStatusDto result = roleStatusService.getRoleStatus("ACTIVE");

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatusCode()).isEqualTo("ACTIVE");
            assertThat(result.getStatusName()).isEqualTo("활성");

            then(roleStatusRepository).should().findByStatusCode("ACTIVE");
        }

        @Test
        @DisplayName("존재하지 않는 역할 상태 조회 시 예외 발생")
        void getRoleStatus_NotFound() {
            // given
            given(roleStatusRepository.findByStatusCode("UNKNOWN"))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> roleStatusService.getRoleStatus("UNKNOWN"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Role status not found");
        }
    }

    @Nested
    @DisplayName("getAllRoleStatuses / getActiveRoleStatuses / getRoleStatusesByCategory")
    class ListRoleStatuses {

        @Test
        @DisplayName("전체 역할 상태 목록 조회")
        void getAllRoleStatuses_Success() {
            // given
            List<RoleStatus> roleStatuses = Arrays.asList(testRoleStatus);
            List<RoleStatusDto> roleStatusDtos = Arrays.asList(testRoleStatusDto);

            given(roleStatusRepository.findAll()).willReturn(roleStatuses);
            given(roleStatusDtoMapper.toDtoList(roleStatuses)).willReturn(roleStatusDtos);

            // when
            List<RoleStatusDto> result = roleStatusService.getAllRoleStatuses();

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatusCode()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("활성 역할 상태 목록 조회")
        void getActiveRoleStatuses_Success() {
            // given
            List<RoleStatus> roleStatuses = Arrays.asList(testRoleStatus);
            List<RoleStatusDto> roleStatusDtos = Arrays.asList(testRoleStatusDto);

            given(roleStatusRepository.findActive()).willReturn(roleStatuses);
            given(roleStatusDtoMapper.toDtoList(roleStatuses)).willReturn(roleStatusDtos);

            // when
            List<RoleStatusDto> result = roleStatusService.getActiveRoleStatuses();

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getIsActive()).isTrue();
        }

        @Test
        @DisplayName("카테고리별 역할 상태 목록 조회")
        void getRoleStatusesByCategory_Success() {
            // given
            List<RoleStatus> roleStatuses = Arrays.asList(testRoleStatus);
            List<RoleStatusDto> roleStatusDtos = Arrays.asList(testRoleStatusDto);

            given(roleStatusRepository.findByRoleCategory("SYSTEM")).willReturn(roleStatuses);
            given(roleStatusDtoMapper.toDtoList(roleStatuses)).willReturn(roleStatusDtos);

            // when
            List<RoleStatusDto> result = roleStatusService.getRoleStatusesByCategory("SYSTEM");

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getRoleCategory()).isEqualTo("SYSTEM");
        }
    }

    @Nested
    @DisplayName("createRoleStatus")
    class CreateRoleStatus {

        @Test
        @DisplayName("역할 상태 생성 성공")
        void createRoleStatus_Success() {
            // given
            RoleStatusDto createRequest = RoleStatusDto.builder()
                    .statusCode("NEW_STATUS")
                    .statusName("새 상태")
                    .roleCategory("COMMON")
                    .description("새로 생성된 상태")
                    .isActive(true)
                    .sortOrder(10)
                    .isDefault(false)
                    .build();

            RoleStatusDto createdDto = RoleStatusDto.builder()
                    .id(2L)
                    .statusCode("NEW_STATUS")
                    .statusName("새 상태")
                    .roleCategory("COMMON")
                    .description("새로 생성된 상태")
                    .isActive(true)
                    .sortOrder(10)
                    .isDefault(false)
                    .createdBy("admin")
                    .build();

            given(roleStatusRepository.findByStatusCode("NEW_STATUS"))
                    .willReturn(Optional.empty());
            given(roleStatusRepository.save(any(RoleStatus.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));
            given(roleStatusDtoMapper.toDto(any(RoleStatus.class)))
                    .willReturn(createdDto);

            // when
            RoleStatusDto result = roleStatusService.createRoleStatus(createRequest, "admin");

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatusCode()).isEqualTo("NEW_STATUS");
            assertThat(result.getRoleCategory()).isEqualTo("COMMON");

            then(roleStatusRepository).should().save(any(RoleStatus.class));
        }

        @Test
        @DisplayName("중복된 상태 코드로 생성 시 예외 발생")
        void createRoleStatus_DuplicateStatusCode() {
            // given
            RoleStatusDto createRequest = RoleStatusDto.builder()
                    .statusCode("ACTIVE")
                    .statusName("중복 상태")
                    .roleCategory("SYSTEM")
                    .build();

            given(roleStatusRepository.findByStatusCode("ACTIVE"))
                    .willReturn(Optional.of(testRoleStatus));

            // when & then
            assertThatThrownBy(() -> roleStatusService.createRoleStatus(createRequest, "admin"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Status code already exists");
        }
    }

    @Nested
    @DisplayName("updateRoleStatus")
    class UpdateRoleStatus {

        @Test
        @DisplayName("역할 상태 수정 성공")
        void updateRoleStatus_Success() {
            // given
            RoleStatusDto updateRequest = RoleStatusDto.builder()
                    .statusName("수정된 상태명")
                    .description("수정된 설명")
                    .sortOrder(5)
                    .build();

            given(roleStatusRepository.findByStatusCode("ACTIVE"))
                    .willReturn(Optional.of(testRoleStatus));
            willDoNothing().given(roleStatusDtoMapper).updateEntity(updateRequest, testRoleStatus);
            given(roleStatusRepository.save(testRoleStatus)).willReturn(testRoleStatus);
            given(roleStatusDtoMapper.toDto(testRoleStatus)).willReturn(testRoleStatusDto);

            // when
            RoleStatusDto result = roleStatusService.updateRoleStatus("ACTIVE", updateRequest, "admin");

            // then
            assertThat(result).isNotNull();
            then(roleStatusRepository).should().save(testRoleStatus);
        }

        @Test
        @DisplayName("존재하지 않는 역할 상태 수정 시 예외 발생")
        void updateRoleStatus_NotFound() {
            // given
            RoleStatusDto updateRequest = RoleStatusDto.builder()
                    .statusName("수정 시도")
                    .build();

            given(roleStatusRepository.findByStatusCode("UNKNOWN"))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> roleStatusService.updateRoleStatus("UNKNOWN", updateRequest, "admin"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Role status not found");
        }
    }

    @Nested
    @DisplayName("deleteRoleStatus")
    class DeleteRoleStatus {

        @Test
        @DisplayName("역할 상태 삭제 성공")
        void deleteRoleStatus_Success() {
            // given
            given(roleStatusRepository.findByStatusCode("ACTIVE"))
                    .willReturn(Optional.of(testRoleStatus));
            willDoNothing().given(roleStatusRepository).delete("ACTIVE");

            // when
            roleStatusService.deleteRoleStatus("ACTIVE");

            // then
            then(roleStatusRepository).should().delete("ACTIVE");
        }

        @Test
        @DisplayName("존재하지 않는 역할 상태 삭제 시 예외 발생")
        void deleteRoleStatus_NotFound() {
            // given
            given(roleStatusRepository.findByStatusCode("UNKNOWN"))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> roleStatusService.deleteRoleStatus("UNKNOWN"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Role status not found");
        }
    }
}
