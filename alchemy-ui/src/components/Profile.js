import React, { useContext, useState, useEffect } from 'react';
import { UserContext } from '../context/UserContext';
import { useNavigate } from 'react-router-dom';
import background from '../assets/images/dashboard_background.jpg';

const Profile = () => {
  const { user, setUser } = useContext(UserContext);
  const navigate = useNavigate();
  const [player, setPlayer] = useState(null);
  const [loading, setLoading] = useState(false);
  const [secretPassword, setSecretPassword] = useState('');
  const [error, setError] = useState('');

  // Fetch player details using the GET /api/player/{playerId} endpoint.
  const fetchPlayerDetails = async () => {
    if (user === null || (user.id === undefined && user.id !== 0)) return;
    setLoading(true);
    try {
      const response = await fetch(`http://45.44.165.5:8080/api/player/${user.id}`);
      if (response.ok) {
        const data = await response.json();
        setPlayer(data);
      } else {
        setError("Failed to fetch player details.");
      }
    } catch (err) {
      console.error("Error fetching player details:", err);
      setError("Error fetching player details.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPlayerDetails();
  }, [user]);

  // Handler to level up the player.
  const handleLevelUp = async () => {
    if (user === null || (user.id === undefined && user.id !== 0)) return;
    try {
      const response = await fetch(`http://45.44.165.5:8080/api/player/levelup`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ playerId: user.id, secretPassword })
      });
      if (response.ok) {
        alert('Leveled up successfully!');
        // Refresh details – the returned player might have an updated level.
        fetchPlayerDetails();
      } else {
        const errMsg = await response.text();
        alert(`Failed to level up: ${errMsg}`);
      }
    } catch (err) {
      console.error("Error leveling up:", err);
      alert("Error leveling up.");
    }
  };

  // Logout: clear the user from context and navigate to login.
  const handleLogout = () => {
    setUser(null);
    navigate('/home');
  };

  // Return to dashboard.
  const goBackToDashboard = () => {
    navigate('/dashboard');
  };

  return (
    <div style={{
      backgroundImage: `url(${background})`,
      backgroundSize: 'cover',
      minHeight: '100vh',
      color: '#fff',
      textAlign: 'center',
      padding: '2rem'
    }}>
      <h1>Profile</h1>
      {loading ? (
        <p>Loading...</p>
      ) : player ? (
        <div style={{ marginBottom: '1rem' }}>
          <p><strong>Username:</strong> {player.username}</p>
          <p><strong>Level:</strong> {player.level}</p>
        </div>
      ) : (
        <p>No player details available.</p>
      )}
      <div>
        <h3>Level Up</h3>
        <input 
          type="password"
          placeholder="Enter secret password"
          value={secretPassword}
          onChange={(e) => setSecretPassword(e.target.value)}
          style={{ padding: '0.5rem', marginRight: '1rem' }}
        />
        <button onClick={handleLevelUp} style={{ padding: '0.5rem 1rem' }}>
          Level Up
        </button>
      </div>
      <div style={{ marginTop: '1rem' }}>
        <button onClick={goBackToDashboard} style={{ marginRight: '1rem', padding: '0.5rem 1rem' }}>
          Back to Dashboard
        </button>
        <button onClick={handleLogout} style={{ padding: '0.5rem 1rem' }}>
          Logout
        </button>
      </div>
      {error && <p style={{ color: 'red' }}>{error}</p>}
    </div>
  );
};

export default Profile;
