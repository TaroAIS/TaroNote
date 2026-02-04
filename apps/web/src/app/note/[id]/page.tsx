"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import apiClient from "@/shared/api/client";
import { NoteDetail, Comment } from "@/shared/api/types";
import LikeButton from "@/features/interaction/LikeButton";
import CommentForm from "@/features/interaction/CommentForm";

export default function NoteDetailPage() {
  const params = useParams();
  const id = Number(params?.id);
  const [note, setNote] = useState<NoteDetail | null>(null);

  useEffect(() => {
    if (!id) return;
    apiClient.get<NoteDetail>(`/api/notes/${id}`).then((res) => setNote(res.data));
  }, [id]);

  const addComment = (text: string) => {
    if (!note) return;
    const newComment: Comment = {
      id: Date.now(),
      userId: "me",
      noteId: note.id,
      content: text,
      createdAt: new Date().toISOString()
    };
    setNote({ ...note, comments: [...note.comments, newComment] });
  };

  if (!note) {
    return <p className="text-gray-500">Loading...</p>;
  }

  return (
    <main className="space-y-6">
      <section className="bg-white rounded-xl p-6 shadow-sm space-y-3">
        <div className="flex items-center gap-3">
          {note.authorAvatar ? (
            <img
              src={note.authorAvatar}
              alt={note.authorName ?? "author"}
              className="h-10 w-10 rounded-full object-cover"
            />
          ) : (
            <div className="h-10 w-10 rounded-full bg-gray-200" />
          )}
          <div>
            <p className="text-sm font-semibold">{note.authorName ?? "Unknown"}</p>
            <p className="text-xs text-gray-400">{new Date(note.createdAt).toLocaleString()}</p>
          </div>
        </div>
        <h2 className="text-2xl font-semibold">{note.title}</h2>
        {note.tags && note.tags.length > 0 ? (
          <div className="flex flex-wrap gap-2 mb-3 text-sm text-rose-500">
            {note.tags.map((tag) => (
              <span key={tag} className="rounded-full bg-rose-50 px-3 py-1">
                #{tag}
              </span>
            ))}
          </div>
        ) : null}
        {note.content ? <p className="text-gray-700">{note.content}</p> : null}
        <div className="flex items-center gap-3">
          <LikeButton noteId={note.id} initial={note.likeCount} />
          <span className="text-sm text-gray-500">评论 {note.commentCount}</span>
          <span className="text-sm text-gray-500">收藏 {note.collectCount}</span>
          <span className="text-sm text-gray-500">Views {note.viewCount}</span>
        </div>
      </section>

      <section className="bg-white rounded-xl p-6 shadow-sm space-y-4">
        <h3 className="text-lg font-semibold">Comments</h3>
        <CommentForm noteId={note.id} onAdd={addComment} />
        <div className="space-y-3">
          {note.comments.map((comment) => (
            <div key={comment.id} className="border-b pb-2">
              <p className="text-sm text-gray-700">{comment.content}</p>
              <span className="text-xs text-gray-400">{new Date(comment.createdAt).toLocaleString()}</span>
            </div>
          ))}
        </div>
      </section>
    </main>
  );
}
