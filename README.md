# AlchemyServer

AlchemyServer is a Java-based system powered by HSQLDB designed to manage player interactions, ingredients, and potion crafting. This project contains database management, game logic, and domain objects for tracking game progress. The backend API is powered by Spring Boot and communicates with a real HSQLDB instance, while the React front-end (in the `alchemy-ui` folder) provides an interactive user interface.

## Project Structure

```
AlchemyServer/
├── .github/
│   └── workflows/
│       └── ci.yml              # GitHub Actions CI pipeline configuration
├── alchemydb.log               # Log file for the HSQLDB database
├── alchemydb.properties        # Database properties file
├── alchemydb.script            # Database script file (created at runtime)
├── alchemydb.tmp               # Temporary database file(s)
├── data/                      # Database management classes
│   ├── HSQLDatabase.java       # Handles database initialization, seeding, etc.
│   └── IStubDatabase.java      # Database interaction interface
├── lib/                       # External libraries (e.g., hsqldb.jar, junit, HikariCP, SLF4J, etc.)
├── logic/                     # Game logic classes
│   ├── GameManager.java        # Singleton; central game control
│   ├── PlayerManager.java      # Manages player accounts, inventories, etc.
│   └── PotionManager.java      # Handles potion creation and related logic
├── object/                    # Domain objects (game entities)
│   ├── Effect.java
│   ├── Ingredient.java
│   ├── Inventory.java
│   ├── KnowledgeBook.java
│   └── Player.java
├── test/                      # Test source files
│   ├── DummyDatabase.java     # Dummy DB implementation for unit tests
│   ├── GameManagerTest.java
│   ├── InventoryTest.java
│   ├── PlayerManagerTest.java
│   ├── PotionManagerTest.java
│   ├── TestSuite.java         # Aggregates all unit tests
│   └── integration/           # Integration test source files (using a real HSQLDB instance)
│       └── RealDatabaseIntegrationTest.java
├── alchemy-ui/                # React front-end source (separate project)
│   ├── public/
│   └── src/
│       ├── assets/images/     # UI images and icons
│       ├── components/        # Reusable React components
│       ├── pages/             # Individual pages (Home, About, Login, Register, Dashboard)
│       └── context/           # Global context (e.g., UserContext)
├── Main.java                  # (Optional) Fallback Java main entry point (if needed)
├── build.gradle               # Gradle build file for the back-end Spring Boot application
└── Makefile                   # Legacy Makefile for starting the HSQLDB server and other tasksAlchemyServer/
├── .github/
│   └── workflows/
│       └── ci.yml              # GitHub Actions CI pipeline configuration
├── alchemydb.log               # Log file for the HSQLDB database
├── alchemydb.properties        # Database properties file
├── alchemydb.script            # Database script file (created at runtime)
├── alchemydb.tmp               # Temporary database file(s)
├── lib/                       # External libraries (e.g., hsqldb.jar, junit, HikariCP, SLF4J, etc.)
├── src/
│   └── main/
│       └── java/
│           └── alchemy/
│               ├── api/      # API classes (Spring Boot controllers and endpoints)
│               ├── data/     # Database management classes
│               │   ├── HSQLDatabase.java   # Handles database initialization, seeding, etc.
│               │   └── IStubDatabase.java  # Database interaction interface
│               ├── logic/    # Game logic classes
│               │   ├── GameManager.java      # Singleton; central game control
│               │   ├── PlayerManager.java    # Manages player accounts, inventories, etc.
│               │   └── PotionManager.java    # Handles potion creation and related logic
│               ├── object/   # Domain objects (game entities)
│               │   ├── Effect.java
│               │   ├── Ingredient.java
│               │   ├── Inventory.java
│               │   ├── KnowledgeBook.java
│               │   └── Player.java
│               └── Main.java   # Main entry point of the program
├── alchemy-ui/                # React front-end source (separate project)
│   ├── public/
│   └── src/
│       ├── assets/images/     # UI images and icons
│       ├── components/        # Reusable React components
│       ├── pages/             # Individual pages (Home, About, Login, Register, Dashboard)
│       └── context/           # Global context (e.g., UserContext)
├── test/                      # Test source files
│   ├── DummyDatabase.java     # Dummy DB implementation for unit tests
│   ├── GameManagerTest.java
│   ├── InventoryTest.java
│   ├── PlayerManagerTest.java
│   ├── PotionManagerTest.java
│   ├── TestSuite.java         # Aggregates all unit tests
│   └── integration/           # Integration test source files (using a real HSQLDB instance)
│       └── RealDatabaseIntegrationTest.java
├── build.gradle               # Gradle build file for the back-end Spring Boot application
└── Makefile                   # Legacy Makefile for starting the HSQLDB server and other tasks

```

## How the System Works
- **HSQLDB Server (Port 9001):**  
  The HSQLDB database runs on port **9001** and stores all game data (players, ingredients, potions, etc.). You can start it via Makefile or manually.
  
