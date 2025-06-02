import React, { useContext } from 'react';
import { UserContext } from '../context/UserContext';
import { Link } from 'react-router-dom';
import background from '../assets/images/dashboard_background.jpg'; // imported img form assets

const Dashboard = () => {
  const { user } = useContext(UserContext);
// gerneral layout
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
    color: '#fff',
    textShadow: '1px 1px 2px rgba(0,0,0,0.7)',
    textAlign: 'center'
  };
 // button layout
  const buttonStyle = {
    marginTop: '1rem',
    padding: '1rem 2rem',
    fontSize: '1.2rem',
    borderRadius: '5px',
    border: 'none',
    cursor: 'pointer',
    backgroundColor: '#61dafb'
  };
// what will be displayed on the screen
  return (
    <div style={containerStyle}>
      <h1>Welcome, {user ? user.username : "User"}!</h1>
      <p>This is your dashboard (to be completed later).</p>
      <Link to="/">
        <button style={buttonStyle}>Back to Home</button>
      </Link>
    </div>
  );
};

export default Dashboard;
