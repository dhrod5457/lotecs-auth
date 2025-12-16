package lotecs.auth.infrastructure.grpc;

import com.google.protobuf.ListValue;
import com.google.protobuf.NullValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Protobuf Struct와 Java Map 간의 변환을 담당하는 유틸리티 클래스.
 * gRPC 통신에서 동적 데이터 구조를 처리할 때 사용한다.
 */
public class StructConverter {

    private StructConverter() {
        // 유틸리티 클래스이므로 인스턴스화 방지
    }

    /**
     * Protobuf Struct를 Java Map으로 변환한다.
     *
     * @param struct 변환할 Struct (null 허용)
     * @return 변환된 Map, struct가 null이면 null 반환
     */
    public static Map<String, Object> toMap(Struct struct) {
        if (struct == null) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Value> entry : struct.getFieldsMap().entrySet()) {
            result.put(entry.getKey(), toObject(entry.getValue()));
        }
        return result;
    }

    /**
     * Java Map을 Protobuf Struct로 변환한다.
     *
     * @param map 변환할 Map (null 허용)
     * @return 변환된 Struct, map이 null이면 null 반환
     */
    public static Struct toStruct(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        Struct.Builder builder = Struct.newBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            builder.putFields(entry.getKey(), toValue(entry.getValue()));
        }
        return builder.build();
    }

    /**
     * Protobuf Value를 Java Object로 변환한다.
     */
    private static Object toObject(Value value) {
        switch (value.getKindCase()) {
            case NULL_VALUE:
                return null;
            case NUMBER_VALUE:
                return value.getNumberValue();
            case STRING_VALUE:
                return value.getStringValue();
            case BOOL_VALUE:
                return value.getBoolValue();
            case STRUCT_VALUE:
                return toMap(value.getStructValue());
            case LIST_VALUE:
                return toList(value.getListValue());
            default:
                return null;
        }
    }

    /**
     * Protobuf ListValue를 Java List로 변환한다.
     */
    private static List<Object> toList(ListValue listValue) {
        List<Object> result = new ArrayList<>();
        for (Value value : listValue.getValuesList()) {
            result.add(toObject(value));
        }
        return result;
    }

    /**
     * Java Object를 Protobuf Value로 변환한다.
     */
    private static Value toValue(Object obj) {
        Value.Builder builder = Value.newBuilder();

        if (obj == null) {
            builder.setNullValue(NullValue.NULL_VALUE);
        } else if (obj instanceof String) {
            builder.setStringValue((String) obj);
        } else if (obj instanceof Number) {
            builder.setNumberValue(((Number) obj).doubleValue());
        } else if (obj instanceof Boolean) {
            builder.setBoolValue((Boolean) obj);
        } else if (obj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) obj;
            builder.setStructValue(toStruct(map));
        } else if (obj instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) obj;
            builder.setListValue(toListValue(list));
        } else {
            // 기타 타입은 문자열로 변환
            builder.setStringValue(obj.toString());
        }

        return builder.build();
    }

    /**
     * Java List를 Protobuf ListValue로 변환한다.
     */
    private static ListValue toListValue(List<Object> list) {
        ListValue.Builder builder = ListValue.newBuilder();
        for (Object item : list) {
            builder.addValues(toValue(item));
        }
        return builder.build();
    }
}
