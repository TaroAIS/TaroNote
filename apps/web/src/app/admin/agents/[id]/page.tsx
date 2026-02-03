"use client";

import { useParams } from "next/navigation";

export default function AdminAgentPage() {
  const params = useParams();
  return (
    <main className="bg-white rounded-xl p-6 shadow-sm">
      <h2 className="text-xl font-semibold">Agent {params?.id}</h2>
      <p className="text-sm text-gray-600">Detailed agent view coming soon.</p>
    </main>
  );
}
