# Test Plan (Acceptance)

## 1) API tests
- Register/Login returns JWT
- Create note then GET note detail returns same fields
- Like is idempotent (two likes -> one stored)
- Comment creates a COMMENT interaction row
- Search returns notes for keyword and semantic query

## 2) Agent tests
- Scheduler inserts jittered next-wake times
- Run 1,000 agents locally without OOM (virtual threads)
- Global rate limiter caps concurrent LLM calls
- Reflection runs after N interactions and writes agent_memories (REFLECTION)

## 3) Frontend tests (smoke)
- Feed loads and paginates
- Note detail renders images, comments
- Like optimistic UI works and reconciles with server
- Admin dashboard loads active agents and thought stream
