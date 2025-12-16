package lotecs.auth.infrastructure.grpc;

import com.google.protobuf.ListValue;
import com.google.protobuf.NullValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StructConverter 테스트")
class StructConverterTest {

    @Nested
    @DisplayName("toMap - Struct를 Map으로 변환")
    class ToMapTest {

        @Test
        @DisplayName("null Struct는 null Map을 반환한다")
        void shouldReturnNull_whenStructIsNull() {
            // Given
            Struct struct = null;

            // When
            Map<String, Object> result = StructConverter.toMap(struct);

            // Then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 Struct는 빈 Map을 반환한다")
        void shouldReturnEmptyMap_whenStructIsEmpty() {
            // Given
            Struct struct = Struct.getDefaultInstance();

            // When
            Map<String, Object> result = StructConverter.toMap(struct);

            // Then
            assertThat(result).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("문자열 값을 올바르게 변환한다")
        void shouldConvertStringValue() {
            // Given
            Struct struct = Struct.newBuilder()
                    .putFields("name", Value.newBuilder().setStringValue("홍길동").build())
                    .build();

            // When
            Map<String, Object> result = StructConverter.toMap(struct);

            // Then
            assertThat(result).containsEntry("name", "홍길동");
        }

        @Test
        @DisplayName("숫자 값을 올바르게 변환한다")
        void shouldConvertNumberValue() {
            // Given
            Struct struct = Struct.newBuilder()
                    .putFields("age", Value.newBuilder().setNumberValue(25.0).build())
                    .putFields("score", Value.newBuilder().setNumberValue(95.5).build())
                    .build();

            // When
            Map<String, Object> result = StructConverter.toMap(struct);

            // Then
            assertThat(result).containsEntry("age", 25.0);
            assertThat(result).containsEntry("score", 95.5);
        }

        @Test
        @DisplayName("boolean 값을 올바르게 변환한다")
        void shouldConvertBooleanValue() {
            // Given
            Struct struct = Struct.newBuilder()
                    .putFields("active", Value.newBuilder().setBoolValue(true).build())
                    .putFields("deleted", Value.newBuilder().setBoolValue(false).build())
                    .build();

            // When
            Map<String, Object> result = StructConverter.toMap(struct);

            // Then
            assertThat(result).containsEntry("active", true);
            assertThat(result).containsEntry("deleted", false);
        }

        @Test
        @DisplayName("null 값을 올바르게 변환한다")
        void shouldConvertNullValue() {
            // Given
            Struct struct = Struct.newBuilder()
                    .putFields("empty", Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build())
                    .build();

            // When
            Map<String, Object> result = StructConverter.toMap(struct);

            // Then
            assertThat(result).containsEntry("empty", null);
        }

        @Test
        @DisplayName("중첩 객체를 올바르게 변환한다")
        void shouldConvertNestedObject() {
            // Given
            Struct nested = Struct.newBuilder()
                    .putFields("city", Value.newBuilder().setStringValue("서울").build())
                    .putFields("zipCode", Value.newBuilder().setStringValue("12345").build())
                    .build();

            Struct struct = Struct.newBuilder()
                    .putFields("address", Value.newBuilder().setStructValue(nested).build())
                    .build();

            // When
            Map<String, Object> result = StructConverter.toMap(struct);

            // Then
            assertThat(result).containsKey("address");
            @SuppressWarnings("unchecked")
            Map<String, Object> addressMap = (Map<String, Object>) result.get("address");
            assertThat(addressMap).containsEntry("city", "서울");
            assertThat(addressMap).containsEntry("zipCode", "12345");
        }

        @Test
        @DisplayName("배열을 올바르게 변환한다")
        void shouldConvertListValue() {
            // Given
            ListValue listValue = ListValue.newBuilder()
                    .addValues(Value.newBuilder().setStringValue("ADMIN").build())
                    .addValues(Value.newBuilder().setStringValue("USER").build())
                    .build();

            Struct struct = Struct.newBuilder()
                    .putFields("roles", Value.newBuilder().setListValue(listValue).build())
                    .build();

            // When
            Map<String, Object> result = StructConverter.toMap(struct);

            // Then
            assertThat(result).containsKey("roles");
            @SuppressWarnings("unchecked")
            List<Object> roles = (List<Object>) result.get("roles");
            assertThat(roles).containsExactly("ADMIN", "USER");
        }
    }

    @Nested
    @DisplayName("toStruct - Map을 Struct로 변환")
    class ToStructTest {

        @Test
        @DisplayName("null Map은 null Struct를 반환한다")
        void shouldReturnNull_whenMapIsNull() {
            // Given
            Map<String, Object> map = null;

            // When
            Struct result = StructConverter.toStruct(map);

            // Then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 Map은 빈 Struct를 반환한다")
        void shouldReturnEmptyStruct_whenMapIsEmpty() {
            // Given
            Map<String, Object> map = Map.of();

            // When
            Struct result = StructConverter.toStruct(map);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getFieldsCount()).isZero();
        }

        @Test
        @DisplayName("문자열 값을 올바르게 변환한다")
        void shouldConvertStringValue() {
            // Given
            Map<String, Object> map = Map.of("name", "홍길동");

            // When
            Struct result = StructConverter.toStruct(map);

            // Then
            assertThat(result.getFieldsOrThrow("name").getStringValue()).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("숫자 값을 올바르게 변환한다")
        void shouldConvertNumberValue() {
            // Given
            Map<String, Object> map = Map.of(
                    "intValue", 25,
                    "doubleValue", 95.5
            );

            // When
            Struct result = StructConverter.toStruct(map);

            // Then
            assertThat(result.getFieldsOrThrow("intValue").getNumberValue()).isEqualTo(25.0);
            assertThat(result.getFieldsOrThrow("doubleValue").getNumberValue()).isEqualTo(95.5);
        }

        @Test
        @DisplayName("boolean 값을 올바르게 변환한다")
        void shouldConvertBooleanValue() {
            // Given
            Map<String, Object> map = Map.of("active", true);

            // When
            Struct result = StructConverter.toStruct(map);

            // Then
            assertThat(result.getFieldsOrThrow("active").getBoolValue()).isTrue();
        }

        @Test
        @DisplayName("중첩 Map을 올바르게 변환한다")
        void shouldConvertNestedMap() {
            // Given
            Map<String, Object> nested = Map.of("city", "서울");
            Map<String, Object> map = Map.of("address", nested);

            // When
            Struct result = StructConverter.toStruct(map);

            // Then
            Struct addressStruct = result.getFieldsOrThrow("address").getStructValue();
            assertThat(addressStruct.getFieldsOrThrow("city").getStringValue()).isEqualTo("서울");
        }

        @Test
        @DisplayName("List를 올바르게 변환한다")
        void shouldConvertList() {
            // Given
            Map<String, Object> map = Map.of("roles", List.of("ADMIN", "USER"));

            // When
            Struct result = StructConverter.toStruct(map);

            // Then
            ListValue listValue = result.getFieldsOrThrow("roles").getListValue();
            assertThat(listValue.getValuesCount()).isEqualTo(2);
            assertThat(listValue.getValues(0).getStringValue()).isEqualTo("ADMIN");
            assertThat(listValue.getValues(1).getStringValue()).isEqualTo("USER");
        }
    }

    @Nested
    @DisplayName("양방향 변환 테스트")
    class RoundTripTest {

        @Test
        @DisplayName("Map -> Struct -> Map 변환 시 데이터가 유지된다")
        void shouldPreserveData_whenRoundTrip() {
            // Given
            Map<String, Object> original = Map.of(
                    "name", "홍길동",
                    "age", 25,
                    "active", true,
                    "roles", List.of("ADMIN", "USER")
            );

            // When
            Struct struct = StructConverter.toStruct(original);
            Map<String, Object> result = StructConverter.toMap(struct);

            // Then
            assertThat(result).containsEntry("name", "홍길동");
            assertThat(result).containsEntry("age", 25.0); // Number는 double로 변환됨
            assertThat(result).containsEntry("active", true);
            @SuppressWarnings("unchecked")
            List<Object> roles = (List<Object>) result.get("roles");
            assertThat(roles).containsExactly("ADMIN", "USER");
        }
    }
}
