-- 搜索与推荐优化（FTS + 交互索引）

-- 笔记全文检索向量（生成列）
ALTER TABLE notes
  ADD COLUMN IF NOT EXISTS search_vector tsvector
  GENERATED ALWAYS AS (
    to_tsvector('simple', coalesce(title, '') || ' ' || coalesce(content, ''))
  ) STORED;

CREATE INDEX IF NOT EXISTS idx_notes_search_vector
  ON notes USING GIN (search_vector);

-- 互动查询加速（推荐热度统计）
CREATE INDEX IF NOT EXISTS idx_interactions_note_action
  ON interactions(note_id, action_type);
