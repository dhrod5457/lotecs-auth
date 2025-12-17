create table ATH_USER_ROLES
(
    USER_ID           VARCHAR2(36)                  not null
        constraint FK_ATH_USER_ROLES_USER
            references ATH_USERS,
    ROLE_ID           VARCHAR2(36)                  not null
        constraint FK_ATH_USER_ROLES_ROLE
            references ATH_ROLES,
    TENANT_ID         VARCHAR2(50)                  not null,
    STATUS_CODE       VARCHAR2(50) default 'ACTIVE' not null
        constraint FK_ATH_USER_ROLES_STATUS
            references ATH_ROLE_STATUS,
    STATUS_CHANGED_AT TIMESTAMP(6),
    STATUS_CHANGED_BY VARCHAR2(36),
    STATUS_REASON     VARCHAR2(1000),
    VALID_FROM        TIMESTAMP(6),
    VALID_UNTIL       TIMESTAMP(6),
    ASSIGNED_AT       TIMESTAMP(6) default SYSTIMESTAMP,
    ASSIGNED_BY       VARCHAR2(36),
    REVOKED_AT        TIMESTAMP(6),
    REVOKED_BY        VARCHAR2(36),
    primary key (USER_ID, ROLE_ID),
    constraint CK_ATH_USER_ROLES_VALID_PERIOD
        check (valid_from IS NULL OR valid_until IS NULL OR valid_from < valid_until)
)
/

comment on table ATH_USER_ROLES is '사용자-역할 매핑 테이블 - 역할 상태 및 유효기간 관리'
/

comment on column ATH_USER_ROLES.USER_ID is '사용자 ID'
/

comment on column ATH_USER_ROLES.ROLE_ID is '역할 ID'
/

comment on column ATH_USER_ROLES.STATUS_CODE is '역할 상태 코드: ENROLLED, ON_LEAVE 등'
/

comment on column ATH_USER_ROLES.STATUS_REASON is '상태 변경 사유'
/

comment on column ATH_USER_ROLES.VALID_FROM is '유효 시작일 (NULL이면 즉시 활성화)'
/

comment on column ATH_USER_ROLES.VALID_UNTIL is '유효 종료일 (NULL이면 무제한)'
/

comment on column ATH_USER_ROLES.ASSIGNED_AT is '역할 부여 시각'
/

comment on column ATH_USER_ROLES.REVOKED_AT is '역할 회수 시각 (NULL이면 활성 상태)'
/

create index IDX_ATH_USER_ROLES_USER
    on ATH_USER_ROLES (USER_ID)
/

create index IDX_ATH_USER_ROLES_ROLE
    on ATH_USER_ROLES (ROLE_ID)
/

create index IDX_ATH_USER_ROLES_TENANT
    on ATH_USER_ROLES (TENANT_ID)
/

create index IDX_ATH_USER_ROLES_STATUS
    on ATH_USER_ROLES (STATUS_CODE)
/

create index IDX_ATH_USER_ROLES_VALID_PERIOD
    on ATH_USER_ROLES (VALID_FROM, VALID_UNTIL)
/

create index IDX_ATH_USER_ROLES_ASSIGNED_AT
    on ATH_USER_ROLES (ASSIGNED_AT)
/

