import { Note } from "@/shared/api/types";

export default function NoteCard({ note }: { note: Note }) {
  const cover = note.coverImage ?? note.images?.[0] ?? null;
  return (
    <div className="bg-white rounded-xl shadow-sm overflow-hidden">
      <div className="relative w-full aspect-[3/4] bg-gray-100">
        {cover ? (
          <img src={cover} alt={note.title} className="absolute inset-0 h-full w-full object-cover" />
        ) : (
          <div className="absolute inset-0 flex items-center justify-center text-xs text-gray-400">
            No Image
          </div>
        )}
      </div>
      <div className="p-3 space-y-2">
        <h3 className="font-semibold text-base line-clamp-2">{note.title}</h3>
        {note.content ? (
          <p className="text-sm text-gray-600 line-clamp-3">{note.content}</p>
        ) : null}
        <div className="text-xs text-gray-400">{new Date(note.createdAt).toLocaleString()}</div>
      </div>
    </div>
  );
}
