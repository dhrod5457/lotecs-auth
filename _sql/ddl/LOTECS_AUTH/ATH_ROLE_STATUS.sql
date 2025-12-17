create table ATH_ROLE_STATUS
(
    STATUS_CODE   VARCHAR2(50)           not null
        primary key,
    STATUS_NAME   VARCHAR2(100)          not null,
    ROLE_CATEGORY VARCHAR2(50)           not null
        constraint CK_ATH_ROLE_STATUS_CATEGORY
            check (role_category IN ('STUDENT', 'PROFESSOR', 'STAFF', 'COMMON')),
    IS_ACTIVE     NUMBER(1)    default 1 not null
        constraint CK_ATH_ROLE_STATUS_ACTIVE
            check (is_active IN (0, 1)),
    DESCRIPTION   VARCHAR2(500),
    SORT_ORDER    NUMBER(3)    default 0,
    IS_DEFAULT    NUMBER(1)    default 0
        constraint CK_ATH_ROLE_STATUS_DEFAULT
            check (is_default IN (0, 1)),
    CREATED_BY    VARCHAR2(36),
    CREATED_AT    TIMESTAMP(6) default SYSTIMESTAMP,
    UPDATED_BY    VARCHAR2(36),
    UPDATED_AT    TIMESTAMP(6),
    DELETED_AT    TIMESTAMP(6)
)
/

comment on table ATH_ROLE_STATUS is '역할 상태 마스터 테이블'
/

comment on column ATH_ROLE_STATUS.STATUS_CODE is '상태 코드: ENROLLED, ON_LEAVE, PROFESSOR_ACTIVE 등'
/

comment on column ATH_ROLE_STATUS.STATUS_NAME is '상태 명칭: 재학, 휴학, 재직 등'
/

comment on column ATH_ROLE_STATUS.ROLE_CATEGORY is '역할 카테고리: STUDENT, PROFESSOR, STAFF, COMMON'
/

comment on column ATH_ROLE_STATUS.IS_ACTIVE is '활성 여부: 1=활성(권한 부여), 0=비활성(권한 차단)'
/

comment on column ATH_ROLE_STATUS.IS_DEFAULT is '기본 상태 여부: 1=기본 상태'
/

create index IDX_ATH_ROLE_STATUS_CATEGORY
    on ATH_ROLE_STATUS (ROLE_CATEGORY)
/

create index IDX_ATH_ROLE_STATUS_ACTIVE
    on ATH_ROLE_STATUS (IS_ACTIVE)
/

create index IDX_ATH_ROLE_STATUS_SORT
    on ATH_ROLE_STATUS (SORT_ORDER)
/

create index IDX_ATH_ROLE_STATUS_DELETED_AT
    on ATH_ROLE_STATUS (DELETED_AT)
/

