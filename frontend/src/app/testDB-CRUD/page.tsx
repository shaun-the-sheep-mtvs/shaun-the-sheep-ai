"use client";
import React, { useEffect, useState } from "react";
import { Post } from "../../types/Post";

const API_URL = "http://localhost:8080/api/posts";

export default function TestDBCRUD() {
  const [posts, setPosts] = useState<Post[]>([]);
  const [form, setForm] = useState<{ title: string; content: string }>({ title: "", content: "" });
  const [editing, setEditing] = useState<Post | null>(null);

  useEffect(() => {
    fetch(API_URL)
      .then(res => res.json())
      .then(setPosts);
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (editing) {
      const res = await fetch(`${API_URL}/${editing.id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ ...editing, ...form }),
      });
      if (res.ok) {
        const updated = await res.json();
        setPosts(posts.map(p => (p.id === editing.id ? updated : p)));
        setEditing(null);
        setForm({ title: "", content: "" });
      }
    } else {
      const res = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });
      if (res.ok) {
        setPosts([...posts, await res.json()]);
        setForm({ title: "", content: "" });
      }
    }
  };

  const handleEdit = (post: Post) => {
    setEditing(post);
    setForm({ title: post.title, content: post.content });
  };

  const handleDelete = async (id?: number) => {
    if (!id) return;
    const res = await fetch(`${API_URL}/${id}`, { method: "DELETE" });
    if (res.ok) setPosts(posts.filter(p => p.id !== id));
  };

  return (
    <main style={{ maxWidth: 600, margin: "2rem auto", fontFamily: "sans-serif" }}>
      <h1>Simple Board</h1>
      <form onSubmit={handleSubmit} style={{ marginBottom: 24 }}>
        <input
          name="title"
          value={form.title}
          onChange={handleChange}
          placeholder="Title"
          required
          style={{ width: "100%", marginBottom: 8, padding: 8 }}
        />
        <textarea
          name="content"
          value={form.content}
          onChange={handleChange}
          placeholder="Content"
          required
          rows={4}
          style={{ width: "100%", marginBottom: 8, padding: 8 }}
        />
        <button type="submit" style={{ padding: "8px 16px" }}>
          {editing ? "Update" : "Create"}
        </button>
        {editing && (
          <button
            type="button"
            onClick={() => {
              setEditing(null);
              setForm({ title: "", content: "" });
            }}
            style={{ marginLeft: 8, padding: "8px 16px" }}
          >
            Cancel
          </button>
        )}
      </form>
      <ul style={{ listStyle: "none", padding: 0 }}>
        {posts.map(post => (
          <li key={post.id} style={{ border: "1px solid #ccc", marginBottom: 12, padding: 12 }}>
            <h3>{post.title}</h3>
            <p>{post.content}</p>
            <div>
              <button onClick={() => handleEdit(post)} style={{ marginRight: 8 }}>
                Edit
              </button>
              <button onClick={() => handleDelete(post.id)} style={{ color: "red" }}>
                Delete
              </button>
            </div>
          </li>
        ))}
      </ul>
    </main>
  );
} 