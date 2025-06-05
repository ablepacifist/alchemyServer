import React, { useEffect, useState, useContext } from 'react';
import { UserContext } from '../context/UserContext';
import { Link } from 'react-router-dom';
import background from '../assets/images/dashboard_background.jpg';

const Dashboard = () => {
  const { user } = useContext(UserContext);
  console.log('DASHBOARD mounted. User:',user);
  const playerId = user?.id; // Assumes the user object contains an 'id' field
  console.log('Player Id from context:',playerId);
  // State for inventory data
  const [ingredients, setIngredients] = useState([]);
  const [potions, setPotions] = useState([]);
  const [loading, setLoading] = useState(false);

  // Brew selection state for two ingredients
  const [brewSelection, setBrewSelection] = useState({ ingredient1: '', ingredient2: '' });

  // Fetch the player's inventory from the API
// Fetch the player's inventory from the API
const fetchInventory = async () => {
  if (playerId === undefined || playerId === null) return;
  setLoading(true);
  console.log(`Sending fetch request to inventory API for player: ${playerId}`);
  try {
    const response = await fetch(`http://45.44.165.5:8080/api/player/inventory/${playerId}`);
    console.log("HTTP status:", response.status);
    if (response.ok) {
      const data = await response.json();
      console.log("Inventory data fetched:", data);
      setIngredients(data.ingredients || []);
      setPotions(data.potions || []);
    } else {
      // If the response is not OK, log the error message.
      const errMsg = await response.text();
      console.error("Fetch inventory failed with error:", errMsg);
    }
  } catch (error) {
    console.error("Error fetching inventory:", error);
  } finally {
    setLoading(false);
  }
};


useEffect(() => {
  // Allow playerId of 0 to be valid.
  if (playerId !== undefined && playerId !== null) {
    console.log('Fetching inventory for player:', playerId);
    fetchInventory();
  } else {
    console.log('Player ID is undefined or null, not fetching inventory.');
  }
}, [playerId]);


  // Handle brewing a potion: post selected ingredient IDs to the brew endpoint
  const handleBrewPotion = async () => {
    if (!brewSelection.ingredient1 || !brewSelection.ingredient2) {
      alert('Please select both ingredients for brewing.');
      return;
    }
    try {
      const response = await fetch(`http://45.44.165.5:8080/api/potion/brew`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          playerId,
          ingredientId1: parseInt(brewSelection.ingredient1, 10),
          ingredientId2: parseInt(brewSelection.ingredient2, 10),
        }),
      });
      if (response.ok) {
        alert('Potion brewed successfully!');
        fetchInventory(); // Refresh inventory after brewing
      } else {
        const errMsg = await response.text();
        alert(`Brew failed: ${errMsg}`);
      }
    } catch (error) {
      console.error('Error brewing potion:', error);
      alert('Error brewing potion.');
    }
  };

  // Handle foraging: confirm with the user then call the forage endpoint
