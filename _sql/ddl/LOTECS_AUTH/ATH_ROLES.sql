create table ATH_ROLES
(
    ROLE_ID      VARCHAR2(36) not null
        primary key,
    TENANT_ID    VARCHAR2(50) not null,
    ROLE_NAME    VARCHAR2(50) not null,
    DISPLAY_NAME VARCHAR2(100),
    DESCRIPTION  VARCHAR2(500),
    PRIORITY     NUMBER(3)    not null,
    CREATED_BY   VARCHAR2(36),
    CREATED_AT   TIMESTAMP(6) default SYSTIMESTAMP,
    UPDATED_BY   VARCHAR2(36),
    UPDATED_AT   TIMESTAMP(6),
    DELETED_AT   TIMESTAMP(6),
    constraint UK_ATH_ROLES_TENANT_NAME
        unique (TENANT_ID, ROLE_NAME)
)
/

comment on table ATH_ROLES is '역할 테이블 - RBAC 기반 역할 관리'
/

comment on column ATH_ROLES.ROLE_ID is '역할 고유 식별자 (UUID)'
/

comment on column ATH_ROLES.TENANT_ID is '테넌트 ID'
/

comment on column ATH_ROLES.ROLE_NAME is '역할 이름: STUDENT, PROFESSOR, STAFF, ADMIN'
/

comment on column ATH_ROLES.DISPLAY_NAME is '표시용 이름: 학생, 교수, 직원, 관리자'
/

comment on column ATH_ROLES.PRIORITY is '역할 우선순위 - 숫자가 낮을수록 높은 권한'
/

comment on column ATH_ROLES.DELETED_AT is 'Soft Delete 시각'
/

create index IDX_ATH_ROLES_TENANT
    on ATH_ROLES (TENANT_ID)
/

create index IDX_ATH_ROLES_PRIORITY
    on ATH_ROLES (PRIORITY)
/

create index IDX_ATH_ROLES_DELETED_AT
    on ATH_ROLES (DELETED_AT)
/

