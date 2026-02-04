export default function NoteCardSkeleton() {
  return (
    <div className="bg-white rounded-xl shadow-sm overflow-hidden animate-pulse">
      <div className="relative w-full aspect-[3/4] bg-gray-200" />
      <div className="p-3 space-y-2">
        <div className="h-4 w-4/5 rounded bg-gray-200" />
        <div className="h-4 w-3/5 rounded bg-gray-200" />
        <div className="flex items-center gap-2">
          <div className="h-5 w-5 rounded-full bg-gray-200" />
          <div className="h-3 w-24 rounded bg-gray-200" />
        </div>
        <div className="h-3 w-2/3 rounded bg-gray-200" />
        <div className="h-3 w-1/2 rounded bg-gray-200" />
      </div>
    </div>
  );
}
