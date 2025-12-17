create table ATH_REFRESH_TOKENS
(
    TOKEN_ID       VARCHAR2(36)                      not null
        primary key,
    USER_ID        VARCHAR2(36)                      not null
        constraint FK_ATH_REFRESH_TOKENS_USER
            references ATH_USERS
                on delete cascade,
    TENANT_ID      VARCHAR2(50)                      not null,
    TOKEN_HASH     VARCHAR2(255)                     not null
        constraint UK_ATH_REFRESH_TOKENS_HASH
            unique,
    TOKEN_FAMILY   VARCHAR2(36),
    ISSUED_AT      TIMESTAMP(6) default SYSTIMESTAMP not null,
    EXPIRES_AT     TIMESTAMP(6)                      not null,
    REVOKED_AT     TIMESTAMP(6),
    REVOKED_REASON VARCHAR2(100),
    IP_ADDRESS     VARCHAR2(45),
    USER_AGENT     VARCHAR2(500),
    DEVICE_ID      VARCHAR2(100),
    LAST_USED_AT   TIMESTAMP(6),
    USED_COUNT     NUMBER(10)   default 0,
    constraint CK_ATH_REFRESH_TOKENS_EXPIRES
        check (expires_at > issued_at)
)
/

comment on table ATH_REFRESH_TOKENS is 'JWT Refresh Token 저장 및 관리'
/

comment on column ATH_REFRESH_TOKENS.TOKEN_ID is '토큰 고유 식별자 (UUID)'
/

comment on column ATH_REFRESH_TOKENS.USER_ID is '사용자 ID'
/

comment on column ATH_REFRESH_TOKENS.TENANT_ID is '테넌트 ID'
/

comment on column ATH_REFRESH_TOKENS.TOKEN_HASH is 'Refresh Token SHA-256 해시'
/

comment on column ATH_REFRESH_TOKENS.TOKEN_FAMILY is '토큰 패밀리 ID (Rotation 추적)'
/

comment on column ATH_REFRESH_TOKENS.ISSUED_AT is '토큰 발급 시각'
/

comment on column ATH_REFRESH_TOKENS.EXPIRES_AT is '토큰 만료 시각 (7~30일)'
/

comment on column ATH_REFRESH_TOKENS.REVOKED_AT is '토큰 무효화 시각'
/

comment on column ATH_REFRESH_TOKENS.REVOKED_REASON is '무효화 사유: LOGOUT, SECURITY_BREACH, EXPIRED'
/

comment on column ATH_REFRESH_TOKENS.IP_ADDRESS is '토큰 발급 시 클라이언트 IP'
/

comment on column ATH_REFRESH_TOKENS.USER_AGENT is '토큰 발급 시 User-Agent'
/

comment on column ATH_REFRESH_TOKENS.DEVICE_ID is '디바이스 식별자'
/

comment on column ATH_REFRESH_TOKENS.LAST_USED_AT is '토큰 마지막 사용 시각'
/

comment on column ATH_REFRESH_TOKENS.USED_COUNT is '토큰 사용 횟수 (재사용 감지용)'
/

create index IDX_ATH_REFRESH_TOKENS_USER
    on ATH_REFRESH_TOKENS (USER_ID)
/

create index IDX_ATH_REFRESH_TOKENS_TENANT
    on ATH_REFRESH_TOKENS (TENANT_ID)
/

create index IDX_ATH_REFRESH_TOKENS_EXPIRES
    on ATH_REFRESH_TOKENS (EXPIRES_AT)
/

create index IDX_ATH_REFRESH_TOKENS_FAMILY
    on ATH_REFRESH_TOKENS (TOKEN_FAMILY)
/

create index IDX_ATH_REFRESH_TOKENS_ACTIVE
    on ATH_REFRESH_TOKENS (USER_ID, EXPIRES_AT, REVOKED_AT)
/

