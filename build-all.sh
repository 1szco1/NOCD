#!/usr/bin/env bash
set -u
cd "$(dirname "$0")"
GRADLE=gradle
mkdir -p dist
status=0
for f in versions/*.properties; do
  v=$(basename "$f" .properties)
  case "$v" in 26.*) continue ;; esac
  echo "===== BUILD $v ====="
  cp "$f" gradle.properties
  log="build-$v.log"
  if "$GRADLE" --no-daemon -Dorg.gradle.workers.max=1 clean build > "$log" 2>&1; then
    find build/libs -maxdepth 1 -name '*.jar' ! -name '*-sources.jar' ! -name '*-dev.jar' -exec cp -f {} dist/ \;
    echo "OK $v"
  else
    echo "FAILED $v (see $log)"
    tail -30 "$log"
    status=1
  fi
done
exit $status
