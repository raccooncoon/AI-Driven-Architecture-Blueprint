-- ================================================
-- ADAB 운영 환경 데이터베이스 초기화 스크립트
-- ================================================

-- 1. 데이터베이스 생성
CREATE DATABASE adab_prod_db
    WITH
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TEMPLATE = template0;

-- 2. 운영용 사용자 생성
CREATE USER adab_prod_user WITH PASSWORD 'CHANGE_THIS_PASSWORD';

-- 3. 데이터베이스 권한 부여
GRANT ALL PRIVILEGES ON DATABASE adab_prod_db TO adab_prod_user;

-- 4. adab_prod_db에 연결 후 스키마 권한 부여
\c adab_prod_db

-- public 스키마 권한 부여
GRANT ALL ON SCHEMA public TO adab_prod_user;

-- 기본 테이블 권한 부여
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO adab_prod_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO adab_prod_user;

-- 향후 생성될 테이블에 대한 권한 자동 부여
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO adab_prod_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO adab_prod_user;

-- ================================================
-- 테이블 생성 (JPA가 자동으로 생성하지만, 수동 생성이 필요한 경우 사용)
-- ================================================

-- Requirements 테이블
CREATE TABLE IF NOT EXISTS requirements (
    requirement_id VARCHAR(255) PRIMARY KEY,
    sequence_no INTEGER,
    rfp_id VARCHAR(255),
    name VARCHAR(255),
    definition VARCHAR(255),
    request_content TEXT,
    deadline VARCHAR(255),
    implementation_opinion TEXT,
    poba_opinion TEXT,
    tech_innovation_opinion TEXT,
    
    -- New columns
    constraints TEXT,
    solution TEXT,
    category VARCHAR(255),
    source VARCHAR(255),
    priority VARCHAR(50),
    acceptance VARCHAR(50),
    acceptance_reason TEXT,
    change_type VARCHAR(100),
    change_date VARCHAR(50),
    change_reason TEXT,
    manager VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Features 테이블
CREATE TABLE IF NOT EXISTS features (
    id BIGSERIAL PRIMARY KEY,
    req_id VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(50),
    FOREIGN KEY (req_id) REFERENCES requirements(requirement_id) ON DELETE CASCADE
);

-- WBS Tasks 테이블
CREATE TABLE IF NOT EXISTS wbs_tasks (
    id BIGSERIAL PRIMARY KEY,
    feature_id BIGINT,
    task_name VARCHAR(255) NOT NULL,
    owner_role VARCHAR(255),
    duration VARCHAR(50),
    FOREIGN KEY (feature_id) REFERENCES features(id) ON DELETE CASCADE
);

-- Tasks 테이블
CREATE TABLE IF NOT EXISTS tasks (
    uuid VARCHAR(255) PRIMARY KEY,
    task_id VARCHAR(255) NOT NULL,
    parent_requirement_id VARCHAR(255) NOT NULL,
    parent_index INTEGER NOT NULL,
    summary VARCHAR(1000),
    major_category_id VARCHAR(255),
    major_category VARCHAR(255),
    detail_function_id VARCHAR(255),
    detail_function VARCHAR(255),
    sub_function VARCHAR(2000),
    generated_by VARCHAR(100),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Model Configs 테이블
CREATE TABLE IF NOT EXISTS model_configs (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    model_name VARCHAR(100),
    api_key VARCHAR(500),
    base_url VARCHAR(500),
    temperature VARCHAR(50),
    max_tokens VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- 인덱스 생성 (성능 최적화)
CREATE INDEX IF NOT EXISTS idx_tasks_parent_req_id ON tasks(parent_requirement_id);
CREATE INDEX IF NOT EXISTS idx_tasks_deleted ON tasks(deleted);
CREATE INDEX IF NOT EXISTS idx_features_req_id ON features(req_id);
CREATE INDEX IF NOT EXISTS idx_wbs_tasks_feature_id ON wbs_tasks(feature_id);

-- ================================================
-- 완료 메시지
-- ================================================
\echo '운영 데이터베이스 초기화 완료!'
\echo '데이터베이스명: adab_prod_db'
\echo '사용자명: adab_prod_user'
\echo '⚠️  보안을 위해 반드시 비밀번호를 변경하세요!'
