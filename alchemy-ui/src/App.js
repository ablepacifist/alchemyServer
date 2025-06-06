import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { UserProvider } from './context/UserContext'; // must make this
// inport all pages
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import About from './pages/About';
import Profile from './components/Profile'
import KnowledgeBook from './pages/KnowledgeBook';

// path to all pages to be called:
function App() {
  return (
    <UserProvider>
      <Router>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/about" element={<About />} /> 
          <Route path="/profile" element={<Profile />} />
          <Route path="/knowledge" element={<KnowledgeBook />} />
        </Routes>
      </Router>
    </UserProvider>
  );
}

export default App;

