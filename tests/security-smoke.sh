#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
AUTH_USERNAME="${AUTH_USERNAME:-student1}"
AUTH_PASSWORD="${AUTH_PASSWORD:-password123}"

echo "Fetching JWT token and session marker from auth login"
login_json=$(curl -s -X POST "$BASE_URL/auth-service/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$AUTH_USERNAME\",\"password\":\"$AUTH_PASSWORD\"}")

read -r token marker <<<"$(python3 - "$login_json" <<'PY'
import json, sys
payload = json.loads(sys.argv[1])
print((payload.get("token") or "") + " " + (payload.get("sessionMarker") or ""))
PY
)"

if [[ -z "$token" || -z "$marker" ]]; then
  echo "Failed to obtain token/session marker: $login_json"
  exit 1
fi

echo "Checking that marker is required (expect 403 without marker)"
marker_missing_code=$(curl -s -o /dev/null -w "%{http_code}" \
  -H "Authorization: Bearer $token" \
  "$BASE_URL/catalog-service/api/health")
echo "catalog-service /api/health with token-only -> HTTP $marker_missing_code"
if [[ "$marker_missing_code" != "403" ]]; then
  echo "Expected 403 when session marker is missing"
  exit 1
fi

echo "Checking authenticated health endpoints with token + marker (expect 200)"
for endpoint in \
  "$BASE_URL/auth-service/api/health" \
  "$BASE_URL/catalog-service/api/health" \
  "$BASE_URL/inventory-service/api/health" \
  "$BASE_URL/reservation-service/api/health" \
  "$BASE_URL/checkout-penalty-service/api/health"; do
  code=$(curl -s -o /dev/null -w "%{http_code}" \
    -H "Authorization: Bearer $token" \
    -H "X-LAB5-SESSION-MARKER: $marker" \
    "$endpoint")
  echo "$endpoint -> HTTP $code"
  if [[ "$code" != "200" ]]; then
    echo "Authenticated request failed for $endpoint"
    exit 1
  fi
done

echo "Security smoke checks passed."
