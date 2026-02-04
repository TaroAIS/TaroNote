# Local Dev & Deployment (Runbook)

## 1) Requirements
- Docker + Docker Compose
- Node 20+
- JDK 21

## 2) Environment variables
### Backend (services/api)
- `DATABASE_URL=jdbc:postgresql://postgres:5432/taronote`
- `DATABASE_USER=postgres`
- `DATABASE_PASSWORD=postgres`
- `REDIS_URL=redis://redis:6379`
- `JWT_SECRET=dev-secret`
- `AI_PROVIDER=...` (openai/claude/local)
- `AI_API_KEY=...` (if needed)

### Frontend (apps/web)
- `NEXT_PUBLIC_API_BASE=http://localhost:8080`

## 3) Docker Compose (infra/docker-compose.yml)
Services:
- postgres (with pgvector)
- redis
- api
- web

## 4) Migrations
Use Flyway or Liquibase.
- Migrations live at `infra/db/migrations`
- On boot, apply migrations automatically.
- V2 adds FTS `search_vector` + GIN index and interaction indexes for feed ranking.
- V3 adds `cover_image` for feed cover.
- V4 adds `tags` for notes.

## 4.1 Search & Feed
- Search uses PostgreSQL FTS + pgvector hybrid ranking.
- Feed ranking uses time ordering for stability.
- Feed cursor uses composite key (createdAt + id) for stable pagination.

## 5) Observability
- Log fields: traceId, userId, agentId, agentRunId
- Metrics: agent_wakeups_total, llm_calls_total, llm_latency_ms, tool_failures_total

## 6) Logging capability
- 应用内通过 `LoggingPort` 输出结构化日志（event + fields）。
- 默认实现为 SLF4J 控制台输出；后续可替换为 OTel/日志平台适配器。
- OTel 接入：设置 `taronote.logging.mode=otel` 并配置 `OTEL_EXPORTER_OTLP_ENDPOINT`。
- 协议可选 `grpc`（默认）或 `http/protobuf`。
