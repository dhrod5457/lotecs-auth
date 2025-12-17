create table ATH_EXTERNAL_USER_MAPPING
(
    MAPPING_ID       VARCHAR2(50)  not null
        primary key,
    TENANT_ID        VARCHAR2(50)  not null,
    USER_ID          VARCHAR2(50)  not null
        constraint FK_ATH_EXT_USER
            references ATH_USERS
                on delete cascade,
    EXTERNAL_USER_ID VARCHAR2(200) not null,
    EXTERNAL_SYSTEM  VARCHAR2(50)  not null,
    LAST_SYNCED_AT   TIMESTAMP(6),
    CREATED_AT       TIMESTAMP(6) default SYSTIMESTAMP,
    constraint UK_ATH_EXT_MAPPING
        unique (TENANT_ID, EXTERNAL_USER_ID, EXTERNAL_SYSTEM)
)
/

comment on table ATH_EXTERNAL_USER_MAPPING is '외부 시스템 사용자 ID 매핑 (학번<->user_id)'
/

comment on column ATH_EXTERNAL_USER_MAPPING.MAPPING_ID is '매핑 고유 식별자'
/

comment on column ATH_EXTERNAL_USER_MAPPING.TENANT_ID is '테넌트 ID'
/

comment on column ATH_EXTERNAL_USER_MAPPING.USER_ID is '사용자 ID'
/

comment on column ATH_EXTERNAL_USER_MAPPING.EXTERNAL_USER_ID is '외부 시스템 사용자 ID (학번 등)'
/

comment on column ATH_EXTERNAL_USER_MAPPING.EXTERNAL_SYSTEM is '외부 시스템명: SEJONG_SIS, KEYCLOAK 등'
/

comment on column ATH_EXTERNAL_USER_MAPPING.LAST_SYNCED_AT is '마지막 동기화 시각'
/

create index IDX_ATH_EXT_USER
    on ATH_EXTERNAL_USER_MAPPING (USER_ID)
/

create index IDX_ATH_EXT_EXTERNAL
    on ATH_EXTERNAL_USER_MAPPING (EXTERNAL_USER_ID)
/

create index IDX_ATH_EXT_TENANT
    on ATH_EXTERNAL_USER_MAPPING (TENANT_ID)
/