- **Spring Boot API (Port 8080):**  
  The back-end API is built with Spring Boot and listens on port **8080**. It handles player authentication, inventory management, and potion brewing. The API communicates with HSQLDB on port 9001 by reading/writing game data.
  
- **React Front-end (Port 3000):**  
  The React application (in the `alchemy-ui` folder) provides the user interface. During development, it runs as a separate server on port **3000**. It communicates with the back-end API on port 8080 via HTTP (using CORS).

## How to Run the Program

### 1. Start the HSQLDB Server
You can start the HSQLDB server using either the provided Makefile target or manually.

- **Using Makefile:**
  ```bash
  make start-server
  ```
  This will start the HSQLDB server on port 9001 using the database file defined by the relative path `alchemydb`.

- **Manually:**  
  If you prefer, run:
  ```bash
  java -cp lib/hsqldb.jar org.hsqldb.Server --database.0 file:alchemydb --dbname.0 mydb
  ```

### 2. Start the Spring Boot API
Use Gradle to start the back-end API:
```bash
./gradlew bootRun
```
This command launches the Spring Boot application, which will listen on port 8080. The application uses the HSQLDB server (running on port 9001) for persistence.

### 3. Start the React Front-end
Navigate to the React project folder (`alchemy-ui`) and start the development server:
```bash
cd alchemy-ui
npm install          # (Run this step once if dependencies are not installed)
npm start
```
This will launch the React front-end on [http://localhost:3000](http://localhost:3000). The front-end communicates with the API on port 8080.

### 4. Stopping the Server
To stop the HSQLDB server, use:
```bash
make stop-server
```
This command finds and terminates the HSQLDB server process. To stop the Spring Boot API and React front-end, use the standard termination methods (Ctrl+C) in their respective terminal windows.

## Automated Testing

### Running Unit Tests
Unit tests (in the `test/` folder) are compiled into the `test_bin/` directory. To compile and run the unit test suite using the Makefile:

1. **Compile the Test Files:**
   ```bash
   make compile-tests
   ```
2. **Run the Test Suite:**
   ```bash
   make test
   ```
The `test` target compiles the tests and runs the `TestSuite` class, which aggregates all unit tests.

### Running Integration Tests
Integration tests, located under `test/integration/`, use a real HSQLDB instance and verify full system flows such as potion brewing and inventory operations.

Before running integration tests, ensure:
- The HSQLDB server is running (**make start-server**).
- Seeded data is loaded (if applicable).

Then:
1. **Compile the Integration Test Files:**
   ```bash
   make integration-compile
   ```
2. **Run the Integration Test Suite:**
   ```bash
   make integration-test
   ```
3. **Clean Up Integration Test Artifacts:**
   ```bash
   make integration-clean
   ```

## CI Pipeline with GitHub Actions

The project includes a GitHub Actions workflow in `.github/workflows/ci.yml` that:
- Checks out the repository.
- Sets up JDK 17.
- Uses Gradle to compile and run both unit and integration tests.
- Caches Gradle dependencies to improve build times.

**Note:** The CI pipeline uses Gradle (`./gradlew clean test`) instead of the legacy Makefile compilation. Make sure that all required dependencies (including Spring Boot libraries) are declared in `build.gradle`.

## Using the HSQLDB GUI (Database Manager)

HSQLDB includes a GUI tool called **DatabaseManagerSwing** for interacting with the database.

### Launching the GUI
Run the following command in your project directory:
```bash
java -cp lib/hsqldb.jar org.hsqldb.util.DatabaseManagerSwing
```
### Connecting to the Database
When the GUI opens:
1. **Driver:** HSQL Database Engine Server
2. **URL:** `jdbc:hsqldb:hsql://localhost:9001/mydb`
3. **User:** `SA`
4. **Password:** (Leave blank)
5. Click **Connect**

### Running SQL Queries
Example queries:
```sql
SELECT * FROM PLAYERS;
```
To list all tables:
```sql
SELECT * FROM INFORMATION_SCHEMA.TABLES;
```
To shut down the database manually:
```sql
SHUTDOWN;
```

---

## Summary of Ports and Communication

- **Port 9001 (HSQLDB):**  
  The HSQLDB server stores game data. The Spring Boot API connects to this server to read and write data.
- **Port 8080 (Spring Boot API):**  
  The back-end API handles player logins, game logic, and database interactions.
- **Port 3000 (React Front-end):**  
  The React application, running separately for development with hot reloading, communicates with the API via HTTP (using CORS).

---

## Running the Entire System

1. **Start the HSQLDB server:**  
   `make start-server` (or use the manual command)
2. **Launch the Spring Boot API:**  
   `./gradlew bootRun`
3. **Start the React front-end:**  
   Navigate to `alchemy-ui` and run `npm start`

This setup ensures that the API, database, and UI communicate as intended, providing a seamless gaming experience.

---

*Note:* If you are developing locally, you can rely on the provided Makefile for simpler tasks (like starting/stopping the database server) and Gradle commands for running the API and tests. GitHub Actions CI uses Gradle to automatically build and test the project.
```
---------------------------------