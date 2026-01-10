-- tasks 테이블에 소프트 삭제 컬럼 추가
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;

-- 인덱스 추가 (성능 향상)
CREATE INDEX IF NOT EXISTS idx_tasks_deleted ON tasks(deleted);
CREATE INDEX IF NOT EXISTS idx_tasks_parent_requirement_deleted ON tasks(parent_requirement_id, deleted);
