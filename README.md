# AlchemyServer

AlchemyServer is a Java-based system powered by HSQLDB designed to manage player interactions, ingredients, and potion crafting. This project contains database management, game logic, and domain objects for tracking progress.

## Project Structure

The project is organized as follows:

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
├── lib/                       # External libraries (e.g., hsqldb.jar, junit, HikariCP, slf4j, etc.)
├── logic/                     # Game logic classes
│   ├── PlayerManager.java
│   └── PotionManager.java
├── object/                    # Domain objects (game entities)
│   ├── Effect.java
│   ├── Ingredient.java
│   ├── Inventory.java
│   ├── KnowledgeBook.java
│   └── Player.java
├── test/                      # Test source files
│   ├── DummyDatabase.java     # A dummy database implementation for unit tests
│   ├── GameManagerTest.java
│   ├── InventoryTest.java
│   ├── PlayerManagerTest.java
│   ├── PotionManagerTest.java
│   ├── TestSuite.java         # Aggregates all unit tests
│   └── integration/           # Integration test source files using a real HSQLDB instance
│       └── RealDatabaseIntegrationTest.java
├── Main.java                  # The main entry point of the program
└── Makefile                   # Build automation script
```

*Note: All JAR files (JUnit, HSQLDB, HikariCP, SLF4J, etc.) are located in the `lib/` folder.*

## How to Compile and Run

### 1. Compile the Program
Use the Makefile to compile the source files and output the class files into the `bin/` folder:
```bash
make compile
```
This command creates the `bin/` folder (if it doesn’t exist) and compiles all the Java source files.

### 2. Start the Database Server
Start the HSQLDB server using:
```bash
make start-server
```
This command launches the server using a relative path (`alchemydb`) for the database file. It will create the necessary database files (e.g., `alchemydb.script`, etc.) in your project root.

If you prefer to start the server manually, run:
```bash
java -cp lib/hsqldb.jar org.hsqldb.Server --database.0 file:alchemydb --dbname.0 mydb
```

### 3. Run the Main Program
After the server is running, execute your main program:
```bash
make run
```
This command waits a few seconds to ensure the server has initialized and then runs `Main`.

### 4. Stop the Server
To stop the HSQLDB server:
```bash
make stop-server
```
This command finds the HSQLDB server process by its name and terminates it gracefully.

### 5. Clean Up Build Files
To remove the compiled class files:
```bash
make clean
```
This deletes the `bin/` folder, keeping your project directory clean.

---
**"Running the React Front-end"**:

---

```markdown
### Running the React Front-end

To view the enhanced UI (with backgrounds, icons, and full navigation), follow these steps:

1. **Navigate to the React Project Folder:**  
   Open a terminal and change directory to the React project (typically called `alchemy-ui`):
   ```bash
   cd alchemy-ui
   ```

2. **Install Dependencies:**  
   If you haven't installed the project dependencies yet, run:
   ```bash
   npm install
   ```

3. **Start the Development Server:**  
   Launch the React development server with:
   ```bash
   npm start
   ```
   This will automatically open the app in your default browser at [http://localhost:3000](http://localhost:3000). If not, manually navigate to that URL.

4. **View the Enhanced UI:**  
   The app will display the improved pages (Home, Login, Register, Dashboard) with assets loaded from `src/assets/images`. You can then interact with the UI—log in, register, and navigate between pages.

> **Note:** The backend (Spring Boot) runs on port 8080, while the React frontend runs on port 3000. They communicate via CORS during development.
```

---



## Automated Testing

### Running Unit Tests

Unit tests are located in the `test/` folder and are compiled into the `test_bin/` directory. To compile and run the unit test suite, execute:

1. **Compile the test files:**
   ```bash
   make compile-tests
   ```
2. **Run the test suite:**
   ```bash
   make test
   ```
The target `test` in the Makefile compiles the tests and runs the `TestSuite` class (which aggregates your unit tests).

### Running Integration Tests

Integration tests use a real HSQLDB instance and are located under `test/integration/`. They verify complete flows such as inventory operations, potion brewing, and player deletion.

Before running integration tests, ensure that:
- The HSQLDB server is running (you can start it using `make start-server`).
- The seeded data (ingredients, effects, etc.) is loaded.

To run integration tests:

1. **Compile integration test files:**
   ```bash
   make integration-compile
   ```
2. **Run the integration test suite:**
   ```bash
   make integration-test
   ```
   This command compiles (if necessary) and runs the `RealDatabaseIntegrationTest` class using JUnit.
3. **Clean up integration test artifacts (if needed):**
   ```bash
   make integration-clean
   ```

---

## CI Pipeline with GitHub Actions

A CI pipeline is set up to run on push or pull requests (as defined in `.github/workflows/ci.yml`). The pipeline:
- Checks out the repository.
- Sets up JDK 11.
- Compiles the main code and unit tests.
- Runs unit tests.
- Starts the HSQLDB server.
- Runs integration tests.
- Stops the database server.
- Cleans up build artifacts.

See the Actions tab in your repository to monitor CI runs.

---

## Using the HSQLDB GUI (Database Manager)

HSQLDB includes a GUI tool called **DatabaseManagerSwing** for viewing tables and running SQL queries.

### Launching the GUI
Run the following command in your project directory:
```bash
java -cp lib/hsqldb.jar org.hsqldb.util.DatabaseManagerSwing
```

### Connecting to the Database
When the GUI opens:
1. **Driver:** Select `HSQL Database Engine Server`
2. **URL:** `jdbc:hsqldb:hsql://localhost:9001/mydb`
3. **User:** `SA`
4. **Password:** *(leave empty)*
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
