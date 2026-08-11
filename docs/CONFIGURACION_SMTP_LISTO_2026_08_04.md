# Configuracion SMTP lista - Gestion Demanda

El backend ya tiene implementado el envio SMTP real para:

- `POST /api/v1/demand/episodes/{episodeId}/send-email`
- `POST /api/v1/demand/documents/{documentId}/send-email`
- `POST /api/v1/demand/notifications/email`

El resultado `EMAIL_SERVICE_NOT_CONFIGURED` aparece solo cuando el servicio no tiene variables SMTP reales.

## Instalacion rapida en servidor

```bash
cd /var/www/html/gestiondemanda-api
sudo ./scripts/instalar-config-smtp-systemd.sh
sudo nano /etc/gestiondemanda-api/email.env
sudo systemctl restart gestiondemanda-api
```

Completar en `/etc/gestiondemanda-api/email.env`:

```ini
EMAIL_ENABLED=true
EMAIL_FROM=no-reply@dssm.cl
EMAIL_FROM_NAME=Gestion Demanda DSSM
SMTP_HOST=smtp.institucional.cl
SMTP_PORT=587
SMTP_USERNAME=usuario_smtp
SMTP_PASSWORD=clave_smtp
SMTP_AUTH=true
SMTP_STARTTLS_ENABLE=true
SMTP_STARTTLS_REQUIRED=false
SMTP_SSL_ENABLE=false
```

Si el SMTP no requiere autenticacion, usar:

```ini
SMTP_AUTH=false
SMTP_USERNAME=
SMTP_PASSWORD=
```

## Prueba

```bash
curl -i -X POST https://gestiondemanda-api.dssm.cl/api/v1/demand/episodes/1/send-email \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "to": "patricio.jara@redsalud.gob.cl",
    "subject": "Prueba correo episodio RDA-SM",
    "message": "Prueba de notificacion asociada al episodio 1."
  }'
```

Respuesta esperada con SMTP operativo:

```json
{
  "sent": true,
  "queued": false,
  "result": "EMAIL_SENT",
  "message": "Correo enviado correctamente."
}
```
