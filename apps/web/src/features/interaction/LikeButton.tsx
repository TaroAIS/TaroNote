"use client";

import { useState } from "react";
import apiClient from "@/shared/api/client";

export default function LikeButton({ noteId, initial }: { noteId: number; initial: number }) {
  const [count, setCount] = useState(initial);
  const [busy, setBusy] = useState(false);

  const like = async () => {
    if (busy) return;
    setBusy(true);
    setCount((prev) => prev + 1);
    try {
      await apiClient.post("/api/interactions/like", { noteId });
    } catch {
      setCount((prev) => Math.max(0, prev - 1));
    } finally {
      setBusy(false);
    }
  };

  return (
    <button
      type="button"
      onClick={like}
      className="px-3 py-1 rounded-full bg-pink-50 text-pink-600 text-sm"
      disabled={busy}
    >
      Like {count}
    </button>
  );
}
