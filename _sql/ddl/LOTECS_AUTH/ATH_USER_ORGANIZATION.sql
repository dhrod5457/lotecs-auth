create table ATH_USER_ORGANIZATION
(
    ID              NUMBER(19)                        not null
        primary key,
    TENANT_ID       VARCHAR2(50)                      not null
        constraint FK_ATH_USER_ORG_TENANT
            references ATH_TENANT,
    USER_ID         VARCHAR2(50)                      not null
        constraint FK_ATH_USER_ORG_USER
            references ATH_USERS,
    ORGANIZATION_ID VARCHAR2(50)                      not null
        constraint FK_ATH_USER_ORG_ORG
            references ATH_ORGANIZATION (ORGANIZATION_ID),
    ROLE_ID         VARCHAR2(50)
        constraint FK_ATH_USER_ORG_ROLE
            references ATH_ROLES,
    IS_PRIMARY      CHAR         default '0'          not null
        constraint CK_ATH_USER_ORG_PRIMARY
            check (is_primary IN ('1', '0')),
    POSITION        VARCHAR2(100),
    START_DATE      DATE,
    END_DATE        DATE,
    ACTIVE          CHAR         default '1'          not null
        constraint CK_ATH_USER_ORG_ACTIVE
            check (active IN ('1', '0')),
    CREATED_BY      VARCHAR2(50)                      not null,
    CREATED_AT      TIMESTAMP(6) default SYSTIMESTAMP not null,
    UPDATED_BY      VARCHAR2(50),
    UPDATED_AT      TIMESTAMP(6),
    DELETED_AT      TIMESTAMP(6),
    constraint CK_ATH_USER_ORG_DATES
        check (end_date IS NULL OR end_date >= start_date)
)
/

comment on table ATH_USER_ORGANIZATION is '사용자-조직 매핑 테이블'
/

comment on column ATH_USER_ORGANIZATION.ID is '내부 ID (시퀀스)'
/

comment on column ATH_USER_ORGANIZATION.TENANT_ID is '테넌트 ID'
/

comment on column ATH_USER_ORGANIZATION.USER_ID is '사용자 ID'
/

comment on column ATH_USER_ORGANIZATION.ORGANIZATION_ID is '조직 ID'
/

comment on column ATH_USER_ORGANIZATION.ROLE_ID is '역할 ID (교수, 학생, 직원 등)'
/

comment on column ATH_USER_ORGANIZATION.IS_PRIMARY is '주 소속 여부: 1(주 소속), 0(겸임)'
/

comment on column ATH_USER_ORGANIZATION.POSITION is '직책/직위: 학과장, 팀장, 과장'
/

comment on column ATH_USER_ORGANIZATION.START_DATE is '소속 시작일'
/

comment on column ATH_USER_ORGANIZATION.END_DATE is '소속 종료일 (NULL이면 현재 소속)'
/

comment on column ATH_USER_ORGANIZATION.ACTIVE is '활성화 여부: 1/0'
/

comment on column ATH_USER_ORGANIZATION.DELETED_AT is 'Soft Delete 시각'
/

create index IDX_ATH_USER_ORG_TENANT
    on ATH_USER_ORGANIZATION (TENANT_ID)
/

create index IDX_ATH_USER_ORG_USER
    on ATH_USER_ORGANIZATION (USER_ID)
/

create index IDX_ATH_USER_ORG_ORG
    on ATH_USER_ORGANIZATION (ORGANIZATION_ID)
/

create index IDX_ATH_USER_ORG_ROLE
    on ATH_USER_ORGANIZATION (ROLE_ID)
/

create index IDX_ATH_USER_ORG_DELETED
    on ATH_USER_ORGANIZATION (DELETED_AT)
/

create unique index UK_ATH_USER_ORGANIZATION
    on ATH_USER_ORGANIZATION (TENANT_ID, USER_ID, ORGANIZATION_ID, NVL2("DELETED_AT", "ID", NULL))
/

