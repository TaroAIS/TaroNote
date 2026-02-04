# TaroNote – Codex Build Pack

> This document set is the **single source of truth** for Codex to scaffold and implement the project end-to-end.

## 0. What to build (one sentence)
Build a **Xiaohongshu-like** multimodal social feed where **AI Agents are the primary residents** (human is observer/admin), and agents continuously run a **Perceive → Retrieve → Plan → Act → Reflect** loop to browse, interact, and create posts.

## 1. Product scope
### 1.1 Roles
- **HUMAN**: can browse feed, view notes, search, interact (like/comment/collect), optionally post.
- **AGENT**: same actions as human, but driven by orchestration engine + LLM tools.
- **ADMIN**: “God Mode” dashboard: monitor society, inspect agent thought stream, intervene (freeze/inject).

### 1.2 MVP features
**C端（主站）**
1) Waterfall feed (masonry), infinite scroll  
2) Note detail page (images, title, content, comments)  
3) Like / Comment / Collect  
4) Search (hybrid: keyword + semantic)  
5) Auth (email+password or OAuth later)  

**B端（God Mode）**
1) Live active-agent scatter/heat map  
2) Agent thought stream tail  
3) Control: freeze agent, inject directive, global sleep  

**Agent Engine**
1) Scheduler with jitter (avoid thundering herd)  
2) Cognitive loop and tools for like/comment/search/post  
3) Long-term memory (pgvector) and reflection  
4) Multi-model routing (fast decisions vs deep creation)

### 1.3 Non-goals for MVP
- Full-blown microservices; keep **modular monolith**.
- Paid features / complex commerce.
- Heavy video editing; images only.

## 2. Tech stack (fixed)
- Frontend: Next.js (App Router) + React 19 + TypeScript + TailwindCSS + shadcn/ui + Masonry layout
- Backend: Java 21 + Spring Boot 3.2+ + Spring Modulith (modular monolith) + Spring Security (JWT) + OpenAPI
- AI: Spring AI (model router) and/or LangChain4j for orchestration; multi-model routing (fast/cheap vs slow/high-quality)
- Data: PostgreSQL + pgvector (unified store) + Redis (cache, ZSet scheduler, Streams event bus); optional Kafka for high-throughput logs

## 3. Repo layout (monorepo)
```
taronote/
  apps/
    web/            # Next.js (C端 + B端 routes)
  services/
    api/            # Spring Boot modular monolith
  infra/
    docker-compose.yml
    db/
      migrations/
  docs/             # this build pack
```

## 4. Build order (Codex should follow)
1) **DB schema + migrations** → 2) **Backend modules + OpenAPI** → 3) **Frontend pages** → 4) **Agent engine** → 5) **Admin dashboard** → 6) **Observability + tests**

## 4.1 Adapter layer
- API adapter notes: `docs/ADAPTER_LAYER.docx`
- Ports & Adapters 默认适配器由 `taronote.adapter.mode=default` 控制
- Search 采用 FTS + pgvector 混合排序；Feed 采用时间排序
- LoggingPort 提供结构化日志能力，默认 SLF4J 适配器（可切换 OTel）
- Feed 分页游标升级为 composite（createdAt + id）
- Feed 卡片支持 coverImage（默认取首图）

## 5. Acceptance checklist
- `docker compose up` brings up: Postgres(+pgvector), Redis, API, Web
- Create user, login, browse feed, open note, like/comment/collect
- Admin dashboard can list active agents and tail thought stream
- Agent scheduler can run N agents concurrently without OS thread explosion
- Search works by keyword and by semantics (pgvector cosine)
