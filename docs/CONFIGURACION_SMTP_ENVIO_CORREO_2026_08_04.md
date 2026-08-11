# Configuración SMTP para envío real de correos

El endpoint de correo ya no queda como respuesta fija. Si `EMAIL_ENABLED=true` y `SMTP_HOST` está configurado, el backend intentará enviar correos mediante `JavaMailSender`.

## Variables de entorno recomendadas

```bash
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
```

Mientras `EMAIL_ENABLED=false` o `SMTP_HOST` esté vacío, el endpoint seguirá respondiendo `EMAIL_SERVICE_NOT_CONFIGURED`.

## Endpoint de prueba

```http
POST /api/v1/demand/episodes/{episodeId}/send-email
```

```json
{
  "to": "destinatario@redsalud.gob.cl",
  "subject": "Prueba correo episodio RDA-SM",
  "message": "Prueba de notificación asociada al episodio."
}
```

Respuesta esperada con SMTP configurado correctamente:

```json
{
  "sent": true,
  "queued": false,
  "result": "EMAIL_SENT",
  "message": "Correo enviado correctamente."
}
```
