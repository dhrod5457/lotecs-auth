package lotecs.auth.domain.user.model;

/**
 * 사용자 프로필 데이터 출처
 */
public enum ProfileSource {
    /**
     * SSO 시스템에서 동기화
     */
    SSO,

    /**
     * 수동 입력
     */
    MANUAL,

    /**
     * 외부 시스템에서 가져옴
     */
    IMPORT,

    /**
     * SCIM 프로비저닝
     */
    SCIM
}
