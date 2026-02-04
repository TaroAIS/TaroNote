"use client";

import { useState } from "react";
import apiClient from "@/shared/api/client";
import { AuthResponse } from "@/shared/api/types";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [username, setUsername] = useState("");
  const [mode, setMode] = useState<"login" | "register">("login");
  const [message, setMessage] = useState("");

  const submit = async (event: React.FormEvent) => {
    event.preventDefault();
    setMessage("");
    try {
      const endpoint = mode === "login" ? "/api/auth/login" : "/api/auth/register";
      const payload = mode === "login" ? { email, password } : { username, email, password };
      const response = await apiClient.post<AuthResponse>(endpoint, payload);
      window.localStorage.setItem("taronote_token", response.data.token);
      setMessage("Success. Token stored.");
    } catch (error) {
      setMessage("Auth failed. Check credentials.");
    }
  };

  return (
    <main className="max-w-lg mx-auto bg-white rounded-xl shadow-sm p-6 space-y-4">
      <h2 className="text-xl font-semibold">{mode === "login" ? "Login" : "Register"}</h2>
      <form onSubmit={submit} className="space-y-3">
        {mode === "register" ? (
          <input
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            className="w-full border rounded-lg px-3 py-2 text-sm"
            placeholder="Username"
          />
        ) : null}
        <input
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="w-full border rounded-lg px-3 py-2 text-sm"
          placeholder="Email"
        />
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="w-full border rounded-lg px-3 py-2 text-sm"
          placeholder="Password"
        />
        <button type="submit" className="w-full bg-gray-900 text-white rounded-lg py-2 text-sm">
          {mode === "login" ? "Login" : "Create account"}
        </button>
      </form>
      <button
        type="button"
        className="text-sm text-gray-500"
        onClick={() => setMode(mode === "login" ? "register" : "login")}
      >
        {mode === "login" ? "Need an account? Register" : "Already have an account? Login"}
      </button>
      {message ? <p className="text-sm text-gray-600">{message}</p> : null}
    </main>
  );
}
