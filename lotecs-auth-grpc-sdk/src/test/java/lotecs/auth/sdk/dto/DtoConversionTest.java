package lotecs.auth.sdk.dto;

import lotecs.auth.sdk.dto.auth.*;
import lotecs.auth.sdk.dto.tenant.*;
import lotecs.auth.sdk.dto.rolestatus.*;
import lotecs.auth.sdk.dto.organization.*;
import lotecs.auth.sdk.dto.userorganization.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DTO 변환 단위 테스트")
class DtoConversionTest {

    @Nested
    @DisplayName("Auth DTO 변환 테스트")
    class AuthDtoConversionTest {

        @Test
        @DisplayName("LoginRequest를 Proto로 변환해야 한다")
        void shouldConvertLoginRequestToProto() {
            LoginRequest request = LoginRequest.builder()
                    .username("testuser")
                    .password("password123")
                    .tenantId("TENANT001")
                    .ipAddress("192.168.1.1")
                    .build();

            com.lotecs.auth.grpc.LoginRequest proto = request.toProto();

            assertThat(proto.getUsername()).isEqualTo("testuser");
            assertThat(proto.getPassword()).isEqualTo("password123");
            assertThat(proto.getTenantId()).isEqualTo("TENANT001");
            assertThat(proto.getIpAddress()).isEqualTo("192.168.1.1");
        }

        @Test
        @DisplayName("LoginRequest에서 null 값을 빈 문자열로 변환해야 한다")
        void shouldConvertNullToEmptyStringInLoginRequest() {
            LoginRequest request = LoginRequest.builder()
                    .username("testuser")
                    .build();

            com.lotecs.auth.grpc.LoginRequest proto = request.toProto();

            assertThat(proto.getUsername()).isEqualTo("testuser");
            assertThat(proto.getPassword()).isEmpty();
            assertThat(proto.getTenantId()).isEmpty();
            assertThat(proto.getIpAddress()).isEmpty();
        }

        @Test
        @DisplayName("CreateUserRequest에 역할 목록을 포함해야 한다")
        void shouldIncludeRolesInCreateUserRequest() {
            List<String> roles = Arrays.asList("ADMIN", "USER");
            CreateUserRequest request = CreateUserRequest.builder()
                    .tenantId("TENANT001")
                    .username("newuser")
                    .password("password")
                    .email("test@example.com")
                    .fullName("Test User")
                    .roles(roles)
                    .build();

            com.lotecs.auth.grpc.CreateUserRequest proto = request.toProto();

            assertThat(proto.getRolesList()).containsExactly("ADMIN", "USER");
        }

        @Test
        @DisplayName("ValidateTokenRequest를 Proto로 변환해야 한다")
        void shouldConvertValidateTokenRequestToProto() {
            ValidateTokenRequest request = ValidateTokenRequest.builder()
                    .accessToken("token123")
                    .build();

            com.lotecs.auth.grpc.ValidateTokenRequest proto = request.toProto();

            assertThat(proto.getAccessToken()).isEqualTo("token123");
        }

        @Test
        @DisplayName("PermissionCheckRequest를 Proto로 변환해야 한다")
        void shouldConvertPermissionCheckRequestToProto() {
            PermissionCheckRequest request = PermissionCheckRequest.builder()
                    .userId("USER001")
                    .tenantId("TENANT001")
                    .permissionCode("ADMIN_READ")
                    .build();

            com.lotecs.auth.grpc.PermissionCheckRequest proto = request.toProto();

            assertThat(proto.getUserId()).isEqualTo("USER001");
            assertThat(proto.getTenantId()).isEqualTo("TENANT001");
            assertThat(proto.getPermissionCode()).isEqualTo("ADMIN_READ");
        }
    }

    @Nested
    @DisplayName("Tenant DTO 변환 테스트")
    class TenantDtoConversionTest {

        @Test
        @DisplayName("CreateTenantRequest를 Proto로 변환해야 한다")
        void shouldConvertCreateTenantRequestToProto() {
            CreateTenantRequest request = CreateTenantRequest.builder()
                    .siteName("Test Site")
                    .siteCode("TESTSITE")
                    .primaryDomain("test.example.com")
                    .defaultLanguage("ko")
                    .timezone("Asia/Seoul")
                    .maxUsers(100)
                    .createdBy("admin")
                    .build();

            com.lotecs.auth.grpc.CreateTenantRequest proto = request.toProto();

            assertThat(proto.getSiteName()).isEqualTo("Test Site");
            assertThat(proto.getSiteCode()).isEqualTo("TESTSITE");
            assertThat(proto.getPrimaryDomain()).isEqualTo("test.example.com");
            assertThat(proto.getDefaultLanguage()).isEqualTo("ko");
            assertThat(proto.getTimezone()).isEqualTo("Asia/Seoul");
            assertThat(proto.getMaxUsers()).isEqualTo(100);
            assertThat(proto.getCreatedBy()).isEqualTo("admin");
        }

