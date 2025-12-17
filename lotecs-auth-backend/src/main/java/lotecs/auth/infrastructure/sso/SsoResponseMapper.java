package lotecs.auth.infrastructure.sso;

import lombok.extern.slf4j.Slf4j;
import lotecs.auth.domain.sso.model.TenantSsoConfig;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * SSO 응답 데이터를 표준 필드로 매핑하는 컴포넌트.
 * 학교별로 다른 필드명을 가진 SSO 응답을 표준화된 키로 변환한다.
 *
 * <p>additionalConfig의 responseMapping 설정을 사용:</p>
 * <pre>
 * {
 *   "responseMapping": {
 *     "studentId": "HAKBUN",
 *     "department": "DEPT_NM",
 *     "grade": "GRADE"
 *   }
 * }
 * </pre>
 */
@Slf4j
@Component
public class SsoResponseMapper {

    private static final String RESPONSE_MAPPING_KEY = "responseMapping";
    private static final String CUSTOM_FIELDS_KEY = "customFields";

    /**
     * SSO 응답 데이터를 표준 필드로 매핑
     *
     * @param rawData    SSO 서버에서 받은 원본 데이터
     * @param ssoConfig  테넌트 SSO 설정
     * @return 표준화된 필드명으로 매핑된 데이터
     */
    public Map<String, Object> mapResponse(Map<String, Object> rawData, TenantSsoConfig ssoConfig) {
        if (rawData == null || rawData.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Object> additionalConfig = ssoConfig.getAdditionalConfigAsMap();
        Map<String, Object> result = new HashMap<>();

        // 1. 응답 매핑 적용
        Map<String, String> responseMapping = getResponseMapping(additionalConfig);
        if (!responseMapping.isEmpty()) {
            for (Map.Entry<String, String> entry : responseMapping.entrySet()) {
                String standardKey = entry.getKey();    // 표준 키 (예: studentId)
                String sourceKey = entry.getValue();     // 원본 키 (예: HAKBUN)

                if (rawData.containsKey(sourceKey)) {
                    result.put(standardKey, rawData.get(sourceKey));
                }
            }
        }

        // 2. 매핑되지 않은 원본 데이터도 포함 (선택적)
        for (Map.Entry<String, Object> entry : rawData.entrySet()) {
            String key = entry.getKey();
            // 이미 매핑된 키가 아니고, 원본 키로도 추가되지 않은 경우
            if (!result.containsKey(key) && !isSourceKey(key, responseMapping)) {
                result.put(key, entry.getValue());
            }
        }

        // 3. 커스텀 정적 필드 추가
        Map<String, Object> customFields = getCustomFields(additionalConfig);
        if (!customFields.isEmpty()) {
            result.putAll(customFields);
        }

        log.debug("SSO response mapped: rawKeys={}, resultKeys={}", rawData.keySet(), result.keySet());
        return result;
    }

    /**
     * SSO 결과에서 특정 표준 필드 값 추출
     *
     * @param additionalData 매핑된 추가 데이터
     * @param standardKey    표준 필드명
     * @return 필드 값 (없으면 null)
     */
    public String getStandardValue(Map<String, Object> additionalData, String standardKey) {
        if (additionalData == null) {
            return null;
        }
        Object value = additionalData.get(standardKey);
        return value != null ? value.toString() : null;
    }

    /**
     * 학번 추출
     */
    public String getStudentId(Map<String, Object> additionalData) {
        return getStandardValue(additionalData, "studentId");
    }

    /**
     * 학과 추출
     */
    public String getDepartment(Map<String, Object> additionalData) {
        return getStandardValue(additionalData, "department");
    }

    /**
     * 사용자 유형 추출
     */
    public String getUserType(Map<String, Object> additionalData) {
        return getStandardValue(additionalData, "userType");
    }

    /**
     * 학년 추출
     */
    public Integer getGrade(Map<String, Object> additionalData) {
        Object value = additionalData != null ? additionalData.get("grade") : null;
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getResponseMapping(Map<String, Object> additionalConfig) {
        Object mapping = additionalConfig.get(RESPONSE_MAPPING_KEY);
        if (mapping instanceof Map) {
            Map<String, String> result = new HashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) mapping).entrySet()) {
                result.put(entry.getKey().toString(), entry.getValue().toString());
            }
            return result;
        }
        return new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getCustomFields(Map<String, Object> additionalConfig) {
        Object fields = additionalConfig.get(CUSTOM_FIELDS_KEY);
        if (fields instanceof Map) {
            return new HashMap<>((Map<String, Object>) fields);
        }
        return new HashMap<>();
    }

    private boolean isSourceKey(String key, Map<String, String> responseMapping) {
        return responseMapping.containsValue(key);
    }
}
