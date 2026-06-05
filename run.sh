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
# Pick the highest Java we can find (Gradle 9 needs 17+, Spring Boot 4 toolchain wants 25)
if [ -z "$JAVA_HOME" ]; then
  for candidate in /opt/homebrew/opt/openjdk@21 /Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home /usr/lib/jvm/temurin-25-jdk; do
    if [ -x "$candidate/bin/java" ]; then
      export JAVA_HOME="$candidate"
      export PATH="$JAVA_HOME/bin:$PATH"
      break
    fi
  done
fi
echo "Using JAVA_HOME=$JAVA_HOME, Java: $(java -version 2>&1 | head -1)"
exec ./gradlew "$@"
