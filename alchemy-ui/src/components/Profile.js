import React, { useContext, useState, useEffect, useCallback, useRef } from 'react';
import { UserContext } from '../context/UserContext';
import { useNavigate, Navigate } from 'react-router-dom';
import { useAvatar } from '../hooks/useAvatar';
import background from '../assets/images/dashboard_background.jpg';

const Profile = () => {
  // 1. Hooks always run in the same order
  const { user, setUser } = useContext(UserContext);
  const navigate = useNavigate();

  const [player, setPlayer] = useState(null);
  const [loading, setLoading] = useState(false);
  const [secretPassword, setSecretPassword] = useState('');
  const [error, setError] = useState('');
  const [avatarUploading, setAvatarUploading] = useState(false);
  const [avatarMsg, setAvatarMsg] = useState('');
  const fileInputRef = useRef(null);

  const { avatarUrl, uploadAvatar, removeAvatar } = useAvatar(user?.username);

  // 2. Memoize the fetch so useEffect deps are satisfied
  const fetchPlayerDetails = useCallback(async () => {
    // guard inside the function
    if (user == null || (user.id == null && user.id !== 0)) return;

    setLoading(true);
    try {
      const res = await fetch(
        `http://96.37.95.22:8080/api/player/${user.id}`,
        { credentials: 'include' }
      );
      if (!res.ok) {
        setError('Failed to fetch player details.');
      } else {
        const data = await res.json();
        setPlayer(data);
      }
    } catch (err) {
      console.error('Error fetching player details:', err);
      setError('Error fetching player details.');
    } finally {
      setLoading(false);
    }
  }, [user]);

  // 3. Unconditional useEffect, with fetchPlayerDetails as dep
  useEffect(() => {
    fetchPlayerDetails();
  }, [fetchPlayerDetails]);

  // 4. Handlers
  const handleLevelUp = async () => {
    if (user == null || (user.id == null && user.id !== 0)) return;

    try {
      const res = await fetch(
        `http://96.37.95.22:8080/api/player/levelup`,
        {
          method: 'POST',
          credentials: 'include',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ playerId: user.id, secretPassword }),
        }
      );
      if (!res.ok) {
        const msg = await res.text();
        alert(`Failed to level up: ${msg}`);
      } else {
        alert('Leveled up successfully!');
        fetchPlayerDetails();
      }
    } catch (err) {
      console.error('Error leveling up:', err);
      alert('Error leveling up.');
    }
  };

  const handleLogout = async () => {
    try {
      await fetch('http://96.37.95.22:8080/api/auth/logout', {
        method: 'POST',
        credentials: 'include',
      });
      setUser(null);
      navigate('/login');
    } catch (err) {
      console.error('Logout failed', err);
    }
  };

  const goBackToDashboard = () => {
    navigate('/dashboard');
  };

  const handleAvatarUpload = async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;
    if (file.size > 2 * 1024 * 1024) {
      setAvatarMsg('File must be under 2 MB');
      return;
    }
    setAvatarUploading(true);
    setAvatarMsg('');
    try {
      await uploadAvatar(file, user?.id);
      setAvatarMsg('Avatar updated!');
    } catch (err) {
      setAvatarMsg('Upload failed — bridge may be offline');
    } finally {
      setAvatarUploading(false);
      if (fileInputRef.current) fileInputRef.current.value = '';
    }
  };

  const handleAvatarRemove = async () => {
    setAvatarUploading(true);
    setAvatarMsg('');
    try {
      await removeAvatar(user?.id);
      setAvatarMsg('Avatar removed');
    } catch {
      setAvatarMsg('Remove failed');
    } finally {
      setAvatarUploading(false);
    }
  };

  // 5. Redirect guard *after* all hooks
  if (!user) {
    return <Navigate to="/login" />;
  }

  // 6. Styles
  const containerStyle = {
    backgroundImage:
      `linear-gradient(rgba(0,0,0,0.65), rgba(0,0,0,0.65)), url(${background})`,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '2rem',
  };
  const cardStyle = {
    backgroundColor: 'rgba(255,255,255,0.9)',
    borderRadius: '8px',
    padding: '2rem 3rem',
    textAlign: 'center',
    maxWidth: '500px',
    width: '100%',
    boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
  };
  const headingStyle = {
    fontSize: '2.5rem',
    marginBottom: '1rem',
    color: '#333',
    fontFamily: `'Segoe UI', Tahoma, Geneva, Verdana, sans-serif`,
  };
  const textStyle = {
    fontSize: '1.2rem',
    color: '#333',
  };
  const inputStyle = {
    padding: '0.5rem',
    marginRight: '1rem',
    fontSize: '1rem',
    borderRadius: '5px',
    border: '1px solid #ccc',
  };
  const buttonStyle = {
    margin: '0.5rem',
    padding: '0.5rem 1rem',
    fontSize: '1rem',
    borderRadius: '5px',
    border: 'none',
    cursor: 'pointer',
    backgroundColor: '#61dafb',
    color: '#333',
    fontWeight: 'bold',
    transition: 'transform 0.2s, box-shadow 0.2s',
  };

  // 7. Render
  return (
    <div style={containerStyle}>
      <div style={cardStyle}>
        <h1 style={headingStyle}>Profile</h1>

        {/* Avatar Section */}
        <div style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
          <div
            style={{ position: 'relative', display: 'inline-block', cursor: 'pointer' }}
            onClick={() => fileInputRef.current?.click()}
            title="Click to change avatar"
          >
            <img
              src={avatarUrl}
              alt={`${user.username}'s avatar`}
              style={{
                width: 110,
                height: 110,
                borderRadius: '50%',
                objectFit: 'cover',
                border: '3px solid #61dafb',
                boxShadow: '0 2px 12px rgba(0,0,0,0.15)',
              }}
            />
          </div>
          <input
            ref={fileInputRef}
            type="file"
            accept="image/jpeg,image/png,image/gif,image/webp"
            style={{ display: 'none' }}
            onChange={handleAvatarUpload}
          />
          <div style={{ marginTop: '0.6rem', display: 'flex', justifyContent: 'center', gap: '0.5rem' }}>
            <button
              onClick={() => fileInputRef.current?.click()}
              disabled={avatarUploading}
              style={{
                ...buttonStyle,
                margin: 0,
                padding: '0.35rem 0.9rem',
                fontSize: '0.85rem',
                opacity: avatarUploading ? 0.6 : 1,
              }}
            >
              {avatarUploading ? 'Uploading…' : 'Upload Photo'}
            </button>
            <button
              onClick={handleAvatarRemove}
              disabled={avatarUploading}
              style={{
                ...buttonStyle,
                margin: 0,
                padding: '0.35rem 0.9rem',
                fontSize: '0.85rem',
                backgroundColor: 'transparent',
                border: '1px solid #ccc',
                color: '#666',
              }}
            >
              Remove
            </button>
          </div>
          {avatarMsg && (
            <p style={{ marginTop: '0.4rem', fontSize: '0.85rem', color: avatarMsg.includes('fail') || avatarMsg.includes('must') ? '#e74c3c' : '#27ae60' }}>
              {avatarMsg}
            </p>
          )}
        </div>

        {loading ? (
          <p style={textStyle}>Loading...</p>
        ) : player ? (
          <div style={{ marginBottom: '1rem' }}>
            <p style={textStyle}>
              <strong>Username:</strong> {player.username}
            </p>
            <p style={textStyle}>
              <strong>Level:</strong> {player.level}
            </p>
          </div>
        ) : (
          <p style={textStyle}>No player details available.</p>
        )}

        <div style={{ marginBottom: '1rem' }}>
          <h3 style={{ ...headingStyle, fontSize: '1.8rem', marginBottom: '0.5rem' }}>
            Level Up
          </h3>
          <input
            type="password"
            placeholder="Enter secret password"
            value={secretPassword}
            onChange={e => setSecretPassword(e.target.value)}
            style={inputStyle}
          />
          <button onClick={handleLevelUp} style={buttonStyle}>
            Level Up
          </button>
        </div>

        <div style={{ marginTop: '1rem' }}>
          <button onClick={goBackToDashboard} style={buttonStyle}>
            Back to Dashboard
          </button>
          <button onClick={handleLogout} style={buttonStyle}>
            Logout
          </button>
        </div>

        {error && <p style={{ color: 'red' }}>{error}</p>}
      </div>
    </div>
  );
};

export default Profile;
