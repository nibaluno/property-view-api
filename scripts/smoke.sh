#!/usr/bin/env bash
set -euo pipefail

BASE="${BASE_URL:-http://localhost:8092/property-view}"
JSON='Content-Type: application/json'
RUN="$(date +%s)"
BRAND="SmokeBrand${RUN}"
CITY="SmokeCity${RUN}"

command -v jq >/dev/null || { echo "jq is required"; exit 2; }

fail()   { echo "SMOKE FAILED: $*" >&2; exit 1; }
expect() { [ "$2" = "$3" ] || fail "$1: expected '$2' but got '$3'"; }
status() { curl -s -o /dev/null -w '%{http_code}' "$@"; }

expect "GET /hotels" 200 "$(status "$BASE/hotels")"

CREATED="$(curl -s -w '\n%{http_code}' -X POST "$BASE/hotels" -H "$JSON" -d "{
  \"name\": \"Smoke Hotel ${RUN}\", \"brand\": \"${BRAND}\",
  \"address\": {\"houseNumber\": 1, \"street\": \"Main\", \"city\": \"${CITY}\", \"country\": \"Belarus\", \"postCode\": \"220000\"},
  \"contacts\": {\"phone\": \"+375 17 000-00-00\", \"email\": \"smoke@example.com\"},
  \"arrivalTime\": {\"checkIn\": \"14:00\", \"checkOut\": \"12:00\"}}")"
expect "POST /hotels status" 201 "$(echo "$CREATED" | tail -n1)"
ID="$(echo "$CREATED" | head -n1 | jq -r .id)"
[[ "$ID" =~ ^[0-9]+$ ]] || fail "POST /hotels returned a non-numeric id: $ID"

expect "GET /hotels/{id} name" "Smoke Hotel ${RUN}" "$(curl -s "$BASE/hotels/$ID" | jq -r .name)"
expect "GET /hotels/{id} checkIn" "14:00" "$(curl -s "$BASE/hotels/$ID" | jq -r .arrivalTime.checkIn)"

AMEN="$(curl -s -X POST "$BASE/hotels/$ID/amenities" -H "$JSON" -d "[\"Smoke WiFi ${RUN}\", \"Smoke Pool ${RUN}\", \"smoke wifi ${RUN}\"]")"
expect "POST amenities (dedup)" 2 "$(echo "$AMEN" | jq '.amenities | length')"

LOWER_CITY="$(echo "$CITY" | tr '[:upper:]' '[:lower:]')"
expect "GET /search?city= (case-insensitive)" 1 "$(curl -s "$BASE/search?city=${LOWER_CITY}" | jq length)"
expect "GET /search?brand=" 1 "$(curl -s "$BASE/search?brand=${BRAND}" | jq length)"
expect "GET /histogram/city" 1 "$(curl -s "$BASE/histogram/city" | jq -r --arg k "$CITY" '.[$k]')"
expect "GET /histogram/brand" 1 "$(curl -s "$BASE/histogram/brand" | jq -r --arg k "$BRAND" '.[$k]')"
expect "GET /histogram/amenities" 1 "$(curl -s "$BASE/histogram/amenities" | jq -r --arg k "Smoke Pool ${RUN}" '.[$k]')"

expect "unknown hotel gives 404" 404 "$(status "$BASE/hotels/999999999")"
expect "unknown histogram param gives 400" 400 "$(status "$BASE/histogram/stars")"
expect "OpenAPI docs" 200 "$(status "$BASE/v3/api-docs")"
expect "Swagger UI" 200 "$(status -L "$BASE/swagger-ui.html")"

echo "SMOKE OK (hotel id ${ID})"
