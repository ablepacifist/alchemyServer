#!/bin/bash
# Load layered environment variables: this module's own .env first, then the
# root .env (or $MASTER_ENV_FILE if set) which overrides it. Both are optional
# - the app also runs on the ${KEY:default} values in application.properties.
set -a
[ -f ./.env ] && source ./.env
[ -f "${MASTER_ENV_FILE:-../.env}" ] && source "${MASTER_ENV_FILE:-../.env}"
set +a

# Start the server
./gradlew bootRun
