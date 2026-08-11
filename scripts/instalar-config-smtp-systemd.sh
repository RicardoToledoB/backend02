#!/usr/bin/env bash
set -euo pipefail

BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ENV_DIR="/etc/gestiondemanda-api"
ENV_FILE="$ENV_DIR/email.env"
OVERRIDE_DIR="/etc/systemd/system/gestiondemanda-api.service.d"
OVERRIDE_FILE="$OVERRIDE_DIR/email.conf"

sudo mkdir -p "$ENV_DIR" "$OVERRIDE_DIR"

if [ ! -f "$ENV_FILE" ]; then
  sudo cp "$BASE_DIR/deploy/systemd/gestiondemanda-api-email.env.example" "$ENV_FILE"
  echo "Creado $ENV_FILE. Edite ese archivo con los datos SMTP reales."
else
  echo "$ENV_FILE ya existe. No se sobrescribio."
fi

sudo cp "$BASE_DIR/deploy/systemd/gestiondemanda-api-email.override.conf.example" "$OVERRIDE_FILE"
sudo systemctl daemon-reload

echo "Override instalado en $OVERRIDE_FILE"
echo "Siguiente paso:"
echo "  sudo nano $ENV_FILE"
echo "  sudo systemctl restart gestiondemanda-api"
