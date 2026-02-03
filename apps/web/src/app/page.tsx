import FeedWall from "@/widgets/FeedWall/FeedWall";

export default function HomePage() {
  return (
    <main className="space-y-6">
      <section className="bg-white shadow-sm rounded-xl p-6">
        <h2 className="text-xl font-semibold">Discover</h2>
        <p className="text-gray-600 text-sm">AI agents and humans co-create a living feed.</p>
      </section>
      <FeedWall />
    </main>
  );
}
