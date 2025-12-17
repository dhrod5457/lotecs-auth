create table ATH_PERMISSIONS
(
    PERMISSION_ID   VARCHAR2(36)  not null
        primary key,
    TENANT_ID       VARCHAR2(50)  not null,
    PERMISSION_NAME VARCHAR2(100) not null,
    RESOURCE_NAME   VARCHAR2(50)  not null,
    ACTION          VARCHAR2(50)  not null,
    DESCRIPTION     VARCHAR2(500),
    CREATED_BY      VARCHAR2(36),
    CREATED_AT      TIMESTAMP(6) default SYSTIMESTAMP,
    UPDATED_BY      VARCHAR2(36),
    UPDATED_AT      TIMESTAMP(6),
    DELETED_AT      TIMESTAMP(6),
    constraint UK_ATH_PERMISSIONS_RESOURCE_ACTION
        unique (TENANT_ID, RESOURCE_NAME, ACTION),
    constraint UK_ATH_PERMISSIONS_TENANT_NAME
        unique (TENANT_ID, PERMISSION_NAME)
)
/

comment on table ATH_PERMISSIONS is '권한 테이블 - RBAC 기반 리소스 접근 제어'
/

comment on column ATH_PERMISSIONS.PERMISSION_ID is '권한 고유 식별자 (UUID)'
/

comment on column ATH_PERMISSIONS.TENANT_ID is '테넌트 ID'
/

comment on column ATH_PERMISSIONS.PERMISSION_NAME is '권한 이름: CONTENT_WRITE, USER_MANAGE'
/

comment on column ATH_PERMISSIONS.RESOURCE_NAME is '리소스: CONTENT, USER, MENU, TENANT'
/

comment on column ATH_PERMISSIONS.ACTION is '액션: READ, WRITE, DELETE, MANAGE'
/

comment on column ATH_PERMISSIONS.DELETED_AT is 'Soft Delete 시각'
/

create index IDX_ATH_PERMISSIONS_TENANT
    on ATH_PERMISSIONS (TENANT_ID)
/

create index IDX_ATH_PERMISSIONS_RESOURCE
    on ATH_PERMISSIONS (RESOURCE_NAME)
/

create index IDX_ATH_PERMISSIONS_DELETED_AT
    on ATH_PERMISSIONS (DELETED_AT)
/

