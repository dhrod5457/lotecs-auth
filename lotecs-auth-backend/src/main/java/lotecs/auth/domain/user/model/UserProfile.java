package lotecs.auth.domain.user.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

/**
 * 사용자 프로필 (SSO 추가 데이터).
 * 학번, 학과, 학년 등 SSO에서 제공하는 추가 정보를 저장한다.
 */
@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String userId;
    private String tenantId;

    /**
     * 프로필 데이터 (JSON)
     * 예: {"studentId": "20231234", "department": "컴퓨터공학과", "grade": 3}
     */
    private String profileData;

    /**
     * 데이터 출처
     * SSO, MANUAL, IMPORT, SCIM 등
     */
    @Builder.Default
    private ProfileSource source = ProfileSource.MANUAL;

    /**
     * 출처 시스템 상세
     * 예: keycloak, cas, bu_sso, bscu_sso 등
     */
    private String sourceSystem;

    /**
     * 마지막 동기화 시간 (SSO에서 데이터를 가져온 시간)
     */
    private LocalDateTime syncedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * profileData JSON을 Map으로 파싱
     */
    public Map<String, Object> getProfileDataAsMap() {
        if (profileData == null || profileData.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(profileData, new TypeReference<>() {});
        } catch (Exception e) {
            log.warn("Failed to parse profileData for user {}: {}", userId, e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * Map을 profileData JSON으로 변환하여 저장
     */
    public void setProfileDataFromMap(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            this.profileData = "{}";
            return;
        }
        try {
            this.profileData = objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            log.warn("Failed to serialize profileData for user {}: {}", userId, e.getMessage());
            this.profileData = "{}";
        }
    }

    /**
     * 특정 프로필 값 조회
     */
    public Object getProfileValue(String key) {
        return getProfileDataAsMap().get(key);
    }

    /**
     * 특정 프로필 값 문자열로 조회
     */
    public String getProfileValueAsString(String key) {
        Object value = getProfileValue(key);
        return value != null ? value.toString() : null;
    }

    /**
     * SSO 데이터로 프로필 생성
     */
    public static UserProfile createFromSso(
            String userId,
            String tenantId,
            Map<String, Object> additionalData,
            String sourceSystem
    ) {
        UserProfile profile = UserProfile.builder()
                .userId(userId)
                .tenantId(tenantId)
                .source(ProfileSource.SSO)
                .sourceSystem(sourceSystem)
                .syncedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        profile.setProfileDataFromMap(additionalData);
        return profile;
    }

    /**
     * SSO 데이터로 프로필 업데이트
     */
    public void updateFromSso(Map<String, Object> additionalData, String sourceSystem) {
        setProfileDataFromMap(additionalData);
        this.source = ProfileSource.SSO;
        this.sourceSystem = sourceSystem;
        this.syncedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 데이터가 특정 시간 이상 오래되었는지 확인
     */
    public boolean isStale(long maxAgeMinutes) {
        if (syncedAt == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(syncedAt.plusMinutes(maxAgeMinutes));
    }
}
