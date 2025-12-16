-- LOTECS Auth Service - Initial Data
-- Execute as: lotecs_auth user
-- Date: 2025-12-15

-- 1. SSO 설정 - 세종대 (RELAY)
INSERT INTO auth_tenant_sso_config (
    tenant_id, sso_type, sso_enabled,
    relay_endpoint, relay_timeout_ms,
    user_sync_enabled, role_mapping_enabled
) VALUES (
    'sejong', 'RELAY', 1,
    'sejong-server.university.ac.kr:9090', 5000,
    1, 1
);

-- 2. SSO 설정 - 기본 테넌트 (INTERNAL)
INSERT INTO auth_tenant_sso_config (
    tenant_id, sso_type, sso_enabled
) VALUES (
    'default', 'INTERNAL', 1
);

-- 3. 기본 권한 생성
INSERT INTO auth_permissions (permission_id, permission_code, description, resource_type)
VALUES ('perm-001', 'USER_READ', '사용자 조회 권한', 'USER');

INSERT INTO auth_permissions (permission_id, permission_code, description, resource_type)
VALUES ('perm-002', 'USER_WRITE', '사용자 생성/수정 권한', 'USER');

INSERT INTO auth_permissions (permission_id, permission_code, description, resource_type)
VALUES ('perm-003', 'USER_DELETE', '사용자 삭제 권한', 'USER');

INSERT INTO auth_permissions (permission_id, permission_code, description, resource_type)
VALUES ('perm-004', 'ROLE_MANAGE', '역할 관리 권한', 'ROLE');

INSERT INTO auth_permissions (permission_id, permission_code, description, resource_type)
VALUES ('perm-005', 'SSO_CONFIG', 'SSO 설정 권한', 'SSO');

-- 4. 기본 역할 생성 (default 테넌트)
INSERT INTO auth_roles (role_id, tenant_id, role_name, display_name, description, priority)
VALUES ('role-001', 'default', 'ADMIN', '시스템 관리자', '전체 시스템 관리 권한', 100);

INSERT INTO auth_roles (role_id, tenant_id, role_name, display_name, description, priority)
VALUES ('role-002', 'default', 'USER', '일반 사용자', '기본 사용자 권한', 10);

-- 5. 역할-권한 매핑 (ADMIN)
INSERT INTO auth_role_permissions (role_id, permission_id)
VALUES ('role-001', 'perm-001');

INSERT INTO auth_role_permissions (role_id, permission_id)
VALUES ('role-001', 'perm-002');

INSERT INTO auth_role_permissions (role_id, permission_id)
VALUES ('role-001', 'perm-003');

INSERT INTO auth_role_permissions (role_id, permission_id)
VALUES ('role-001', 'perm-004');

INSERT INTO auth_role_permissions (role_id, permission_id)
VALUES ('role-001', 'perm-005');

-- 6. 역할-권한 매핑 (USER)
INSERT INTO auth_role_permissions (role_id, permission_id)
VALUES ('role-002', 'perm-001');

-- 7. 기본 관리자 계정 생성 (password: admin123)
-- BCrypt hash of 'admin123'
INSERT INTO auth_users (
    user_id, tenant_id, username, password,
    email, full_name, status,
    account_non_locked, credentials_non_expired, enabled,
    created_by
) VALUES (
    'user-admin-001', 'default', 'admin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'admin@lotecs.com', '시스템 관리자', 'ACTIVE',
    1, 1, 1,
    'SYSTEM'
);

-- 8. 관리자에게 ADMIN 역할 부여
INSERT INTO auth_user_roles (user_id, role_id, tenant_id, assigned_by)
VALUES ('user-admin-001', 'role-001', 'default', 'SYSTEM');

COMMIT;

-- Verify data
SELECT 'SSO Configs' as category, COUNT(*) as count FROM auth_tenant_sso_config
UNION ALL
SELECT 'Permissions', COUNT(*) FROM auth_permissions
UNION ALL
SELECT 'Roles', COUNT(*) FROM auth_roles
UNION ALL
SELECT 'Users', COUNT(*) FROM auth_users;
