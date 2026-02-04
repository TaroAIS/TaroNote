"use client";

import { useEffect, useState } from "react";
import apiClient from "@/shared/api/client";
import { AgentStatus } from "@/shared/api/types";

export default function AdminDashboardPage() {
  const [agents, setAgents] = useState<AgentStatus[]>([]);
  const [directive, setDirective] = useState("");
  const [thoughts, setThoughts] = useState<string[]>([]);

  const load = async () => {
    const response = await apiClient.get<{ agents: AgentStatus[] }>("/api/admin/agents/active");
    setAgents(response.data.agents);
    const thoughtResponse = await apiClient.get<string[]>("/api/admin/agents/thoughts");
    setThoughts(thoughtResponse.data);
  };

  useEffect(() => {
    load();
  }, []);

  const freeze = async (id: string) => {
    await apiClient.post(`/api/admin/agents/${id}/freeze`);
    await load();
  };

  const inject = async (id: string) => {
    if (!directive.trim()) return;
    await apiClient.post(`/api/admin/agents/${id}/inject`, { directive });
    setDirective("");
  };

  return (
    <main className="space-y-6">
      <section className="bg-white rounded-xl p-6 shadow-sm">
        <h2 className="text-xl font-semibold mb-2">God Mode Dashboard</h2>
        <p className="text-sm text-gray-600">Monitor active agents and inject directives.</p>
      </section>

      <section className="bg-white rounded-xl p-6 shadow-sm space-y-4">
        <div className="flex items-center gap-3">
          <input
            value={directive}
            onChange={(e) => setDirective(e.target.value)}
            className="flex-1 border rounded-lg px-3 py-2 text-sm"
            placeholder="Directive to inject"
          />
        </div>
        <div className="space-y-3">
          {agents.map((agent) => (
            <div key={agent.id} className="border rounded-lg p-3 flex items-center justify-between">
              <div>
                <div className="font-medium">{agent.id}</div>
                <div className="text-xs text-gray-500">Mood {agent.mood} · Energy {agent.energy} · {agent.status}</div>
              </div>
              <div className="flex gap-2">
                <button
                  className="text-sm px-3 py-1 rounded-full bg-gray-100"
                  onClick={() => freeze(agent.id)}
                >
                  Freeze
                </button>
                <button
                  className="text-sm px-3 py-1 rounded-full bg-blue-50 text-blue-600"
                  onClick={() => inject(agent.id)}
                >
                  Inject
                </button>
              </div>
            </div>
          ))}
        </div>
      </section>

      <section className="bg-white rounded-xl p-6 shadow-sm space-y-3">
        <h3 className="text-lg font-semibold">Thought Stream</h3>
        <div className="space-y-2 max-h-64 overflow-auto text-sm text-gray-700">
          {thoughts.map((thought, index) => (
            <div key={`${thought}-${index}`} className="border-b pb-2">
              {thought}
            </div>
          ))}
        </div>
      </section>
    </main>
  );
}
