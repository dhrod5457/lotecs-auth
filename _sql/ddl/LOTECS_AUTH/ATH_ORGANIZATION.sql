create table ATH_ORGANIZATION
(
    ID                     NUMBER(19)                        not null
        primary key,
    TENANT_ID              VARCHAR2(50)                      not null
        constraint FK_ATH_ORG_TENANT
            references ATH_TENANT,
    ORGANIZATION_ID        VARCHAR2(50)                      not null
        constraint UK_ATH_ORGANIZATION_ID
            unique,
    ORGANIZATION_CODE      VARCHAR2(50)                      not null,
    ORGANIZATION_NAME      VARCHAR2(200)                     not null,
    ORGANIZATION_TYPE      VARCHAR2(50)                      not null,
    PARENT_ORGANIZATION_ID VARCHAR2(50)
        constraint FK_ATH_ORG_PARENT
            references ATH_ORGANIZATION (ORGANIZATION_ID),
    ORG_LEVEL              NUMBER(10)   default 0            not null
        constraint CK_ATH_ORG_LEVEL
            check (org_level >= 0),
    DISPLAY_ORDER          NUMBER(10)   default 0            not null,
    DESCRIPTION            VARCHAR2(1000),
    ACTIVE                 CHAR         default '1'          not null
        constraint CK_ATH_ORG_ACTIVE
            check (active IN ('1', '0')),
    CREATED_BY             VARCHAR2(50)                      not null,
    CREATED_AT             TIMESTAMP(6) default SYSTIMESTAMP not null,
    UPDATED_BY             VARCHAR2(50),
    UPDATED_AT             TIMESTAMP(6),
    DELETED_AT             TIMESTAMP(6)
)
/

comment on table ATH_ORGANIZATION is '조직 마스터 테이블'
/

comment on column ATH_ORGANIZATION.ID is '내부 ID (시퀀스)'
/

comment on column ATH_ORGANIZATION.TENANT_ID is '테넌트 ID'
/

comment on column ATH_ORGANIZATION.ORGANIZATION_ID is '조직 ID (비즈니스 키)'
/

comment on column ATH_ORGANIZATION.ORGANIZATION_CODE is '조직 코드: CS, EE, ADMIN'
/

comment on column ATH_ORGANIZATION.ORGANIZATION_NAME is '조직명: 컴퓨터공학과, 전자공학과'
/

comment on column ATH_ORGANIZATION.ORGANIZATION_TYPE is '조직 유형: COLLEGE, MAJOR, DEPARTMENT, DIVISION, TEAM'
/

comment on column ATH_ORGANIZATION.PARENT_ORGANIZATION_ID is '상위 조직 ID'
/

comment on column ATH_ORGANIZATION.ORG_LEVEL is '계층 레벨 (0: 최상위)'
/

comment on column ATH_ORGANIZATION.ACTIVE is '활성화 여부: 1/0'
/

comment on column ATH_ORGANIZATION.DELETED_AT is 'Soft Delete 시각'
/

create index IDX_ATH_ORGANIZATION_TENANT_ID
    on ATH_ORGANIZATION (TENANT_ID)
/

create index IDX_ATH_ORGANIZATION_CODE
    on ATH_ORGANIZATION (ORGANIZATION_CODE)
/

create index IDX_ATH_ORGANIZATION_TYPE
    on ATH_ORGANIZATION (ORGANIZATION_TYPE)
/

create index IDX_ATH_ORGANIZATION_PARENT
    on ATH_ORGANIZATION (PARENT_ORGANIZATION_ID)
/

create index IDX_ATH_ORGANIZATION_DELETED
    on ATH_ORGANIZATION (DELETED_AT)
/

create unique index UK_ATH_ORGANIZATION_CODE
    on ATH_ORGANIZATION (TENANT_ID, ORGANIZATION_CODE, NVL2("DELETED_AT", "ID", NULL))
/

