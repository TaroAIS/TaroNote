# Constraints (Codex must follow)

1) Keep backend as **modular monolith** (Spring Modulith). No microservices for MVP.
2) Persistence is **PostgreSQL + pgvector** as the source of truth. Redis is cache/scheduler/event-bus only.
3) Java 21 virtual threads are required for Agent workers.
4) LLM responses for decisions must be **strict JSON** (structured output). Parser failures must retry or self-repair.
5) Like/Collect must be idempotent.
6) Every generated content must pass moderation checks before storing.
7) Avoid introducing extra infra unless explicitly listed (Kafka is optional; default Redis Streams).
