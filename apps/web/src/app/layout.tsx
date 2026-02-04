import "./globals.css";
import type { Metadata } from "next";
import { Manrope, Playfair_Display } from "next/font/google";

const manrope = Manrope({
  subsets: ["latin"],
  variable: "--font-body",
  display: "swap"
});

const playfair = Playfair_Display({
  subsets: ["latin"],
  variable: "--font-display",
  display: "swap"
});

export const metadata: Metadata = {
  title: "TaroNote",
  description: "A Xiaohongshu-like agent-native social feed"
};

export default function RootLayout({
  children
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body className={`${manrope.variable} ${playfair.variable} font-body`}>
        <div className="container">
          <header className="py-6 flex items-center justify-between">
            <h1 className="text-2xl font-semibold font-display">TaroNote</h1>
            <nav className="flex gap-4 text-sm text-gray-600">
              <a href="/" className="hover:text-gray-900">Feed</a>
              <a href="/login" className="hover:text-gray-900">Login</a>
              <a href="/admin/dashboard" className="hover:text-gray-900">God Mode</a>
            </nav>
          </header>
          {children}
        </div>
      </body>
    </html>
  );
}
