package lotecs.auth.infrastructure.relay;

import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import lotecs.relay.auth.grpc.v1.AuthenticateRequest;
import lotecs.relay.auth.grpc.v1.AuthenticateResponse;
import lotecs.relay.auth.grpc.v1.RelayAuthServiceGrpc;
import lotecs.relay.auth.grpc.v1.RelayUserInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RelayClient gRPC 통합 테스트.
 * InProcess gRPC 서버를 사용하여 실제 gRPC 통신을 테스트한다.
 */
@DisplayName("RelayClient 통합 테스트")
class RelayClientIntegrationTest {

    private Server server;
    private ManagedChannel channel;
    private TestRelayAuthService testService;

    @BeforeEach
    void setUp() throws Exception {
        String serverName = InProcessServerBuilder.generateName();
        testService = new TestRelayAuthService();

        server = InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(testService)
                .build()
                .start();

        channel = InProcessChannelBuilder.forName(serverName)
                .directExecutor()
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        server.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }

    @Nested
    @DisplayName("인증 성공 시나리오")
    class AuthenticationSuccessTest {

        @Test
        @DisplayName("기본 인증 성공 - 사용자 정보가 올바르게 변환된다")
        void shouldReturnUserInfo_whenAuthenticationSucceeds() {
            // Given
            RelayUserInfo userInfo = RelayUserInfo.newBuilder()
                    .setUserId("user001")
                    .setUserName("홍길동")
                    .setEmail("hong@university.ac.kr")
                    .setUserType("STUDENT")
                    .setDepartment("컴퓨터공학과")
                    .addAllRoles(List.of("ROLE_USER", "ROLE_STUDENT"))
                    .build();

            testService.setResponse(AuthenticateResponse.newBuilder()
                    .setSuccess(true)
                    .setUserInfo(userInfo)
                    .build());

            // When
            RelayAuthServiceGrpc.RelayAuthServiceBlockingStub stub =
                    RelayAuthServiceGrpc.newBlockingStub(channel);

            AuthenticateResponse response = stub.authenticate(
                    AuthenticateRequest.newBuilder()
                            .setTenantId("sejong")
                            .setUserId("user001")
                            .setPassword("password123")
                            .build()
            );

            // Then
            assertThat(response.getSuccess()).isTrue();

            RelayUserInfo resultUser = response.getUserInfo();
            assertThat(resultUser.getUserId()).isEqualTo("user001");
            assertThat(resultUser.getUserName()).isEqualTo("홍길동");
            assertThat(resultUser.getEmail()).isEqualTo("hong@university.ac.kr");
            assertThat(resultUser.getUserType()).isEqualTo("STUDENT");
            assertThat(resultUser.getDepartment()).isEqualTo("컴퓨터공학과");
            assertThat(resultUser.getRolesList()).containsExactly("ROLE_USER", "ROLE_STUDENT");
        }

        @Test
        @DisplayName("additionalData가 포함된 인증 성공 - Struct가 올바르게 전달된다")
        void shouldReceiveAdditionalData_whenAuthenticationSucceeds() {
            // Given
            Struct additionalData = Struct.newBuilder()
                    .putFields("studentId", Value.newBuilder().setStringValue("20231234").build())
                    .putFields("grade", Value.newBuilder().setNumberValue(3.0).build())
                    .putFields("isScholarship", Value.newBuilder().setBoolValue(true).build())
                    .putFields("gpa", Value.newBuilder().setNumberValue(3.85).build())
                    .build();

            RelayUserInfo userInfo = RelayUserInfo.newBuilder()
                    .setUserId("user001")
                    .setUserName("홍길동")
                    .setUserType("STUDENT")
                    .setDepartment("컴퓨터공학과")
                    .addRoles("ROLE_USER")
                    .setAdditionalData(additionalData)
                    .build();

            testService.setResponse(AuthenticateResponse.newBuilder()
                    .setSuccess(true)
                    .setUserInfo(userInfo)
                    .build());

            // When
            RelayAuthServiceGrpc.RelayAuthServiceBlockingStub stub =
                    RelayAuthServiceGrpc.newBlockingStub(channel);

            AuthenticateResponse response = stub.authenticate(
                    AuthenticateRequest.newBuilder()
                            .setTenantId("sejong")
                            .setUserId("user001")
                            .setPassword("password123")
                            .build()
            );

            // Then
            assertThat(response.getSuccess()).isTrue();
            assertThat(response.getUserInfo().hasAdditionalData()).isTrue();

            Struct struct = response.getUserInfo().getAdditionalData();
            assertThat(struct.getFieldsOrThrow("studentId").getStringValue()).isEqualTo("20231234");
            assertThat(struct.getFieldsOrThrow("grade").getNumberValue()).isEqualTo(3.0);
            assertThat(struct.getFieldsOrThrow("isScholarship").getBoolValue()).isTrue();
            assertThat(struct.getFieldsOrThrow("gpa").getNumberValue()).isEqualTo(3.85);
        }

