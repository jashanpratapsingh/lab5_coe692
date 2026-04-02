#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"

echo "Checking unauthenticated backend health protection on $BASE_URL"
for endpoint in   "$BASE_URL/auth-service/api/health"   "$BASE_URL/catalog-service/api/health"   "$BASE_URL/inventory-service/api/health"   "$BASE_URL/reservation-service/api/health"   "$BASE_URL/checkout-penalty-service/api/health"; do
  code=$(curl -s -o /dev/null -w "%{http_code}" "$endpoint" || true)
  echo "$endpoint -> HTTP $code"
  if [[ "$code" != "401" ]]; then
    echo "Expected HTTP 401 for protected health endpoint: $endpoint"
    exit 1
  fi
done

echo "Checking frontend entry point"
front_code=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/frontend-service/" || true)
echo "$BASE_URL/frontend-service/ -> HTTP $front_code"
if [[ "$front_code" != "302" && "$front_code" != "200" ]]; then
  echo "Frontend entry point is not reachable"
  exit 1
fi

echo "Local smoke checks passed."
