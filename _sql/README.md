# LOTECS Auth Service - Database Setup

## 실행 순서

### 1. 스키마 생성 (DBA 권한 필요)

```bash
# Oracle 서버에 접속
ssh user@192.168.0.57

# SYSTEM 계정으로 스키마 생성 SQL 실행
sqlplus system/password@xepdb1 @01_create_schema.sql
```

또는 로컬에서 원격 실행:
```bash
sqlplus system/password@192.168.0.57:1521/xepdb1 @01_create_schema.sql
```

### 2. 테이블 생성 (lotecs_auth 계정)

```bash
sqlplus lotecs_auth/lotecs9240@192.168.0.57:1521/xepdb1 @02_create_tables.sql
```

### 3. 초기 데이터 입력

```bash
sqlplus lotecs_auth/lotecs9240@192.168.0.57:1521/xepdb1 @03_initial_data.sql
```

## 스키마 확인

```sql
-- 사용자 확인
SELECT username, account_status FROM dba_users WHERE username = 'LOTECS_AUTH';

-- 테이블 확인
SELECT table_name FROM user_tables ORDER BY table_name;

-- 초기 데이터 확인
SELECT * FROM auth_tenant_sso_config;
```

## 롤백

```sql
-- 모든 테이블 삭제
DROP USER lotecs_auth CASCADE;
```
