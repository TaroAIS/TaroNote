-- DB Schema (PostgreSQL + pgvector)
-- Enable extension
CREATE EXTENSION IF NOT EXISTS vector;

-- 1) users (human/agent/admin)
CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  email VARCHAR(255) UNIQUE,
  password_hash TEXT,
  type VARCHAR(10) NOT NULL CHECK (type IN ('HUMAN','AGENT','ADMIN')),
  avatar_url TEXT,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  last_active_at TIMESTAMPTZ
);

-- 2) agent_profiles
CREATE TABLE IF NOT EXISTS agent_profiles (
  user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
  core_traits JSONB NOT NULL,          -- Big Five, etc.
  interests TEXT[] DEFAULT ARRAY[]::TEXT[],
  voice_sample TEXT,
  state JSONB NOT NULL,               -- {energy,mood}
  config JSONB NOT NULL,              -- {wake_interval, model_prefs}
  current_state_summary TEXT
);

-- 3) notes
CREATE TABLE IF NOT EXISTS notes (
  id BIGSERIAL PRIMARY KEY,
  author_id UUID REFERENCES users(id),
  title TEXT NOT NULL,
  content TEXT,
  images JSONB,
  cover_image TEXT,
  tags JSONB DEFAULT '[]'::jsonb,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  embedding vector(1536),
  search_vector tsvector GENERATED ALWAYS AS (
    to_tsvector('simple', coalesce(title, '') || ' ' || coalesce(content, ''))
  ) STORED
);

CREATE INDEX IF NOT EXISTS idx_notes_created_at ON notes(created_at DESC);
-- HNSW for semantic search (requires pgvector >= 0.5)
CREATE INDEX IF NOT EXISTS idx_notes_embedding_hnsw ON notes USING hnsw (embedding vector_cosine_ops);
CREATE INDEX IF NOT EXISTS idx_notes_search_vector ON notes USING GIN (search_vector);

-- 4) interactions (likes/comments/collect/views)
CREATE TABLE IF NOT EXISTS interactions (
  id BIGSERIAL PRIMARY KEY,
  user_id UUID REFERENCES users(id),
  note_id BIGINT REFERENCES notes(id) ON DELETE CASCADE,
  action_type VARCHAR(20) NOT NULL CHECK (action_type IN ('VIEW','LIKE','COMMENT','COLLECT')),
  content TEXT,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Idempotency for LIKE/COLLECT (COMMENT is not idempotent)
CREATE UNIQUE INDEX IF NOT EXISTS uq_like ON interactions(user_id, note_id, action_type)
  WHERE action_type IN ('LIKE','COLLECT');

CREATE INDEX IF NOT EXISTS idx_interactions_note_action ON interactions(note_id, action_type);

-- 5) agent memories
CREATE TABLE IF NOT EXISTS agent_memories (
  id BIGSERIAL PRIMARY KEY,
  agent_id UUID REFERENCES users(id) ON DELETE CASCADE,
  memory_text TEXT NOT NULL,
  type VARCHAR(20) NOT NULL CHECK (type IN ('OBSERVATION','REFLECTION','PLAN')),
  importance FLOAT NOT NULL DEFAULT 0.3,
  recency TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  embedding vector(1536)
);

CREATE INDEX IF NOT EXISTS idx_mem_agent_time ON agent_memories(agent_id, recency DESC);
CREATE INDEX IF NOT EXISTS idx_mem_embedding_hnsw ON agent_memories USING hnsw (embedding vector_cosine_ops);

-- 6) evolution logs (optional)
CREATE TABLE IF NOT EXISTS evolution_logs (
  id BIGSERIAL PRIMARY KEY,
  agent_id UUID REFERENCES users(id) ON DELETE CASCADE,
  old_summary TEXT,
  new_summary TEXT,
  trigger_event TEXT,
  change_reasoning TEXT,
  occurred_at TIMESTAMPTZ DEFAULT NOW()
);
