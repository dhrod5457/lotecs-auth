create table ATH_USERS
(
    USER_ID                 VARCHAR2(36)                  not null
        primary key,
    TENANT_ID               VARCHAR2(50)                  not null,
    USERNAME                VARCHAR2(100)                 not null,
    PASSWORD                VARCHAR2(255)                 not null,
    EMAIL                   VARCHAR2(255)                 not null,
    PHONE_NUMBER            VARCHAR2(100),
    FULL_NAME               VARCHAR2(100),
    STATUS                  VARCHAR2(20) default 'ACTIVE' not null
        constraint CK_ATH_USERS_STATUS
            check (status IN ('ACTIVE', 'INACTIVE', 'LOCKED', 'SUSPENDED')),
    ACCOUNT_NON_LOCKED      NUMBER(1)    default 1        not null,
    CREDENTIALS_NON_EXPIRED NUMBER(1)    default 1        not null,
    ENABLED                 NUMBER(1)    default 1        not null,
    LAST_LOGIN_AT           TIMESTAMP(6),
    LAST_LOGIN_IP           VARCHAR2(45),
    FAILED_LOGIN_ATTEMPTS   NUMBER(3)    default 0
        constraint CK_ATH_USERS_FAILED_ATTEMPTS
            check (failed_login_attempts >= 0 AND failed_login_attempts <= 10),
    LOCKED_AT               TIMESTAMP(6),
    PASSWORD_CHANGED_AT     TIMESTAMP(6),
    CREATED_BY              VARCHAR2(36),
    CREATED_AT              TIMESTAMP(6) default SYSTIMESTAMP,
    UPDATED_BY              VARCHAR2(36),
    UPDATED_AT              TIMESTAMP(6),
    DELETED_AT              TIMESTAMP(6),
    constraint UK_ATH_USERS_TENANT_EMAIL
        unique (TENANT_ID, EMAIL),
    constraint UK_ATH_USERS_TENANT_USERNAME
        unique (TENANT_ID, USERNAME)
)
/

comment on table ATH_USERS is '사용자 계정 테이블 - 멀티테넌트 기반 인증'
/

comment on column ATH_USERS.USER_ID is '사용자 고유 식별자 (UUID)'
/

comment on column ATH_USERS.TENANT_ID is '테넌트 ID - 사이트별 격리'
/

comment on column ATH_USERS.USERNAME is '사용자명 (로그인 ID) - 테넌트 내 유니크'
/

comment on column ATH_USERS.PASSWORD is 'BCrypt 해시된 비밀번호 (work factor 12)'
/

comment on column ATH_USERS.STATUS is '계정 상태: ACTIVE, INACTIVE, LOCKED, SUSPENDED'
/

comment on column ATH_USERS.ACCOUNT_NON_LOCKED is '계정 잠김 여부: 1=정상, 0=잠김'
/

comment on column ATH_USERS.FAILED_LOGIN_ATTEMPTS is '연속 로그인 실패 횟수 (5회 초과 시 계정 잠김)'
/

comment on column ATH_USERS.PASSWORD_CHANGED_AT is '비밀번호 변경 시각 (90일 경과 시 만료)'
/

comment on column ATH_USERS.DELETED_AT is 'Soft Delete 시각'
/

create index IDX_ATH_USERS_TENANT
    on ATH_USERS (TENANT_ID)
/

create index IDX_ATH_USERS_USERNAME
    on ATH_USERS (USERNAME)
/

create index IDX_ATH_USERS_EMAIL
    on ATH_USERS (EMAIL)
/

create index IDX_ATH_USERS_STATUS
    on ATH_USERS (STATUS)
/

create index IDX_ATH_USERS_DELETED_AT
    on ATH_USERS (DELETED_AT)
/

