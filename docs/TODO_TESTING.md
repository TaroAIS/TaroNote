# Testing TODOs

This document lists pending verification steps across the stack and defines success criteria for each area.

## 1) Backend API
- Start infra (Postgres + Redis) and run `mvn -q -DskipTests spring-boot:run`.
- Verify migrations succeed and API boots without errors.
- Smoke test core endpoints:
  - `GET /api/feed` returns items and `nextCursor`.
  - `GET /api/notes/{id}` returns detail + counts + author info.
  - `POST /api/notes` creates note with `coverImage` + `tags`.
  - `POST /api/interactions/like` toggles like.
  - `POST /api/interactions/comment` creates comment.
  - `POST /api/interactions/collect` toggles collect.

Success:
- App starts without errors once infra is up.
- Responses match OpenAPI + recent fields (coverImage, tags, author info, counts).
- Pagination works and cursor is stable across pages.

## 2) Frontend Web
- Install dependencies and run `npm run dev`.
- Load `/` feed and scroll to load more cards.
- Hover on cards to see action overlay.
- Skeleton cards appear on initial load and pagination.
- Open a note detail page and confirm counts + author info.

Success:
- No runtime errors in console.
- Masonry layout remains stable while loading.
- Hover overlay and skeletons display as expected on desktop.
- UI remains usable on mobile viewport widths.

## 3) Admin (God Mode)
- Open `/admin/dashboard`.
- Open `/admin/agents/{id}`.
- Verify SSE thought stream and control buttons render.

Success:
- Dashboard loads without errors.
- Agent detail loads and controls are present.

## 4) Observability
- Set `taronote.logging.mode=otel` and endpoint.
- Confirm logs export via OTLP gRPC.

Success:
- Logs are received at the configured OTLP gRPC endpoint.

## 5) Regression Checklist
- Feed shows cover images, tags, author info, and counts.
- Detail page shows tags, author, counts, and comments.

Success:
- All recent UI revamp features appear and behave correctly.
