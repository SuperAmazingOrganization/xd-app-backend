#!/bin/bash
# Load .env (KEY=VALUE) into current shell, then run gradlew with the rest of the args.
# Usage:
#   ./run.sh bootRun                                 # production-shaped, uses Supabase from .env
#   SPRING_PROFILES_ACTIVE=dev ./run.sh bootRun      # local H2, no Supabase needed
#   ./run.sh bootRun --args='--spring.profiles.active=dev'
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

# If we're on the dev profile, strip the Supabase env vars so the H2 datasource
# in application-dev.properties wins (env vars otherwise override profile files).
if [ "${SPRING_PROFILES_ACTIVE:-}" = "dev" ]; then
  unset SPRING_DATASOURCE_URL SPRING_DATASOURCE_USERNAME SPRING_DATASOURCE_PASSWORD
  unset SUPABASE_URL SUPABASE_KEY SUPABASE_BUCKET
  echo "[dev profile] Supabase env vars unset, using H2 in-memory datasource"
fi

# Force a JDK >= 17 (Gradle 9 requirement). We have 21 installed via Homebrew.
# If you have a 25 JDK or want to use a different one, set JAVA_HOME before invoking.
for candidate in /opt/homebrew/opt/openjdk@21 /Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home /usr/lib/jvm/temurin-25-jdk /usr/lib/jvm/java-21-openjdk; do
  if [ -x "$candidate/bin/java" ]; then
    export JAVA_HOME="$candidate"
    export PATH="$JAVA_HOME/bin:$PATH"
    break
  fi
done
echo "Using JAVA_HOME=$JAVA_HOME, Java: $(java -version 2>&1 | head -1)"
echo "SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-(none)}"
exec ./gradlew "$@"
