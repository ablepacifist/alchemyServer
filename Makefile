# Compiler and paths
JAVAC = javac
JAVA = java
JAR_PATH = lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar:lib/hsqldb.jar:lib/HikariCP-3.1.0.jar 
#JAR_PATH = lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar:lib/hsqldb.jar:lib/HikariCP-3.1.0.jar:lib/slf4j-api-1.7.36.jar:lib/slf4j-simple-1.7.36.jar
SRC = src/main/java/alchemy/Main.java \
      src/main/java/alchemy/data/*.java \
      src/main/java/alchemy/logic/*.java \
      src/main/java/alchemy/object/*.java
OUT = bin

# Test based files
TEST_SRC = $(wildcard test/*.java)
TEST_OUT = test_bin

DB_PATH = alchemydb  # Relative path for the database file (created in the project root)

# Default rule: compile, start the server, then run the program
all: compile start-server run

# Compile Java files into bin/
compile:
	mkdir -p $(OUT)
	$(JAVAC) -d $(OUT) -cp $(JAR_PATH) $(SRC)

# Compile test files into test_bin/
compile-tests:
	mkdir -p $(TEST_OUT)
	$(JAVAC) -d $(TEST_OUT) -cp $(OUT):$(JAR_PATH) $(TEST_SRC)


# Start the HSQLDB server
start-server:
	@echo "Starting HSQLDB Server on public interface..."
	$(JAVA) -cp $(JAR_PATH) org.hsqldb.Server --address 0.0.0.0 --port 9001 --database.0 file:$(DB_PATH) --dbname.0 mydb &

# Run the main program (after the server starts)
run:
	@echo "Waiting for the database to start..."
	sleep 10  # Give the server time to initialize
	$(JAVA) -cp $(OUT):$(JAR_PATH) Main

# Run the test suite (must compile first)
test: compile-tests
	@echo "Running tests..."
	$(JAVA) -cp $(TEST_OUT):$(OUT):$(JAR_PATH) test.TestSuite

# Stop the HSQLDB server (finds the PID and kills it)
stop-server:
	@echo "Stopping HSQLDB Server..."
	kill $(shell ps aux | grep '[h]sqldb.Server' | awk '{print $$2}')

# Clean up compiled files
clean:
	rm -rf $(OUT)

clean-tests:
	rm -rf $(TEST_OUT)


# Integration Test Compilation
INTEGRATION_SRC = $(wildcard test/integration/*.java)
INTEGRATION_OUT = integration_bin

integration-compile:
	mkdir -p $(INTEGRATION_OUT)
	$(JAVAC) -d $(INTEGRATION_OUT) -cp $(OUT):$(JAR_PATH) $(INTEGRATION_SRC)

integration-test: integration-compile
	@echo "Running integration tests..."
	$(JAVA) -cp $(INTEGRATION_OUT):$(OUT):$(JAR_PATH) org.junit.runner.JUnitCore test.integration.RealDatabaseIntegrationTest

integration-clean:
	rm -rf $(INTEGRATION_OUT);
