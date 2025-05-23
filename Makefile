# Compiler and paths
JAVAC = javac
JAVA = java
JAR_PATH = lib/hsqldb.jar:lib/HikariCP-3.1.0.jar:lib/slf4j-api-1.7.36.jar:lib/slf4j-simple-1.7.36.jar
SRC = Main.java data/*.java object/*.java logic/*.java
OUT = bin
DB_PATH = alchemydb  # Relative path for the database file (created in the project root)

# Default rule: compile, start the server, then run the program
all: compile start-server run

# Compile Java files into bin/
compile:
	mkdir -p $(OUT)
	$(JAVAC) -d $(OUT) -cp $(JAR_PATH) $(SRC)

# Start the HSQLDB server
start-server:
	@echo "Starting HSQLDB Server..."
	$(JAVA) -cp $(JAR_PATH) org.hsqldb.Server --database.0 file:$(DB_PATH) --dbname.0 mydb &

# Run the main program (after the server starts)
run:
	@echo "Waiting for the database to start..."
	sleep 10  # Give the server time to initialize
	$(JAVA) -cp $(OUT):$(JAR_PATH) Main

# Stop the HSQLDB server (finds the PID and kills it)
stop-server:
	@echo "Stopping HSQLDB Server..."
	kill $(shell ps aux | grep '[h]sqldb.Server' | awk '{print $$2}')

# Clean up compiled files
clean:
	rm -rf $(OUT)
