// File: src/pages/Home.js
import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import { UserContext } from '../context/UserContext';
import background from '../assets/images/background.jpg';
import icon from '../assets/images/icon.jpg';

const Home = () => {
  const { user } = useContext(UserContext);

  const containerStyle = {
    backgroundImage: `url(${background})`,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    minHeight: '100vh',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    fontFamily: 'Arial, sans-serif',
    textAlign: 'center',
    color: '#fff',
    textShadow: '1px 1px 2px rgba(0, 0, 0, 0.7)'
  };

  const buttonStyle = {
    margin: '0.5rem',
    padding: '1rem 2rem',
    fontSize: '1.2rem',
    borderRadius: '5px',
    border: 'none',
    cursor: 'pointer',
    backgroundColor: '#61dafb'
  };

  return (
    <div style={containerStyle}>
      <img src={icon} alt="Alchemy Icon" style={{ width: '150px', marginBottom: '1rem' }} />
      <h1>Welcome to Alchemy!</h1>
      {user ? (
        <p>Logged in as: <strong>{user.username}</strong></p>
      ) : (
        <div>
          <Link to="/login">
            <button style={buttonStyle}>Login</button>
          </Link>
          <Link to="/register">
            <button style={{ ...buttonStyle, marginLeft: '1rem' }}>Register</button>
          </Link>
        </div>
      )}
    </div>
  );
};

export default Home;
