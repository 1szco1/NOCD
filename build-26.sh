#!/usr/bin/env bash
set -u
ROOT="$(cd "$(dirname "$0")" && pwd)"
GRADLE=gradle
export JAVA_HOME=/usr/lib/jvm/java-25-openjdk
mkdir -p "$ROOT/dist"
status=0
for v in 26.1 26.2 26.3; do
  echo "===== BUILD $v ====="
  cp "$ROOT/versions/$v.properties" "$ROOT/build26/gradle.properties"
  if (cd "$ROOT/build26" && "$GRADLE" --no-daemon -Dorg.gradle.workers.max=1 clean build > ""$ROOT"/build-$v.log" 2>&1); then
    find "$ROOT/build26/build/libs" -maxdepth 1 -name '*.jar' ! -name '*-sources.jar' ! -name '*-dev.jar' -exec cp -f {} "$ROOT/dist/" \;
    echo "OK $v"
  else
    echo "FAILED $v (see "$ROOT"/build-$v.log)"
    tail -30 ""$ROOT"/build-$v.log"
    status=1
  fi
done
exit $status
