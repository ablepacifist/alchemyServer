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
    const response = await fetch(`http://96.37.95.22:8080/api/player/${user.id}`, {
      credentials: 'include'
    });
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
    if (user) {
      fetchPlayerDetails();
    }
  }, [user]);

  // Handler to level up the player.
  const handleLevelUp = async () => {
    if (user === null || (user.id === undefined && user.id !== 0)) return;
    try {
      const response = await fetch(`http://96.37.95.22:8080/api/player/levelup`, {
        credentials: 'include',
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
    navigate('/');
  };

  // Return to dashboard.
  const goBackToDashboard = () => {
    navigate('/dashboard');
  };

  // Styling for the Profile UI
  const containerStyle = {
    backgroundImage: `linear-gradient(rgba(0,0,0,0.65), rgba(0,0,0,0.65)), url(${background})`,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '2rem'
  };

  const cardStyle = {
    backgroundColor: 'rgba(255,255,255,0.9)',
    borderRadius: '8px',
    padding: '2rem 3rem',
    textAlign: 'center',
    maxWidth: '500px',
    width: '100%',
    boxShadow: '0 4px 6px rgba(0,0,0,0.1)'
  };

  const headingStyle = {
    fontSize: '2.5rem',
    marginBottom: '1rem',
    color: '#333',
    fontFamily: `'Segoe UI', Tahoma, Geneva, Verdana, sans-serif`
  };

  const textStyle = {
    fontSize: '1.2rem',
    color: '#333'
  };

  const inputStyle = {
    padding: '0.5rem',
    marginRight: '1rem',
    fontSize: '1rem',
    borderRadius: '5px',
    border: '1px solid #ccc'
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
    transition: 'transform 0.2s, box-shadow 0.2s'
  };

  return (
    <div style={containerStyle}>
      <div style={cardStyle}>
        <h1 style={headingStyle}>Profile</h1>
        {loading ? (
          <p style={textStyle}>Loading...</p>
        ) : player ? (
          <div style={{ marginBottom: '1rem' }}>
            <p style={textStyle}><strong>Username:</strong> {player.username}</p>
            <p style={textStyle}><strong>Level:</strong> {player.level}</p>
          </div>
        ) : (
          <p style={textStyle}>No player details available.</p>
        )}
        <div style={{ marginBottom: '1rem' }}>
          <h3 style={{ ...headingStyle, fontSize: '1.8rem', marginBottom: '0.5rem' }}>Level Up</h3>
          <input 
            type="password"
            placeholder="Enter secret password"
            value={secretPassword}
            onChange={(e) => setSecretPassword(e.target.value)}
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
