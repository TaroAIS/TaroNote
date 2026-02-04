import { Note } from "@/shared/api/types";

export default function NoteCard({ note }: { note: Note }) {
  const cover = note.coverImage ?? note.images?.[0] ?? null;
  return (
    <div className="group bg-white rounded-xl shadow-sm overflow-hidden transition-shadow duration-200 hover:shadow-lg animate-fade-up">
      <div className="relative w-full aspect-[3/4] bg-gray-100 overflow-hidden">
        {cover ? (
          <img
            src={cover}
            alt={note.title}
            className="absolute inset-0 h-full w-full object-cover transition-transform duration-300 group-hover:scale-105"
          />
        ) : (
          <div className="absolute inset-0 flex items-center justify-center text-xs text-gray-400">
            No Image
          </div>
        )}
      </div>
      <div className="p-3 space-y-2">
        <h3 className="font-semibold text-base line-clamp-2">{note.title}</h3>
        {note.tags && note.tags.length > 0 ? (
          <div className="flex flex-wrap gap-1 text-xs text-rose-500">
            {note.tags.slice(0, 3).map((tag) => (
              <span key={tag} className="rounded-full bg-rose-50 px-2 py-0.5">
                #{tag}
              </span>
            ))}
          </div>
        ) : null}
        {note.content ? (
          <p className="text-sm text-gray-600 line-clamp-3">{note.content}</p>
        ) : null}
        <div className="flex gap-3 text-xs text-gray-400">
          <span>赞 {note.likeCount ?? 0}</span>
          <span>评 {note.commentCount ?? 0}</span>
          <span>藏 {note.collectCount ?? 0}</span>
        </div>
        <div className="text-xs text-gray-400">{new Date(note.createdAt).toLocaleString()}</div>
      </div>
    </div>
  );
}
