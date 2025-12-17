create table ATH_TENANT
(
    TENANT_ID              VARCHAR2(50)           not null
        primary key,
    SITE_NAME              VARCHAR2(200)          not null,
    SITE_CODE              VARCHAR2(50)           not null
        unique,
    PRIMARY_DOMAIN         VARCHAR2(200)
        unique,
    ADDITIONAL_DOMAINS     VARCHAR2(1000),
    DESCRIPTION            VARCHAR2(500),
    SITE_TITLE             VARCHAR2(200),
    SITE_DESCRIPTION       VARCHAR2(1000),
    THEME_NAME             VARCHAR2(100),
    DEFAULT_LANGUAGE       VARCHAR2(10) default 'ko',
    TIMEZONE               VARCHAR2(50) default 'Asia/Seoul',
    OWNER_EMAIL            VARCHAR2(200),
    ADMIN_EMAIL            VARCHAR2(200),
    CONTACT_PHONE          VARCHAR2(50),
    PARENT_TENANT_ID       VARCHAR2(50)
        constraint FK_ATH_TENANT_PARENT
            references ATH_TENANT,
    SITE_LEVEL             NUMBER       default 0
        constraint CK_ATH_SITE_LEVEL
            check (site_level >= 0 AND site_level <= 5),
    MAX_CONTENT_ITEMS      NUMBER,
    MAX_STORAGE_MB         NUMBER,
    MAX_USERS              NUMBER       default 100,
    FEATURES               VARCHAR2(4000),
    SETTINGS               VARCHAR2(4000),
    STATUS                 VARCHAR2(20) default 'DRAFT'
        constraint CK_ATH_TENANT_STATUS
            check (status IN ('DRAFT', 'PUBLISHED', 'SUSPENDED', 'ARCHIVED')),
    PUBLISHED_AT           TIMESTAMP(6),
    UNPUBLISHED_AT         TIMESTAMP(6),
    SUBSCRIPTION_PLAN_CODE VARCHAR2(100),
    PLAN_START_DATE        TIMESTAMP(6),
    PLAN_END_DATE          TIMESTAMP(6),
    CREATED_BY             VARCHAR2(100),
    CREATED_AT             TIMESTAMP(6) default SYSTIMESTAMP,
    UPDATED_BY             VARCHAR2(100),
    UPDATED_AT             TIMESTAMP(6),
    DELETED_AT             TIMESTAMP(6),
    VERSION                NUMBER(19)   default 0 not null
)
/

comment on table ATH_TENANT is '테넌트 관리 테이블'
/

comment on column ATH_TENANT.TENANT_ID is '테넌트 고유 식별자'
/

comment on column ATH_TENANT.SITE_CODE is '사이트 코드 (URL slug)'
/

comment on column ATH_TENANT.SITE_TITLE is '사이트 제목'
/

comment on column ATH_TENANT.SITE_DESCRIPTION is '사이트 설명'
/

comment on column ATH_TENANT.THEME_NAME is '테마 이름'
/

comment on column ATH_TENANT.MAX_CONTENT_ITEMS is '최대 컨텐츠 수'
/

comment on column ATH_TENANT.MAX_STORAGE_MB is '최대 저장용량 (MB)'
/

comment on column ATH_TENANT.FEATURES is '기능 설정 (JSON)'
/

comment on column ATH_TENANT.STATUS is '상태: DRAFT, PUBLISHED, SUSPENDED, ARCHIVED'
/

comment on column ATH_TENANT.PUBLISHED_AT is '게시 일시'
/

comment on column ATH_TENANT.UNPUBLISHED_AT is '게시 중단 일시'
/

comment on column ATH_TENANT.SUBSCRIPTION_PLAN_CODE is '구독 플랜 코드'
/

comment on column ATH_TENANT.PLAN_START_DATE is '플랜 시작일'
/

comment on column ATH_TENANT.PLAN_END_DATE is '플랜 종료일'
/

create index IDX_ATH_TENANT_STATUS
    on ATH_TENANT (STATUS)
/

create index IDX_ATH_TENANT_PARENT
    on ATH_TENANT (PARENT_TENANT_ID)
/

