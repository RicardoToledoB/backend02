# Ajuste SMTP Gmail para notificaciones

Se deja el backend preparado para envio real de correos mediante Gmail SMTP.

## Variables configuradas

- EMAIL_ENABLED=true
- EMAIL_FROM=mensajedssm@gmail.com
- EMAIL_FROM_NAME="Gestion Demanda DSSM"
- SMTP_HOST=smtp.gmail.com
- SMTP_PORT=587
- SMTP_USERNAME=mensajedssm@gmail.com
- SMTP_PASSWORD debe configurarse en `/etc/gestiondemanda-api/email.env`
- SMTP_AUTH=true
- SMTP_STARTTLS_ENABLE=true
- SMTP_STARTTLS_REQUIRED=true
- SMTP_SSL_ENABLE=false
- SMTP_SSL_TRUST=smtp.gmail.com

## Instalacion recomendada en servidor

```bash
cd /var/www/html/gestiondemanda-api
sudo ./scripts/instalar-config-smtp-gmail-systemd.sh
sudo systemctl restart gestiondemanda-api
```

El script solicita la App Password por consola y la guarda en `/etc/gestiondemanda-api/email.env` con permisos `600`.

## Validacion

```bash
sudo systemctl show gestiondemanda-api -p EnvironmentFiles
sudo systemctl cat gestiondemanda-api
```

Luego probar el endpoint de envio de correo ya existente.

## Nota de seguridad

La contrasena real no debe quedar versionada en el proyecto ni dentro de repositorios. Debe mantenerse en variables de entorno o en el archivo protegido `/etc/gestiondemanda-api/email.env`.
