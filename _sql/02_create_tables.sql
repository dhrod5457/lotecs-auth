-- LOTECS Auth Service - Table Creation
-- Execute as: lotecs_auth user
-- Date: 2025-12-15

-- 1. auth_users (사용자)
CREATE TABLE auth_users (
    user_id VARCHAR2(50) PRIMARY KEY,
    tenant_id VARCHAR2(50) NOT NULL,
    username VARCHAR2(50) NOT NULL,
    password VARCHAR2(255) NOT NULL,
    email VARCHAR2(255),
    phone_number VARCHAR2(50),
    full_name VARCHAR2(100),
    status VARCHAR2(20) NOT NULL,
    account_non_locked NUMBER(1) DEFAULT 1,
    credentials_non_expired NUMBER(1) DEFAULT 1,
    enabled NUMBER(1) DEFAULT 1,
    last_login_at TIMESTAMP,
    last_login_ip VARCHAR2(50),
    failed_login_attempts NUMBER DEFAULT 0,
    locked_at TIMESTAMP,
    password_changed_at TIMESTAMP,
    created_by VARCHAR2(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR2(50),
    updated_at TIMESTAMP,
    CONSTRAINT uk_auth_users_username UNIQUE (tenant_id, username),
    CONSTRAINT ck_auth_users_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED'))
);

CREATE INDEX idx_auth_users_tenant ON auth_users(tenant_id);
CREATE INDEX idx_auth_users_email ON auth_users(email);
CREATE INDEX idx_auth_users_status ON auth_users(status);

COMMENT ON TABLE auth_users IS '사용자 정보';
COMMENT ON COLUMN auth_users.user_id IS '사용자 ID (UUID)';
COMMENT ON COLUMN auth_users.tenant_id IS '테넌트 ID';
COMMENT ON COLUMN auth_users.status IS 'ACTIVE, INACTIVE, SUSPENDED, DELETED';

-- 2. auth_roles (역할)
CREATE TABLE auth_roles (
    role_id VARCHAR2(50) PRIMARY KEY,
    tenant_id VARCHAR2(50) NOT NULL,
    role_name VARCHAR2(50) NOT NULL,
    display_name VARCHAR2(100),
    description VARCHAR2(500),
    priority NUMBER DEFAULT 0,
    created_by VARCHAR2(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR2(50),
    updated_at TIMESTAMP,
    CONSTRAINT uk_auth_roles_name UNIQUE (tenant_id, role_name)
);

CREATE INDEX idx_auth_roles_tenant ON auth_roles(tenant_id);

COMMENT ON TABLE auth_roles IS '역할 정보';
COMMENT ON COLUMN auth_roles.priority IS '역할 우선순위 (높을수록 상위 권한)';

-- 3. auth_permissions (권한)
CREATE TABLE auth_permissions (
    permission_id VARCHAR2(50) PRIMARY KEY,
    permission_code VARCHAR2(100) NOT NULL UNIQUE,
    description VARCHAR2(500),
    resource_type VARCHAR2(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_auth_permissions_code ON auth_permissions(permission_code);

COMMENT ON TABLE auth_permissions IS '권한 정보';
COMMENT ON COLUMN auth_permissions.permission_code IS '권한 코드 (예: USER_READ, USER_WRITE)';

-- 4. auth_user_roles (사용자-역할 매핑)
CREATE TABLE auth_user_roles (
    user_id VARCHAR2(50) NOT NULL,
    role_id VARCHAR2(50) NOT NULL,
    tenant_id VARCHAR2(50) NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by VARCHAR2(50),
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_auth_ur_user FOREIGN KEY (user_id) REFERENCES auth_users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_auth_ur_role FOREIGN KEY (role_id) REFERENCES auth_roles(role_id) ON DELETE CASCADE
);

CREATE INDEX idx_auth_user_roles_user ON auth_user_roles(user_id);
CREATE INDEX idx_auth_user_roles_role ON auth_user_roles(role_id);

COMMENT ON TABLE auth_user_roles IS '사용자-역할 매핑';

-- 5. auth_role_permissions (역할-권한 매핑)
CREATE TABLE auth_role_permissions (
    role_id VARCHAR2(50) NOT NULL,
    permission_id VARCHAR2(50) NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_auth_rp_role FOREIGN KEY (role_id) REFERENCES auth_roles(role_id) ON DELETE CASCADE,
    CONSTRAINT fk_auth_rp_perm FOREIGN KEY (permission_id) REFERENCES auth_permissions(permission_id) ON DELETE CASCADE
);

CREATE INDEX idx_auth_rp_role ON auth_role_permissions(role_id);

COMMENT ON TABLE auth_role_permissions IS '역할-권한 매핑';

-- 6. auth_refresh_tokens (리프레시 토큰)
CREATE TABLE auth_refresh_tokens (
    token_id VARCHAR2(50) PRIMARY KEY,
    user_id VARCHAR2(50) NOT NULL,
    tenant_id VARCHAR2(50) NOT NULL,
    refresh_token VARCHAR2(500) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_auth_rt_user FOREIGN KEY (user_id) REFERENCES auth_users(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_auth_rt_user ON auth_refresh_tokens(user_id);
CREATE INDEX idx_auth_rt_token ON auth_refresh_tokens(refresh_token);
CREATE INDEX idx_auth_rt_expires ON auth_refresh_tokens(expires_at);

COMMENT ON TABLE auth_refresh_tokens IS '리프레시 토큰 저장';

-- 7. auth_tenant_sso_config (SSO 설정)
CREATE TABLE auth_tenant_sso_config (
    tenant_id VARCHAR2(50) PRIMARY KEY,
    sso_type VARCHAR2(20) NOT NULL,
    sso_enabled NUMBER(1) DEFAULT 1,

    -- RELAY 타입 설정 (학교 테넌트)
    relay_endpoint VARCHAR2(500),
    relay_timeout_ms NUMBER DEFAULT 5000,

    -- 직접 SSO 설정 (일반 기업)
    sso_server_url VARCHAR2(500),
    sso_realm VARCHAR2(100),
    sso_client_id VARCHAR2(100),
    sso_client_secret VARCHAR2(500),

    -- 동기화 설정
    user_sync_enabled NUMBER(1) DEFAULT 1,
    role_mapping_enabled NUMBER(1) DEFAULT 1,

    -- 추가 설정 (JSON)
    additional_config CLOB,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT ck_auth_sso_type CHECK (sso_type IN ('INTERNAL', 'RELAY', 'KEYCLOAK', 'LDAP', 'EXTERNAL'))
);

CREATE INDEX idx_auth_sso_type ON auth_tenant_sso_config(sso_type);

COMMENT ON TABLE auth_tenant_sso_config IS '테넌트별 SSO 설정';
COMMENT ON COLUMN auth_tenant_sso_config.sso_type IS 'INTERNAL: 자체인증, RELAY: lotecs-relay위임, KEYCLOAK/LDAP/EXTERNAL: 직접SSO';

-- 8. auth_external_user_mapping (외부 사용자 매핑)
CREATE TABLE auth_external_user_mapping (
    mapping_id VARCHAR2(50) PRIMARY KEY,
    tenant_id VARCHAR2(50) NOT NULL,
    user_id VARCHAR2(50) NOT NULL,
    external_user_id VARCHAR2(200) NOT NULL,
    external_system VARCHAR2(50) NOT NULL,
    last_synced_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_auth_ext_user FOREIGN KEY (user_id) REFERENCES auth_users(user_id) ON DELETE CASCADE,
    CONSTRAINT uk_auth_ext_mapping UNIQUE (tenant_id, external_user_id, external_system)
);

CREATE INDEX idx_auth_ext_user ON auth_external_user_mapping(user_id);
CREATE INDEX idx_auth_ext_external ON auth_external_user_mapping(external_user_id);

COMMENT ON TABLE auth_external_user_mapping IS '외부 시스템 사용자 ID 매핑 (학번↔user_id)';

-- Verify table creation
SELECT table_name FROM user_tables ORDER BY table_name;
