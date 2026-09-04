#!/usr/bin/env bash
set -euo pipefail

ENV_DIR="/etc/gestiondemanda-api"
ENV_FILE="$ENV_DIR/email.env"
OVERRIDE_DIR="/etc/systemd/system/gestiondemanda-api.service.d"
OVERRIDE_FILE="$OVERRIDE_DIR/email.conf"

sudo mkdir -p "$ENV_DIR" "$OVERRIDE_DIR"

if [ -f "$ENV_FILE" ]; then
  echo "$ENV_FILE ya existe. Se creara respaldo antes de actualizarlo."
  sudo cp "$ENV_FILE" "$ENV_FILE.backup_$(date +%Y%m%d_%H%M%S)"
fi

read -r -p "Usuario Gmail SMTP [mensajedssm@gmail.com]: " SMTP_USER
SMTP_USER=${SMTP_USER:-mensajedssm@gmail.com}

read -r -s -p "App Password Gmail para $SMTP_USER: " SMTP_PASS
echo
if [ -z "$SMTP_PASS" ]; then
  echo "ERROR: SMTP_PASSWORD no puede quedar vacio." >&2
  exit 1
fi

sudo tee "$ENV_FILE" >/dev/null <<EOCFG
EMAIL_ENABLED=true
EMAIL_FROM=$SMTP_USER
EMAIL_FROM_NAME="Gestion Demanda DSSM"

SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=$SMTP_USER
SMTP_PASSWORD="$SMTP_PASS"

SMTP_AUTH=true
SMTP_STARTTLS_ENABLE=true
SMTP_STARTTLS_REQUIRED=true
SMTP_SSL_ENABLE=false
SMTP_SSL_TRUST=smtp.gmail.com
SMTP_DEBUG=false

SMTP_CONNECTION_TIMEOUT=10000
SMTP_TIMEOUT=10000
SMTP_WRITE_TIMEOUT=10000
EOCFG

sudo chmod 600 "$ENV_FILE"
sudo chown root:root "$ENV_FILE"

sudo tee "$OVERRIDE_FILE" >/dev/null <<'EOOVERRIDE'
[Service]
EnvironmentFile=-/etc/gestiondemanda-api/email.env
EOOVERRIDE

sudo systemctl daemon-reload

echo "Configuracion SMTP Gmail instalada en $ENV_FILE"
echo "Override systemd instalado en $OVERRIDE_FILE"
echo "Ahora reinicie el servicio: sudo systemctl restart gestiondemanda-api"
