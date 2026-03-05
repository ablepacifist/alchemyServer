import { useState, useEffect, useCallback } from 'react';

// Bridge always runs via Cloudflare tunnel
const BRIDGE_BASE_URL = 'https://voice.alex-dyakin.com';
const DEFAULT_AVATAR = `${BRIDGE_BASE_URL}/uploads/avatars/default.jpg`;

/**
 * Hook to fetch, upload, and remove user avatars via the Mumble Bridge avatar API.
 * @param {string} username - The username to fetch the avatar for
 * @returns {{ avatarUrl, loading, error, uploadAvatar, removeAvatar, refetch }}
 */
export const useAvatar = (username) => {
  const [avatarUrl, setAvatarUrl] = useState(DEFAULT_AVATAR);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchAvatar = useCallback(async () => {
    if (!username) {
      setLoading(false);
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const res = await fetch(
        `${BRIDGE_BASE_URL}/api/avatar/${encodeURIComponent(username)}`
      );
      if (res.ok) {
        const data = await res.json();
        setAvatarUrl(`${BRIDGE_BASE_URL}${data.avatarUrl}`);
      } else {
        setAvatarUrl(DEFAULT_AVATAR);
      }
    } catch (err) {
      console.warn('Avatar fetch failed (bridge may be offline):', err.message);
      setAvatarUrl(DEFAULT_AVATAR);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, [username]);

  useEffect(() => {
    fetchAvatar();
  }, [fetchAvatar]);

  const uploadAvatar = async (file, userId) => {
    const formData = new FormData();
    formData.append('username', username);
    if (userId != null) formData.append('userId', String(userId));
    formData.append('avatar', file);

    const res = await fetch(`${BRIDGE_BASE_URL}/api/avatar/upload`, {
      method: 'POST',
      body: formData,
    });

    if (!res.ok) {
      const text = await res.text().catch(() => 'Upload failed');
      throw new Error(text);
    }

    const data = await res.json();
    const newUrl = `${BRIDGE_BASE_URL}${data.avatarUrl}`;
    setAvatarUrl(newUrl);
    return newUrl;
  };

  const removeAvatar = async (userId) => {
    const res = await fetch(`${BRIDGE_BASE_URL}/api/avatar/remove`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, userId }),
    });

    if (res.ok) {
      setAvatarUrl(DEFAULT_AVATAR);
    }
  };

  return { avatarUrl, loading, error, uploadAvatar, removeAvatar, refetch: fetchAvatar };
};

export { DEFAULT_AVATAR, BRIDGE_BASE_URL };