        @Test
        @DisplayName("GetTenantRequest를 Proto로 변환해야 한다")
        void shouldConvertGetTenantRequestToProto() {
            GetTenantRequest request = GetTenantRequest.builder()
                    .tenantId("TENANT001")
                    .build();

            com.lotecs.auth.grpc.GetTenantRequest proto = request.toProto();

            assertThat(proto.getTenantId()).isEqualTo("TENANT001");
        }

        @Test
        @DisplayName("PublishTenantRequest를 Proto로 변환해야 한다")
        void shouldConvertPublishTenantRequestToProto() {
            PublishTenantRequest request = PublishTenantRequest.builder()
                    .tenantId("TENANT001")
                    .updatedBy("admin")
                    .build();

            com.lotecs.auth.grpc.PublishTenantRequest proto = request.toProto();

            assertThat(proto.getTenantId()).isEqualTo("TENANT001");
            assertThat(proto.getUpdatedBy()).isEqualTo("admin");
        }
    }

    @Nested
    @DisplayName("RoleStatus DTO 변환 테스트")
    class RoleStatusDtoConversionTest {

        @Test
        @DisplayName("CreateRoleStatusRequest를 Proto로 변환해야 한다")
        void shouldConvertCreateRoleStatusRequestToProto() {
            CreateRoleStatusRequest request = CreateRoleStatusRequest.builder()
                    .statusCode("ACTIVE")
                    .statusName("활성")
                    .roleCategory("SYSTEM")
                    .description("활성 상태")
                    .active(true)
                    .sortOrder(1)
                    .isDefault(true)
                    .createdBy("admin")
                    .build();

            com.lotecs.auth.grpc.CreateRoleStatusRequest proto = request.toProto();

            assertThat(proto.getStatusCode()).isEqualTo("ACTIVE");
            assertThat(proto.getStatusName()).isEqualTo("활성");
            assertThat(proto.getRoleCategory()).isEqualTo("SYSTEM");
            assertThat(proto.getIsActive()).isTrue();
            assertThat(proto.getSortOrder()).isEqualTo(1);
            assertThat(proto.getIsDefault()).isTrue();
        }
    }

    @Nested
    @DisplayName("Organization DTO 변환 테스트")
    class OrganizationDtoConversionTest {

        @Test
        @DisplayName("SyncOrganizationRequest를 Proto로 변환해야 한다")
        void shouldConvertSyncOrganizationRequestToProto() {
            SyncOrganizationRequest request = SyncOrganizationRequest.builder()
                    .tenantId("TENANT001")
                    .organizationId("ORG001")
                    .organizationCode("DEV")
                    .organizationName("개발팀")
                    .organizationType("DEPARTMENT")
                    .parentOrganizationId("ROOT")
                    .orgLevel(2)
                    .displayOrder(1)
                    .description("개발 부서")
                    .active(true)
                    .syncBy("system")
                    .build();

            com.lotecs.auth.grpc.SyncOrganizationRequest proto = request.toProto();

            assertThat(proto.getTenantId()).isEqualTo("TENANT001");
            assertThat(proto.getOrganizationId()).isEqualTo("ORG001");
            assertThat(proto.getOrganizationCode()).isEqualTo("DEV");
            assertThat(proto.getOrganizationName()).isEqualTo("개발팀");
            assertThat(proto.getOrganizationType()).isEqualTo("DEPARTMENT");
            assertThat(proto.getParentOrganizationId()).isEqualTo("ROOT");
            assertThat(proto.getOrgLevel()).isEqualTo(2);
            assertThat(proto.getActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("UserOrganization DTO 변환 테스트")
    class UserOrganizationDtoConversionTest {

        @Test
        @DisplayName("SyncUserOrganizationRequest를 Proto로 변환해야 한다")
        void shouldConvertSyncUserOrganizationRequestToProto() {
            SyncUserOrganizationRequest request = SyncUserOrganizationRequest.builder()
                    .tenantId("TENANT001")
                    .userId("USER001")
                    .organizationId("ORG001")
                    .roleId("MEMBER")
                    .primary(true)
                    .position("개발자")
                    .startDate("2024-01-01")
                    .endDate("2024-12-31")
                    .active(true)
                    .syncBy("system")
                    .build();

            com.lotecs.auth.grpc.SyncUserOrganizationRequest proto = request.toProto();

            assertThat(proto.getTenantId()).isEqualTo("TENANT001");
            assertThat(proto.getUserId()).isEqualTo("USER001");
            assertThat(proto.getOrganizationId()).isEqualTo("ORG001");
            assertThat(proto.getRoleId()).isEqualTo("MEMBER");
            assertThat(proto.getIsPrimary()).isTrue();
            assertThat(proto.getPosition()).isEqualTo("개발자");
            assertThat(proto.getStartDate()).isEqualTo("2024-01-01");
            assertThat(proto.getEndDate()).isEqualTo("2024-12-31");
            assertThat(proto.getActive()).isTrue();
        }
    }
}
