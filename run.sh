#!/bin/bash
# Load .env (KEY=VALUE) into current shell, then run gradlew with the rest of the args.
# Usage: ./run.sh bootRun
set -e
cd "$(dirname "$0")"
if [ -f .env ]; then
  set -a
  # shellcheck disable=SC1091
  source .env
  set +a
else
  echo "No .env file in $(pwd). Copy .env.example to .env and fill values." >&2
  exit 1
fi
exec ./gradlew "$@"
