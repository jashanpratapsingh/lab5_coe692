#!/usr/bin/env bash
set -euo pipefail

if command -v lsof >/dev/null 2>&1; then
  if lsof -iTCP:8080 -sTCP:LISTEN -n -P >/tmp/lab5_8080.txt; then
    echo "Port 8080 is in use:" 
    cat /tmp/lab5_8080.txt
    echo "Stop the process above or change Tomcat connector before local tests."
    exit 1
  fi
  echo "Port 8080 is available."
else
  echo "lsof not installed; skipping explicit 8080 check."
fi
