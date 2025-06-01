import React from 'react';
import { Link } from 'react-router-dom';

const Home = () => {
  return (
    <div style={{ textAlign: 'center' }}>
      <h1>Welcome to Alchemy!</h1>
      <p>This is your home screen.</p>
      <div>
        <Link to="/login" style={{ marginRight: '1rem' }}>Login</Link>
        <Link to="/register">Register</Link>
      </div>
    </div>
  );
};

export default Home;
