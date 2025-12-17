create table ATH_TENANT_SSO_CONFIG
(
    TENANT_ID            VARCHAR2(50) not null
        primary key,
    SSO_TYPE             VARCHAR2(20) not null
        constraint CK_ATH_SSO_TYPE
            check (sso_type IN ('INTERNAL', 'RELAY', 'KEYCLOAK', 'LDAP', 'EXTERNAL')),
    SSO_ENABLED          NUMBER(1)    default 1,
    RELAY_ENDPOINT       VARCHAR2(500),
    RELAY_TIMEOUT_MS     NUMBER       default 5000,
    SSO_SERVER_URL       VARCHAR2(500),
    SSO_REALM            VARCHAR2(100),
    SSO_CLIENT_ID        VARCHAR2(100),
    SSO_CLIENT_SECRET    VARCHAR2(500),
    USER_SYNC_ENABLED    NUMBER(1)    default 1,
    ROLE_MAPPING_ENABLED NUMBER(1)    default 1,
    ADDITIONAL_CONFIG    CLOB,
    CREATED_AT           TIMESTAMP(6) default SYSTIMESTAMP,
    UPDATED_AT           TIMESTAMP(6)
)
/

comment on table ATH_TENANT_SSO_CONFIG is '테넌트별 SSO 설정'
/

comment on column ATH_TENANT_SSO_CONFIG.TENANT_ID is '테넌트 ID'
/

comment on column ATH_TENANT_SSO_CONFIG.SSO_TYPE is 'INTERNAL: 자체인증, RELAY: lotecs-relay위임, KEYCLOAK/LDAP/EXTERNAL: 직접SSO'
/

comment on column ATH_TENANT_SSO_CONFIG.RELAY_ENDPOINT is 'RELAY 타입 엔드포인트'
/

comment on column ATH_TENANT_SSO_CONFIG.RELAY_TIMEOUT_MS is 'RELAY 타임아웃 (ms)'
/

comment on column ATH_TENANT_SSO_CONFIG.USER_SYNC_ENABLED is '사용자 동기화 활성화 여부'
/

comment on column ATH_TENANT_SSO_CONFIG.ROLE_MAPPING_ENABLED is '역할 매핑 활성화 여부'
/

create index IDX_ATH_SSO_TYPE
    on ATH_TENANT_SSO_CONFIG (SSO_TYPE)
/

