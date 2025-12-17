-- Test data for integration tests

-- 1. Roles
INSERT INTO AUTH_ROLES (ROLE_ID, TENANT_ID, ROLE_NAME, DISPLAY_NAME, DESCRIPTION) VALUES
    (1, 'TEST-TENANT', 'ROLE_USER', 'User Role', 'Basic user role'),
    (2, 'TEST-TENANT', 'ROLE_ADMIN', 'Admin Role', 'Administrator role'),
    (3, 'TEST-TENANT', 'ROLE_MANAGER', 'Manager Role', 'Manager role');

-- 2. Users (password: "password123" encoded with BCrypt $2a$10$N9qo8uLOickgx2ZMRZoMye)
INSERT INTO AUTH_USERS (
    USER_ID, TENANT_ID, USERNAME, PASSWORD, EMAIL, FULL_NAME, STATUS,
    ACCOUNT_NON_LOCKED, CREDENTIALS_NON_EXPIRED, ENABLED, FAILED_LOGIN_ATTEMPTS
) VALUES
    (1, 'TEST-TENANT', 'testuser', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'testuser@example.com', 'Test User', 'ACTIVE', 1, 1, 1, 0),
    (2, 'TEST-TENANT', 'adminuser', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@example.com', 'Admin User', 'ACTIVE', 1, 1, 1, 0),
    (3, 'TEST-TENANT', 'lockeduser', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'locked@example.com', 'Locked User', 'ACTIVE', 0, 1, 1, 5);

-- 3. User-Role mappings
INSERT INTO AUTH_USER_ROLES (USER_ROLE_ID, TENANT_ID, USER_ID, ROLE_ID) VALUES
    (1, 'TEST-TENANT', 1, 1),
    (2, 'TEST-TENANT', 2, 1),
    (3, 'TEST-TENANT', 2, 2);

-- 4. SSO Configurations
INSERT INTO ATH_TENANT_SSO_CONFIG (
    CONFIG_ID, TENANT_ID, SSO_TYPE, SSO_ENABLED,
    USER_SYNC_ENABLED, ROLE_MAPPING_ENABLED
) VALUES
    (1, 'TEST-TENANT', 'INTERNAL', 1, 0, 0);

INSERT INTO ATH_TENANT_SSO_CONFIG (
    CONFIG_ID, TENANT_ID, SSO_TYPE, SSO_ENABLED,
    SSO_SERVER_URL, JWT_SECRET_KEY, JWT_EXPIRATION_SECONDS,
    USER_SYNC_ENABLED, ROLE_MAPPING_ENABLED
) VALUES
    (2, 'JWT-TENANT', 'JWT_SSO', 1, 'https://sso.test.com', 'test-secret-key-32-chars-minimum', 3600, 1, 1);

INSERT INTO ATH_TENANT_SSO_CONFIG (
    CONFIG_ID, TENANT_ID, SSO_TYPE, SSO_ENABLED,
    SSO_SERVER_URL, CAS_VALIDATE_ENDPOINT, CAS_SERVICE_URL,
    USER_SYNC_ENABLED, ROLE_MAPPING_ENABLED
) VALUES
    (3, 'CAS-TENANT', 'CAS', 1, 'https://cas.test.com', '/serviceValidate', 'https://myapp.test.com', 1, 0);

-- 5. Permissions
INSERT INTO AUTH_PERMISSIONS (PERMISSION_ID, TENANT_ID, PERMISSION_CODE, PERMISSION_NAME, RESOURCE, ACTION) VALUES
    (1, 'TEST-TENANT', 'USER_READ', 'Read User', 'USER', 'READ'),
    (2, 'TEST-TENANT', 'USER_WRITE', 'Write User', 'USER', 'WRITE'),
    (3, 'TEST-TENANT', 'ADMIN_ACCESS', 'Admin Access', 'ADMIN', 'ALL');

-- 6. Role-Permission mappings
INSERT INTO AUTH_ROLE_PERMISSIONS (ROLE_PERMISSION_ID, TENANT_ID, ROLE_ID, PERMISSION_ID) VALUES
    (1, 'TEST-TENANT', 1, 1),
    (2, 'TEST-TENANT', 2, 1),
    (3, 'TEST-TENANT', 2, 2),
    (4, 'TEST-TENANT', 2, 3);
