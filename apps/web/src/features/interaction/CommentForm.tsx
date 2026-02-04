"use client";

import { useState } from "react";
import apiClient from "@/shared/api/client";

export default function CommentForm({ noteId, onAdd }: { noteId: number; onAdd: (text: string) => void }) {
  const [content, setContent] = useState("");
  const [loading, setLoading] = useState(false);

  const submit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!content.trim()) return;
    setLoading(true);
    try {
      await apiClient.post("/api/interactions/comment", { noteId, content });
      onAdd(content);
      setContent("");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={submit} className="flex gap-2">
      <input
        value={content}
        onChange={(e) => setContent(e.target.value)}
        className="flex-1 border rounded-lg px-3 py-2 text-sm"
        placeholder="Write a comment"
      />
      <button
        type="submit"
        className="px-4 py-2 bg-gray-900 text-white rounded-lg text-sm"
        disabled={loading}
      >
        Send
      </button>
    </form>
  );
}
