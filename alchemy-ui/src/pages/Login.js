import React, { useState, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { UserContext } from '../context/UserContext';
import background from '../assets/images/background.jpg';
const API_URL = process.env.REACT_APP_API_URL;
// const login here is a component that handles user login
const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const { setUser } = useContext(UserContext);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  // This will run when user submits the login
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      // this is the JSON package that will be sent to the server
      // can replace the fetch call with '${API_URL}/api/auth/login' ?
      const response = await fetch("http://45.44.165.5:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
      });
      if (!response.ok) { // it failed to login
        throw new Error('Login failed');
      }
      const data = await response.json();
      console.log("Login successful:", data);
      // Update context with the username and navigate to dashboard
      setUser({ id: data.playerId, username: data.username || username });
      navigate("/dashboard");
    } catch (err) {
      console.error("Login error:", err);
      setError("Invalid username or password.");
    }
  };

  const containerStyle = {
    backgroundImage: `url(${background})`,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontFamily: 'Arial, sans-serif'
  };

  const formStyle = {
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    padding: '2rem',
    borderRadius: '10px',
    boxShadow: '0 0 10px rgba(0,0,0,0.5)'
  };

  const inputStyle = {
    width: '100%',
    padding: '0.5rem',
    marginTop: '0.5rem',
    marginBottom: '1rem',
    borderRadius: '4px',
    border: '1px solid #ccc'
  };

  const buttonStyle = {
    width: '100%',
    padding: '0.75rem',
    backgroundColor: '#61dafb',
    border: 'none',
    borderRadius: '5px',
    cursor: 'pointer',
    fontSize: '1rem'
  };
  // this is what will actually be displayed on the screen
  return (
    <div style={containerStyle}>
      <div style={formStyle}>
        <h1>Login</h1>
        <form onSubmit={handleSubmit}>
          <div>
            <label>Username:</label>
            <input 
              type="text" 
              value={username} 
              onChange={(e) => setUsername(e.target.value)}
              style={inputStyle}
            />
          </div>
          <div>
            <label>Password:</label>
            <input 
              type="password" 
              value={password} 
              onChange={(e) => setPassword(e.target.value)}
              style={inputStyle}
            />
          </div>
          {error && <p style={{ color: 'red' }}>{error}</p>}
          <button type="submit" style={buttonStyle}>Login</button>
        </form>
        <p>
          Don't have an account? <Link to="/register">Register here</Link>.
        </p>
      </div>
    </div>
  );
};

export default Login;