        @Test
        @DisplayName("중첩된 additionalData - Struct 내 Struct가 올바르게 전달된다")
        void shouldReceiveNestedAdditionalData_whenAuthenticationSucceeds() {
            // Given
            Struct addressStruct = Struct.newBuilder()
                    .putFields("city", Value.newBuilder().setStringValue("서울").build())
                    .putFields("district", Value.newBuilder().setStringValue("광진구").build())
                    .build();

            Struct additionalData = Struct.newBuilder()
                    .putFields("studentId", Value.newBuilder().setStringValue("20231234").build())
                    .putFields("address", Value.newBuilder().setStructValue(addressStruct).build())
                    .build();

            RelayUserInfo userInfo = RelayUserInfo.newBuilder()
                    .setUserId("user001")
                    .setUserName("홍길동")
                    .setUserType("STUDENT")
                    .setDepartment("컴퓨터공학과")
                    .addRoles("ROLE_USER")
                    .setAdditionalData(additionalData)
                    .build();

            testService.setResponse(AuthenticateResponse.newBuilder()
                    .setSuccess(true)
                    .setUserInfo(userInfo)
                    .build());

            // When
            RelayAuthServiceGrpc.RelayAuthServiceBlockingStub stub =
                    RelayAuthServiceGrpc.newBlockingStub(channel);

            AuthenticateResponse response = stub.authenticate(
                    AuthenticateRequest.newBuilder()
                            .setTenantId("sejong")
                            .setUserId("user001")
                            .setPassword("password123")
                            .build()
            );

            // Then
            assertThat(response.getSuccess()).isTrue();

            Struct struct = response.getUserInfo().getAdditionalData();
            assertThat(struct.getFieldsOrThrow("studentId").getStringValue()).isEqualTo("20231234");

            Struct nestedAddress = struct.getFieldsOrThrow("address").getStructValue();
            assertThat(nestedAddress.getFieldsOrThrow("city").getStringValue()).isEqualTo("서울");
            assertThat(nestedAddress.getFieldsOrThrow("district").getStringValue()).isEqualTo("광진구");
        }
    }

    @Nested
    @DisplayName("인증 실패 시나리오")
    class AuthenticationFailureTest {

        @Test
        @DisplayName("잘못된 비밀번호 - 에러 응답이 반환된다")
        void shouldReturnError_whenPasswordIsInvalid() {
            // Given
            testService.setResponse(AuthenticateResponse.newBuilder()
                    .setSuccess(false)
                    .setErrorCode("AUTH001")
                    .setErrorMessage("비밀번호가 일치하지 않습니다")
                    .build());

            // When
            RelayAuthServiceGrpc.RelayAuthServiceBlockingStub stub =
                    RelayAuthServiceGrpc.newBlockingStub(channel);

            AuthenticateResponse response = stub.authenticate(
                    AuthenticateRequest.newBuilder()
                            .setTenantId("sejong")
                            .setUserId("user001")
                            .setPassword("wrongpassword")
                            .build()
            );

            // Then
            assertThat(response.getSuccess()).isFalse();
            assertThat(response.getErrorCode()).isEqualTo("AUTH001");
            assertThat(response.getErrorMessage()).isEqualTo("비밀번호가 일치하지 않습니다");
            assertThat(response.hasUserInfo()).isFalse();
        }

        @Test
        @DisplayName("사용자 없음 - 에러 응답이 반환된다")
        void shouldReturnError_whenUserNotFound() {
            // Given
            testService.setResponse(AuthenticateResponse.newBuilder()
                    .setSuccess(false)
                    .setErrorCode("AUTH004")
                    .setErrorMessage("사용자를 찾을 수 없습니다")
                    .build());

            // When
            RelayAuthServiceGrpc.RelayAuthServiceBlockingStub stub =
                    RelayAuthServiceGrpc.newBlockingStub(channel);

            AuthenticateResponse response = stub.authenticate(
                    AuthenticateRequest.newBuilder()
                            .setTenantId("sejong")
                            .setUserId("unknown_user")
                            .setPassword("password123")
                            .build()
            );

            // Then
            assertThat(response.getSuccess()).isFalse();
            assertThat(response.getErrorCode()).isEqualTo("AUTH004");
            assertThat(response.getErrorMessage()).isEqualTo("사용자를 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("요청 검증")
    class RequestValidationTest {

        @Test
        @DisplayName("요청 정보가 gRPC 서버에 올바르게 전달된다")
        void shouldPassRequestToServer_correctly() {
            // Given
            testService.setResponse(AuthenticateResponse.newBuilder()
                    .setSuccess(true)
                    .setUserInfo(RelayUserInfo.newBuilder()
                            .setUserId("user001")
                            .setUserName("홍길동")
                            .build())
                    .build());

            // When
            RelayAuthServiceGrpc.RelayAuthServiceBlockingStub stub =
                    RelayAuthServiceGrpc.newBlockingStub(channel);

            stub.authenticate(
                    AuthenticateRequest.newBuilder()
                            .setTenantId("sejong")
                            .setUserId("user001")
                            .setPassword("password123")
                            .setIpAddress("192.168.1.100")
                            .build()
            );

            // Then
            AuthenticateRequest receivedRequest = testService.getLastRequest();
            assertThat(receivedRequest.getTenantId()).isEqualTo("sejong");
            assertThat(receivedRequest.getUserId()).isEqualTo("user001");
            assertThat(receivedRequest.getPassword()).isEqualTo("password123");
            assertThat(receivedRequest.getIpAddress()).isEqualTo("192.168.1.100");
        }
    }

    /**
     * 테스트용 RelayAuthService 구현
     */
    static class TestRelayAuthService extends RelayAuthServiceGrpc.RelayAuthServiceImplBase {
        private AuthenticateResponse response;
        private AuthenticateRequest lastRequest;

        void setResponse(AuthenticateResponse response) {
            this.response = response;
        }

        AuthenticateRequest getLastRequest() {
            return lastRequest;
        }

        @Override
        public void authenticate(AuthenticateRequest request, StreamObserver<AuthenticateResponse> responseObserver) {
            this.lastRequest = request;
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
