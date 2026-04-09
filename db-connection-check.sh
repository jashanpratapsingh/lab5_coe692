#!/usr/bin/env bash
set -euo pipefail

NAMESPACE="${NAMESPACE:-default}"
MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-lab5_root}"

records=(
  "auth-service-db|Auth_Rental_Lab5_DB|users"
  "catalog-service-db|Catalog_Rental_Lab5_DB|equipment"
  "inventory-service-db|Inventory_Rental_Lab5_DB|inventory_item"
  "reservation-service-db|Reservation_Rental_Lab5_DB|reservation"
  "checkout-penalty-service-db|CheckoutPenalty_Rental_Lab5_DB|rental_transaction"
  "frontend-service-db|Frontend_Rental_Lab5_DB|ui_audit"
)

usage() {
  cat <<'EOF'
Usage:
  ./db-connection-check.sh
  ./db-connection-check.sh --namespace <k8s-namespace>
  ./db-connection-check.sh --open <deployment-name>
  ./db-connection-check.sh --values [--limit <n>]
  ./db-connection-check.sh --table-values <deployment-name> <table-name> [--limit <n>]

Examples:
  ./db-connection-check.sh
  ./db-connection-check.sh --open auth-service-db
  ./db-connection-check.sh --values --limit 5
  ./db-connection-check.sh --table-values reservation-service-db reservation_item --limit 10
  NAMESPACE=default MYSQL_ROOT_PASSWORD=lab5_root ./db-connection-check.sh
EOF
}

open_database() {
  local target="$1"
  local found=0
  for rec in "${records[@]}"; do
    IFS='|' read -r deploy db _ <<<"$rec"
    if [[ "$deploy" == "$target" ]]; then
      found=1
      echo "Opening MySQL shell in deployment/$deploy (database: $db, namespace: $NAMESPACE)"
      kubectl -n "$NAMESPACE" exec -it "deploy/$deploy" -- mysql -uroot "-p${MYSQL_ROOT_PASSWORD}" "$db"
      break
    fi
  done

  if [[ "$found" -eq 0 ]]; then
    echo "Unknown deployment: $target"
    echo "Valid values:"
    for rec in "${records[@]}"; do
      IFS='|' read -r deploy _ _ <<<"$rec"
      echo "  - $deploy"
    done
    exit 1
  fi
}

check_connection() {
  local deploy="$1"
  local db="$2"
  local table="$3"

  echo "------------------------------------------------------------"
  echo "Checking deployment/$deploy -> $db"

  if ! kubectl -n "$NAMESPACE" get deploy "$deploy" >/dev/null 2>&1; then
    echo "FAIL: deployment/$deploy not found in namespace $NAMESPACE"
    return 1
  fi

  if ! kubectl -n "$NAMESPACE" exec "deploy/$deploy" -- sh -c \
    "mysql -uroot -p'${MYSQL_ROOT_PASSWORD}' -e \"SELECT 1;\" >/dev/null"; then
    echo "FAIL: cannot connect to MySQL inside deployment/$deploy"
    return 1
  fi

  local query_output
  if ! query_output="$(kubectl -n "$NAMESPACE" exec "deploy/$deploy" -- sh -c \
    "mysql -uroot -p'${MYSQL_ROOT_PASSWORD}' -e \"USE ${db}; SELECT '${db}' AS database_name; SHOW TABLES;\" 2>/dev/null")"; then
    echo "FAIL: database $db is not reachable"
    return 1
  fi

  echo "PASS: MySQL connection and database access OK"
  echo "$query_output"

  if kubectl -n "$NAMESPACE" exec "deploy/$deploy" -- sh -c \
    "mysql -uroot -p'${MYSQL_ROOT_PASSWORD}' -e \"USE ${db}; SELECT COUNT(*) AS row_count FROM ${table};\" >/dev/null" 2>/dev/null; then
    local count_output
    count_output="$(kubectl -n "$NAMESPACE" exec "deploy/$deploy" -- sh -c \
      "mysql -uroot -p'${MYSQL_ROOT_PASSWORD}' -e \"USE ${db}; SELECT COUNT(*) AS row_count FROM ${table};\" 2>/dev/null")"
    echo "$count_output"
  else
    echo "INFO: sample table '${table}' not found in $db (connection still validated)"
  fi
}

database_for_deployment() {
  local target="$1"
  for rec in "${records[@]}"; do
    IFS='|' read -r deploy db _ <<<"$rec"
    if [[ "$deploy" == "$target" ]]; then
      echo "$db"
      return 0
    fi
  done
  return 1
}

preview_values() {
  local deploy="$1"
  local db="$2"
  local table="$3"
  local limit="$4"

  echo "Sample rows from ${db}.${table} (limit ${limit}):"
  if ! kubectl -n "$NAMESPACE" exec "deploy/$deploy" -- sh -c \
    "mysql -uroot -p'${MYSQL_ROOT_PASSWORD}' -e \"USE ${db}; SELECT * FROM ${table} LIMIT ${limit};\" 2>/dev/null"; then
    echo "INFO: unable to preview values for ${db}.${table}"
  fi
}

OPEN_TARGET=""
SHOW_VALUES=0
LIMIT=3
TABLE_DEPLOY=""
TABLE_NAME=""
while [[ $# -gt 0 ]]; do
  case "$1" in
    --namespace)
      NAMESPACE="$2"
      shift 2
      ;;
    --open)
      OPEN_TARGET="$2"
      shift 2
      ;;
    --values)
      SHOW_VALUES=1
      shift
      ;;
    --limit)
      LIMIT="$2"
      shift 2
      ;;
    --table-values)
      TABLE_DEPLOY="$2"
      TABLE_NAME="$3"
      shift 3
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown option: $1"
      usage
      exit 1
      ;;
  esac
done

if [[ -n "$OPEN_TARGET" ]]; then
  open_database "$OPEN_TARGET"
  exit 0
fi

if [[ -n "$TABLE_DEPLOY" || -n "$TABLE_NAME" ]]; then
  if [[ -z "$TABLE_DEPLOY" || -z "$TABLE_NAME" ]]; then
    echo "--table-values requires both <deployment-name> and <table-name>"
    exit 1
  fi
  if ! db_name="$(database_for_deployment "$TABLE_DEPLOY")"; then
    echo "Unknown deployment: $TABLE_DEPLOY"
    exit 1
  fi
  preview_values "$TABLE_DEPLOY" "$db_name" "$TABLE_NAME" "$LIMIT"
  exit 0
fi

echo "Namespace: $NAMESPACE"
echo "Testing all database deployments..."

failures=0
for rec in "${records[@]}"; do
  IFS='|' read -r deploy db table <<<"$rec"
  if ! check_connection "$deploy" "$db" "$table"; then
    failures=$((failures + 1))
  elif [[ "$SHOW_VALUES" -eq 1 ]]; then
    preview_values "$deploy" "$db" "$table" "$LIMIT"
  fi
done

echo "------------------------------------------------------------"
if [[ "$failures" -eq 0 ]]; then
  echo "All database connectivity checks passed."
else
  echo "$failures database check(s) failed."
  exit 1
fi
