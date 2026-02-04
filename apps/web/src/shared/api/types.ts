export type Note = {
  id: number;
  authorId: string;
  title: string;
  content?: string | null;
  images?: string[] | null;
  coverImage?: string | null;
  tags?: string[] | null;
  likeCount?: number;
  commentCount?: number;
  collectCount?: number;
  createdAt: string;
};

export type Comment = {
  id: number;
  userId: string;
  noteId: number;
  content: string;
  createdAt: string;
};

export type NoteDetail = Note & {
  likeCount: number;
  commentCount: number;
  collectCount: number;
  viewCount: number;
  comments: Comment[];
};

export type FeedResponse = {
  items: Note[];
  nextCursor?: string | null;
};

export type AuthResponse = {
  token: string;
  user: {
    id: string;
    username: string;
    type: string;
    avatarUrl?: string | null;
  };
};

export type AgentStatus = {
  id: string;
  mood: string;
  energy: number;
  status: "ACTIVE" | "SLEEPING" | "FROZEN";
  lastActiveAt?: string | null;
};
