# AlchemyServer

AlchemyServer is a Java-based system powered by HSQLDB designed to manage player interactions, ingredients, and potion crafting. This project contains database management, game logic, and domain objects for tracking game progress. The backend API is powered by Spring Boot and communicates with a real HSQLDB instance, while the React front-end (in the `alchemy-ui` folder) provides an interactive user interface.
Of course! Here's the updated **Project Structure** for your alchemically unified 

 
 embedded React frontend, Spring Boot backend on port `38770`, and the centralized potion pipeline :

---

##  Updated Project Structure

```
AlchemyServer/
├── .github/
│   └── workflows/
│       └── ci.yml                 # GitHub Actions CI pipeline configuration
├── alchemy-ui/                    # React frontend source (developed separately)
│   ├── public/
│   └── src/
│       ├── assets/images/         # UI images and icons
│       ├── components/            # Reusable React components
│       ├── pages/                 # Pages (Home, About, Login, Register, Dashboard)
│       └── context/               # Global context (e.g., UserContext)
│   └── build/                     # Production-ready React bundle (copied into backend)
├── alchemydb.log                  # HSQLDB runtime log
├── alchemydb.properties           # HSQLDB properties
├── alchemydb.script               # HSQLDB script (created at runtime)
├── alchemydb.tmp                  # Temporary HSQLDB files
├── lib/                           # External libraries (hsqldb.jar, junit, HikariCP, etc.)
├── src/
│   └── main/
│       ├── java/
│       │   └── alchemy/
│       │       ├── api/           # Spring Boot controllers and endpoints
│       │       ├── data/          # DB connection logic and abstractions
│       │       ├── logic/         # Core game mechanics and player interactions
│       │       ├── object/        # Domain models (Potion, Player, Inventory, etc.)
│       │       └── Main.java      # Spring Boot main class
│       └── resources/
│           ├── application.properties   # Spring Boot config (port, DB, etc.)
│           └── static/           # React frontend build files (served by backend)
│               ├── index.html
│               ├── static/       # JS/CSS bundles
│               ├── manifest.json
│               ├── favicon.ico
│               └── asset-manifest.json
├── test/                          # Test source files
│   ├── DummyDatabase.java         # Mock DB for unit testing
│   ├── GameManagerTest.java
│   ├── InventoryTest.java
│   ├── PlayerManagerTest.java
│   ├── PotionManagerTest.java
│   ├── TestSuite.java             # Aggregated unit tests
│   └── integration/
│       └── RealDatabaseIntegrationTest.java
├── build.gradle                   # Gradle build script (Spring Boot backend)
└── Makefile                       # Legacy build targets (start/stop HSQLDB, compile tests)
```

---

### Port Overview

| Port  | Component               | Purpose                                |
|-------|-------------------------|----------------------------------------|
| 9001  | HSQLDB                  | Persistent game data                   |
| 38770 | Spring Boot + React     | Serves frontend & API through Proton VPN |

---

##  How the System Works

- **HSQLDB Server (Port 9001):**  
  The HSQLDB database stores all game data (players, ingredients, potions, etc.) and runs on port **9001**. It’s started via the Makefile or manually using Java.

- **Spring Boot API + React Frontend (Port 38770):**  
  The Spring Boot back-end now serves both the API and the production-built React frontend. It listens on port **38770**, which is publicly accessible via Proton VPN. The React build is embedded inside the backend and loaded from Spring Boot’s `static/` directory.

---

##  How to Run the Program

### 1. Start the HSQLDB Server

- **Using Makefile:**
  ```bash
  make start-server
  ```

- **Manually:**
  ```bash
  java -cp lib/hsqldb.jar org.hsqldb.Server --database.0 file:alchemydb --dbname.0 mydb
  ```

---

### 2. Build and Embed the React Frontend

Inside the React project folder (`alchemy-ui`):

```bash
cd alchemy-ui
npm install          # (Run once to install dependencies)
npm run build        # Create optimized production build
```

Then, from the root of the Spring Boot project:

```bash
rm -rf src/main/resources/static/*
cp -r alchemy-ui/build/* src/main/resources/static/
```

This embeds the React app directly into the backend so it’s served from `/`.

---

### 3. Start the Spring Boot Backend

Ensure `application.properties` contains:

```properties
server.port=38770
server.address=0.0.0.0
```

Then start the server:

```bash
./gradlew bootRun
```

Spring Boot will now serve both the React frontend and API from `http://72.14.148.9:38770`.

---

### 4. Stopping the Servers

- HSQLDB:
  ```bash
  make stop-server
  ```

- Spring Boot:
  Press `Ctrl+C` in the terminal running `./gradlew bootRun`.

---

## Summary of Communication & Ports

| Component       | Port  | Description                                      |
|----------------|-------|--------------------------------------------------|
| HSQLDB Server   | 9001  | Stores persistent game data                     |
| Spring Boot App | 38770 | Serves both the frontend and backend API        |

---

##  Running the Entire System

1. **Start the HSQLDB server**  
   `make start-server`

2. **Build and embed React frontend**  
   `npm run build` → move files to `static/`

3. **Launch the Spring Boot backend**  
   `./gradlew bootRun` (on port 38770)

Players can now access the UI and all potion-related features via:

```
http://72.14.148.9:38770
```

