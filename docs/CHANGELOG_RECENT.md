# Recent Changes (UI Revamp Plan Execution)

This document summarizes the five-step UI/UX revamp and backend support changes.

## 1) Cover-first cards
Backend:
- DB migration `V3__note_cover_image.sql` adds `notes.cover_image` and backfills from first image.
- Note model includes `coverImage`; create logic auto-sets cover from first image.
- API responses include `coverImage`.

Frontend:
- Feed cards render cover image with aspect ratio placeholder.

Docs:
- `docs/db/schema.sql`, `docs/DOMAIN_MODEL.md`, `docs/openapi/taronote.openapi.yaml`,
  `docs/frontend/FRONTEND_SPEC.md`, `docs/README.md`, `docs/RUNBOOK.md`, `docs/ADAPTER_LAYER.docx`

Commit: `1f90a85`

## 2) Tags / topics
Backend:
- DB migration `V4__note_tags.sql` adds `notes.tags` (JSONB).
- Create note accepts `tags`, stored and returned in API.

Frontend:
- Feed card and detail page show tags as #chips.

Docs:
- `docs/db/schema.sql`, `docs/DOMAIN_MODEL.md`, `docs/openapi/taronote.openapi.yaml`,
  `docs/frontend/FRONTEND_SPEC.md`, `docs/README.md`, `docs/RUNBOOK.md`, `docs/ADAPTER_LAYER.docx`

Commit: `e17ac52`

## 3) Interaction counts
Backend:
- Feed summary includes like/comment/collect counts.
- Detail response includes like/comment/collect counts.

Frontend:
- Feed cards show counts; detail shows counts.

Docs:
- `docs/DOMAIN_MODEL.md`, `docs/openapi/taronote.openapi.yaml`,
  `docs/frontend/FRONTEND_SPEC.md`, `docs/README.md`, `docs/ADAPTER_LAYER.docx`

Commit: `7e7556a`

## 4) Density + motion
Frontend:
- Denser masonry columns, cover zoom on hover, subtle fade-up.
- Typography and background polish.

Docs:
- `docs/frontend/FRONTEND_SPEC.md`, `docs/README.md`

Commit: `4130bd1`

## 5) Author info
Backend:
- Feed summary joins users for `authorName/authorAvatar`.
- Detail response includes author info.

Frontend:
- Feed cards show avatar + author name.
- Detail page shows author block.

Docs:
- `docs/DOMAIN_MODEL.md`, `docs/openapi/taronote.openapi.yaml`,
  `docs/frontend/FRONTEND_SPEC.md`, `docs/README.md`, `docs/ADAPTER_LAYER.docx`

Commit: `997f386`

## 6) Backend build/run stability
Backend:
- Removed UTF-8 BOMs that caused `javac` illegal character errors across API sources/tests.
- OTel logging exporter now uses OTLP gRPC only (HTTP exporter class not available in current deps).
- `taronote.logging.otlp-protocol` remains but is currently ignored; endpoint still supported.

Docs:
- This changelog updated only.

Commit: `b524201`
