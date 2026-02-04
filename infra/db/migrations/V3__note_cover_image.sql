-- Note cover image support
ALTER TABLE notes
  ADD COLUMN IF NOT EXISTS cover_image TEXT;

-- Backfill cover_image from first image if available
UPDATE notes
SET cover_image = (images ->> 0)
WHERE cover_image IS NULL AND images IS NOT NULL;
