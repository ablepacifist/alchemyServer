#!/bin/bash
# Load environment variables from parent .env file
set -a
source ../.env
set +a

# Start the server
./gradlew bootRun
