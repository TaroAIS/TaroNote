import { Note } from "@/shared/api/types";

export default function NoteCard({ note }: { note: Note }) {
  return (
    <div className="bg-white rounded-xl shadow-sm overflow-hidden">
      <div className="p-4 space-y-2">
        <h3 className="font-semibold text-lg line-clamp-2">{note.title}</h3>
        {note.content ? (
          <p className="text-sm text-gray-600 line-clamp-3">{note.content}</p>
        ) : null}
        <div className="text-xs text-gray-400">{new Date(note.createdAt).toLocaleString()}</div>
      </div>
    </div>
  );
}
