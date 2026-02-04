import { Note } from "@/shared/api/types";

export default function NoteCard({ note }: { note: Note }) {
  const cover = note.coverImage ?? note.images?.[0] ?? null;
  return (
    <div className="group bg-white rounded-xl shadow-sm overflow-hidden transition-all duration-200 hover:shadow-lg hover:-translate-y-1 animate-fade-up">
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
        <div className="pointer-events-none absolute inset-0 bg-gradient-to-t from-black/40 via-black/0 to-transparent opacity-50 transition-opacity duration-300 md:opacity-0 md:group-hover:opacity-100" />
        <div className="pointer-events-none absolute bottom-2 right-2 flex gap-2 text-gray-700 opacity-90 transition-all duration-300 md:translate-y-1 md:opacity-0 md:group-hover:translate-y-0 md:group-hover:opacity-100">
          <span className="flex h-8 w-8 items-center justify-center rounded-full bg-white/85 shadow-sm backdrop-blur">
            <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="1.7">
              <path d="M20.6 8.4c0 4.7-8.6 9.6-8.6 9.6S3.4 13.1 3.4 8.4c0-2.5 2-4.5 4.5-4.5 1.6 0 3 0.8 3.8 2 0.8-1.2 2.2-2 3.9-2 2.5 0 4.5 2 4.5 4.5z" />
            </svg>
          </span>
          <span className="flex h-8 w-8 items-center justify-center rounded-full bg-white/85 shadow-sm backdrop-blur">
            <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="1.7">
              <path d="M20 15a4 4 0 0 1-4 4H7l-3 3V7a4 4 0 0 1 4-4h8a4 4 0 0 1 4 4z" />
            </svg>
          </span>
          <span className="flex h-8 w-8 items-center justify-center rounded-full bg-white/85 shadow-sm backdrop-blur">
            <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="1.7">
              <path d="M6 4h12a1 1 0 0 1 1 1v16l-7-4-7 4V5a1 1 0 0 1 1-1z" />
            </svg>
          </span>
        </div>
      </div>
      <div className="p-3 space-y-2">
        <h3 className="font-semibold text-base line-clamp-2">{note.title}</h3>
        <div className="flex items-center gap-2 text-xs text-gray-500">
          {note.authorAvatar ? (
            <img
              src={note.authorAvatar}
              alt={note.authorName ?? "author"}
              className="h-5 w-5 rounded-full object-cover"
            />
          ) : (
            <div className="h-5 w-5 rounded-full bg-gray-200" />
          )}
          <span>{note.authorName ?? "Unknown"}</span>
        </div>
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
