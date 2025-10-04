# Frontend PC Setup Guide

## Overview
This guide helps you move the frontend to a new PC while keeping the backend on Bilbo PC.

**Current Setup:**
- Backend (Bilbo PC): `see-recover.gl.at.ply.gg:36567` → `127.0.0.1:36567`
- Frontend (New PC): Will use a new tunnel

## Prerequisites on New PC
1. Node.js (v16 or higher)
2. npm or yarn
3. playit tunnel software
4. Git (optional, for cloning)

## Step-by-Step Setup

### 1. Transfer Frontend Code
```bash
# Option A: Copy the entire alchemy-ui folder to new PC
# Option B: Clone from git repository
git clone <your-repo> 
cd alchemyServer/alchemy-ui
```

### 2. Install Dependencies
```bash
npm install
```

### 3. Setup Environment Configuration
```bash
# Copy the template file
cp .env.newpc .env

# Edit .env file and update:
# - REACT_APP_FRONTEND_URL with your new tunnel URL
```

### 4. Setup playit Tunnel on New PC
```bash
# Install playit
# Create a new tunnel for port 3001
playit tunnel tcp 3001

# Note the tunnel URL (e.g., "new-tunnel-name.ply.gg")
```

### 5. Update Backend CORS (on Bilbo PC)
Add your new tunnel URL to the backend CORS configuration:

In `AuthController.java` and `PlayerController.java`, add your new tunnel URL:
```java
@CrossOrigin(origins = {
    "http://localhost:3000", "http://localhost:3001", 
    "http://127.0.0.1:3000", "http://127.0.0.1:3001",
    "https://see-recover.gl.at.ply.gg", 
    "http://see-recover.gl.at.ply.gg",
    "https://your-new-tunnel.ply.gg",  // <- Add this line
    "http://your-new-tunnel.ply.gg"    // <- Add this line
}, allowCredentials = "true")
```

### 6. Update Frontend .env File
```env
REACT_APP_API_URL=https://see-recover.gl.at.ply.gg/api
PORT=3001
REACT_APP_ENV=production
REACT_APP_FRONTEND_URL=https://your-new-tunnel.ply.gg
```

### 7. Start Frontend
```bash
npm start
```

## Testing the Setup

### Test Connectivity
1. Frontend should start on port 3001
2. playit tunnel should expose it publicly
3. Backend API calls should work through the tunnel

### Test API Connection
Open browser developer tools and verify:
- No CORS errors
- API calls to `https://see-recover.gl.at.ply.gg/api/*` work
- Authentication flows properly

## Troubleshooting

### CORS Errors
- Ensure your new tunnel URL is added to backend CORS
- Restart backend after CORS changes
- Check both http:// and https:// variants

### Network Issues
- Verify playit tunnel is running on both PCs
- Check firewall settings
- Ensure ports 3001 (frontend) and 36567 (backend) are accessible

### Environment Variables
- Restart frontend after changing .env
- Verify REACT_APP_API_URL points to correct backend tunnel
- Check for typos in tunnel URLs

## Quick Reference

**Backend (Bilbo PC):**
- URL: `https://see-recover.gl.at.ply.gg/api`
- Port: 36567
- Files to edit for new frontend: AuthController.java, PlayerController.java

**Frontend (New PC):**
- Port: 3001
- Main config file: `.env`
- Start command: `npm start`

## Architecture After Setup
```
[Frontend PC] → [playit tunnel] → [Internet] → [Bilbo PC tunnel] → [Backend PC]
     ↓                                                                    ↓
  React App                                                        Spring Boot API
  Port 3001                                                         Port 36567
```

This setup allows complete separation while maintaining secure communication through playit tunnels.