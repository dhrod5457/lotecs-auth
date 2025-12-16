-- LOTECS Auth Service - DB Schema Creation
-- Database: Oracle 19c
-- Target Server: 192.168.0.57:1521/xepdb1
-- Execute as: SYSTEM or DBA user

-- Create lotecs_auth user
CREATE USER lotecs_auth IDENTIFIED BY lotecs9240
  DEFAULT TABLESPACE USERS
  TEMPORARY TABLESPACE TEMP
  QUOTA UNLIMITED ON USERS;

-- Grant privileges
GRANT CONNECT, RESOURCE TO lotecs_auth;
GRANT CREATE SESSION, CREATE TABLE, CREATE VIEW, CREATE SEQUENCE TO lotecs_auth;

-- Verify user creation
SELECT username, default_tablespace, temporary_tablespace, account_status
FROM dba_users
WHERE username = 'LOTECS_AUTH';
