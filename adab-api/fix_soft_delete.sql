-- 1. deleted 컬럼이 이미 존재하면 삭제
ALTER TABLE tasks DROP COLUMN IF EXISTS deleted;
ALTER TABLE tasks DROP COLUMN IF EXISTS deleted_at;
ALTER TABLE tasks DROP COLUMN IF EXISTS generated_by;

-- 2. 다시 생성 (기본값 포함)
ALTER TABLE tasks ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE tasks ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE tasks ADD COLUMN generated_by VARCHAR(100);

-- 3. 인덱스 추가
CREATE INDEX IF NOT EXISTS idx_tasks_deleted ON tasks(deleted);
CREATE INDEX IF NOT EXISTS idx_tasks_parent_requirement_deleted ON tasks(parent_requirement_id, deleted);

-- 4. 확인
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_name = 'tasks'
ORDER BY ordinal_position;
