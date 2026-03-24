#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "[1/5] Deteniendo stack Elastic..."
docker compose down

echo "[2/5] Levantando stack Elastic..."
docker compose up -d

echo "[3/5] Esperando que Elasticsearch responda en http://localhost:9200 ..."
for i in {1..60}; do
  if curl -s "http://localhost:9200" >/dev/null; then
    echo "Elasticsearch disponible."
    break
  fi
  sleep 1
  if [[ "$i" -eq 60 ]]; then
    echo "ERROR: Elasticsearch no estuvo listo a tiempo."
    exit 1
  fi
done

echo "[4/5] Esperando recuperacion del cluster (yellow/green)..."
for i in {1..90}; do
  HEALTH_JSON="$(curl -s "http://localhost:9200/_cluster/health" || true)"
  STATUS="$(echo "$HEALTH_JSON" | sed -n 's/.*"status"[[:space:]]*:[[:space:]]*"\([a-z]*\)".*/\1/p' | head -1)"
  if [[ "$STATUS" == "yellow" || "$STATUS" == "green" ]]; then
    echo "Cluster en estado: $STATUS"
    break
  fi
  sleep 1
  if [[ "$i" -eq 90 ]]; then
    echo "ERROR: El cluster no alcanzo estado yellow/green a tiempo."
    echo "Respuesta: $HEALTH_JSON"
    exit 1
  fi
done

echo "[5/5] Validando indice de logs..."
COUNT_JSON="$(curl -s "http://localhost:9200/torneos-logs-*/_count" || true)"
COUNT="$(echo "$COUNT_JSON" | sed -n 's/.*"count"[[:space:]]*:[[:space:]]*\([0-9][0-9]*\).*/\1/p' | head -1)"

if [[ -z "$COUNT" ]]; then
  echo "No se pudo leer el conteo del indice torneos-logs-*."
  echo "Respuesta: $COUNT_JSON"
  exit 1
fi

echo "Documentos en torneos-logs-*: $COUNT"

echo "[6/6] Indices actuales:"
curl -s "http://localhost:9200/_cat/indices?v" | grep -E "torneos-logs|logs\s|health|index" || true

echo "Proceso completado."