const handleForage = async () => {
  if (playerId === undefined || playerId === null) return;
  const confirmed = window.confirm('Are you sure? Did Alex tell you you can forage for today?');
  if (!confirmed) return;
  try {
    console.log(`Sending forage request for player: ${playerId}`);
    const response = await fetch(`http://45.44.165.5:8080/api/player/forage/${playerId}`, { method: 'GET' });
    console.log("Forage HTTP status:", response.status);
    if (response.ok) {
      const data = await response.json();
      console.log("Forage response:", data);
      alert(`Foraged ingredient: ${data.forage}`);
      fetchInventory(); // Refresh inventory after foraging
    } else {
      const errMsg = await response.text();
      console.error("Forage fetch failed with error:", errMsg);
      alert(`Forage failed: ${errMsg}`);
    }
  } catch (error) {
    console.error('Error executing forage:', error);
    alert('Error executing forage.');
  }
};



  // Manually refresh the inventory
  const handleRefresh = () => {
    fetchInventory();
  };

  // --- Styles ---
  const containerStyle = {
    backgroundImage: `url(${background})`,
    backgroundSize: 'cover',
    minHeight: '100vh',
    color: '#fff',
    textAlign: 'center',
    padding: '2rem',
  };

  const headerStyle = { marginBottom: '1rem' };

  const columnsContainer = {
    display: 'flex',
    justifyContent: 'space-around',
    marginBottom: '2rem',
  };

  const listContainer = {
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    borderRadius: '8px',
    padding: '1rem',
    width: '40%',
    maxHeight: '400px',
    overflowY: 'scroll',
  };

  const buttonStyle = {
    margin: '0.5rem',
    padding: '1rem 2rem',
    fontSize: '1.2rem',
    borderRadius: '5px',
    backgroundColor: '#61dafb',
    cursor: 'pointer',
  };

  const formLabelStyle = {
    marginRight: '0.5rem',
  };

  return (
    <div style={containerStyle}>
      <h1 style={headerStyle}>
        Welcome, {user && user.username ? user.username : 'User'}!
      </h1>

      <div style={columnsContainer}>
        {/* Ingredients List */}
        <div style={listContainer}>
          <h2>Ingredients</h2>
          {loading ? (
            <p>Loading...</p>
          ) : ingredients.length === 0 ? (
            <p>No ingredients available.</p>
          ) : (
            <ul style={{ listStyleType: 'none', padding: 0 }}>
              {ingredients.map((ing) => (
                <li key={ing.id}>
                  <strong>{ing.name}</strong> (x{ing.quantity})
                  {ing.effects && ing.effects.length > 0 && (
                    <ul style={{ listStyleType: 'circle' }}>
                      {ing.effects.map((effect, idx) => (
                        <li key={idx}>{effect.title}</li>
                      ))}
                    </ul>
                  )}
                </li>
              ))}
            </ul>
          )}
        </div>

        {/* Potions List */}
        <div style={listContainer}>
          <h2>Potions</h2>
          {loading ? (
            <p>Loading...</p>
          ) : potions.length === 0 ? (
            <p>No potions brewed yet.</p>
          ) : (
            <ul style={{ listStyleType: 'none', padding: 0 }}>
              {potions.map((pot) => (
                <li key={pot.id}>
                  <strong>{pot.name}</strong> (x{pot.quantity})
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>

      {/* Brew Potion Section */}
      <div style={{ textAlign: 'center', marginBottom: '1rem' }}>
        <h2>Brew Potion</h2>
        <div>
          <label style={formLabelStyle}>
            Ingredient 1:
            <select
              value={brewSelection.ingredient1}
              onChange={(e) =>
                setBrewSelection({ ...brewSelection, ingredient1: e.target.value })
              }
              style={{ marginLeft: '0.5rem' }}
            >
              <option value="">Select</option>
              {ingredients.map((ing) => (
                <option key={ing.id} value={ing.id}>
                  {ing.name}
                </option>
              ))}
            </select>
          </label>
        </div>
        <div>
          <label style={formLabelStyle}>
            Ingredient 2:
            <select
              value={brewSelection.ingredient2}
              onChange={(e) =>
                setBrewSelection({ ...brewSelection, ingredient2: e.target.value })
              }
              style={{ marginLeft: '0.5rem' }}
            >
              <option value="">Select</option>
              {ingredients.map((ing) => (
                <option key={ing.id} value={ing.id}>
                  {ing.name}
                </option>
              ))}
            </select>
          </label>
        </div>
        <button style={buttonStyle} onClick={handleBrewPotion}>
          Brew Potion
        </button>
      </div>

      {/* Forage Button */}
      <div style={{ marginBottom: '1rem', textAlign: 'center' }}>
        <button style={buttonStyle} onClick={handleForage}>
          Forage
        </button>
      </div>

      {/* Navigation Buttons */}
      <div style={{ textAlign: 'center' }}>
        <Link to="/profile">
          <button style={buttonStyle}>Profile</button>
        </Link>
        <Link to="/knowledge">
          <button style={buttonStyle}>Knowledge Book</button>
        </Link>
        <Link to="/inventory">
          <button style={buttonStyle}>Inventory</button>
        </Link>
        <button style={buttonStyle} onClick={handleRefresh}>
          Refresh Inventory
        </button>
      </div>
    </div>
  );
};

export default Dashboard;
