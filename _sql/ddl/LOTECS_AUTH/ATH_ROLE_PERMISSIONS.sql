create table ATH_ROLE_PERMISSIONS
(
    ROLE_ID       VARCHAR2(36) not null
        constraint FK_ATH_ROLE_PERMISSIONS_ROLE
            references ATH_ROLES,
    PERMISSION_ID VARCHAR2(36) not null
        constraint FK_ATH_ROLE_PERMISSIONS_PERM
            references ATH_PERMISSIONS,
    TENANT_ID     VARCHAR2(50) not null,
    GRANTED_AT    TIMESTAMP(6) default SYSTIMESTAMP,
    GRANTED_BY    VARCHAR2(36),
    primary key (ROLE_ID, PERMISSION_ID)
)
/

comment on table ATH_ROLE_PERMISSIONS is '역할-권한 매핑 테이블 - RBAC 구현'
/

comment on column ATH_ROLE_PERMISSIONS.ROLE_ID is '역할 ID'
/

comment on column ATH_ROLE_PERMISSIONS.PERMISSION_ID is '권한 ID'
/

comment on column ATH_ROLE_PERMISSIONS.TENANT_ID is '테넌트 ID'
/

comment on column ATH_ROLE_PERMISSIONS.GRANTED_AT is '권한 부여 시각'
/

comment on column ATH_ROLE_PERMISSIONS.GRANTED_BY is '권한 부여자 ID'
/

create index IDX_ATH_ROLE_PERMISSIONS_ROLE
    on ATH_ROLE_PERMISSIONS (ROLE_ID)
/

create index IDX_ATH_ROLE_PERMISSIONS_PERMISSION
    on ATH_ROLE_PERMISSIONS (PERMISSION_ID)
/

create index IDX_ATH_ROLE_PERMISSIONS_TENANT
    on ATH_ROLE_PERMISSIONS (TENANT_ID)
/

create index IDX_ATH_ROLE_PERMISSIONS_GRANTED_AT
    on ATH_ROLE_PERMISSIONS (GRANTED_AT)
/

