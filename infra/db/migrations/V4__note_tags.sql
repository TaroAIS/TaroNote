-- Note tags support
ALTER TABLE notes
  ADD COLUMN IF NOT EXISTS tags JSONB DEFAULT '[]'::jsonb;
