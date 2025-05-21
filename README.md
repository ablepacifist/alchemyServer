# AlchemyServer

AlchemyServer is a Java-based system powered by HSQLDB designed to manage player interactions, ingredients, and potion crafting. This project contains database management, game logic, and domain objects for tracking progress.

## Project Structure

The project is organized as follows:

```
AlchemyServer/
├── README.md              # Documentation
├── Makefile               # Build automation script
├── Main.java              # The main entry point of the program
├── data/                  # Database management classes
│   ├── HSQLDatabase.java  # Handles database initialization
│   ├── IStubDatabase.java # Database interaction interface
├── object/                # Domain objects (game entities)
│   ├── Player.java
│   ├── Ingredient.java
│   ├── Inventory.java
│   ├── KnowledgeBook.java
│   ├── Effect.java
├── lib/                   # External libraries
│   └── hsqldb.jar         # HSQLDB library
├── alchemydb.*            # Database files created at runtime (e.g., alchemydb.script, alchemydb.properties)
```

*Note: The relative paths ensure portability. All files and folders reside in the project root or relative to it.*

## How to Compile and Run

### 1. Compile the Program
Use the Makefile to compile the source files and output the class files into the `bin/` folder:
```bash
make compile
```
This command creates the `bin/` folder (if it doesn't exist) and compiles all the Java source files.

### 2. Start the Database Server
Start the HSQLDB server using:
```bash
make start-server
```
This command launches the server using a relative path (`alchemydb`) for the database file. It will create the necessary database files (alchemydb.script, etc.) in your project root.

If you prefer to start the server manually, use:
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
When you’re done, you can stop the server using:
```bash
make stop-server
```
This command finds the HSQLDB server process by its name and kills it gracefully.

### 5. Clean Up Build Files
To remove the compiled class files:
```bash
make clean
```
This deletes the `bin/` folder, keeping your project directory clean.



## 🖥️ Using the HSQLDB GUI (Database Manager)

HSQLDB includes a built-in graphical tool called **DatabaseManagerSwing**, which allows you to view tables and run SQL queries.

### **Steps to Launch the GUI**
Run the following command in your project directory:
```bash
java -cp lib/hsqldb.jar org.hsqldb.util.DatabaseManagerSwing
```
This will open the **Database Manager** window.

### **Connecting to Your Database**
Once the GUI is open:
1. **Driver:** Select `HSQL Database Engine Server`
2. **URL:** `jdbc:hsqldb:hsql://localhost:9001/mydb`
3. **User:** `SA`
4. **Password:** *(leave empty)*
5. Click **Connect**

### **Running SQL Queries**
Once connected, you can execute queries such as:
```sql
SELECT * FROM PLAYERS;
```
To check which tables exist:
```sql
SELECT * FROM INFORMATION_SCHEMA.TABLES;
```
To shut down the database manually:
```sql
SHUTDOWN;
```

## Troubleshooting

- **Server Startup Errors (e.g., Address already in use):**  
  Run `make stop-server` to ensure no other HSQLDB server is running, or check for processes using port 9001 with:
  ```bash
  netstat -tulnp | grep 9001
  ```

- **"User lacks privilege or object not found" Errors:**  
  Verify that the `createTables()` method in `HSQLDatabase.java` correctly initializes all tables. If needed, delete the existing database files (alchemydb.*) and run the server/compiled program again.

- **Compilation Issues Related to Folder Structure:**  
  Ensure that all your object classes are in the `object/` folder and that the imports in your code match this structure.

---
