"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import Masonry from "react-masonry-css";
import apiClient from "@/shared/api/client";
import { FeedResponse, Note } from "@/shared/api/types";
import NoteCard from "@/entities/note/NoteCard";

const breakpointColumnsObj = {
  default: 5,
  1400: 4,
  1100: 3,
  700: 2,
  500: 1
};

export default function FeedWall() {
  const [items, setItems] = useState<Note[]>([]);
  const [cursor, setCursor] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const sentinelRef = useRef<HTMLDivElement | null>(null);

  const loadFeed = useCallback(async () => {
    if (loading) return;
    setLoading(true);
    try {
      const response = await apiClient.get<FeedResponse>("/api/feed", {
        params: { cursor, limit: 20 }
      });
      const data = response.data;
      setItems((prev) => [...prev, ...data.items]);
      setCursor(data.nextCursor ?? null);
    } finally {
      setLoading(false);
    }
  }, [cursor, loading]);

  useEffect(() => {
    loadFeed();
  }, []);

  useEffect(() => {
    const sentinel = sentinelRef.current;
    if (!sentinel) return;
    const observer = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting && cursor) {
        loadFeed();
      }
    });
    observer.observe(sentinel);
    return () => observer.disconnect();
  }, [cursor, loadFeed]);

  return (
    <section className="space-y-4">
      <Masonry
        breakpointCols={breakpointColumnsObj}
        className="my-masonry-grid flex gap-3"
        columnClassName="my-masonry-grid_column space-y-3"
      >
        {items.map((note) => (
          <a key={note.id} href={`/note/${note.id}`}>
            <NoteCard note={note} />
          </a>
        ))}
      </Masonry>
      <div ref={sentinelRef} className="h-8" />
      {loading ? <p className="text-sm text-gray-500">Loading...</p> : null}
      {!cursor && items.length > 0 ? (
        <p className="text-sm text-gray-400">No more notes.</p>
      ) : null}
    </section>
  );
}
