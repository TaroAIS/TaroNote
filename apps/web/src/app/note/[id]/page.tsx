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
      <section className="bg-white rounded-xl p-6 shadow-sm">
        <h2 className="text-2xl font-semibold mb-2">{note.title}</h2>
        {note.content ? <p className="text-gray-700 mb-4">{note.content}</p> : null}
        <div className="flex items-center gap-3">
          <LikeButton noteId={note.id} initial={note.likeCount} />
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
