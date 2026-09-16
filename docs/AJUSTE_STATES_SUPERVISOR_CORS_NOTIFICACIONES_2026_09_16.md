# Ajuste states SUPERVISOR + CORS notificaciones

Fecha: 2026-09-16

## Objetivo

Corregir dos problemas reportados desde frontend:

1. `GET /api/v1/states` respondía 403 para perfil `SUPERVISOR`.
2. `POST /api/v1/demand/notifications/email` podía responder 403 desde Angular en localhost por validación de seguridad/CORS.

## Cambios

### States

Se elimina la restricción de clase `@PreAuthorize(ADMIN, ADMINISTRATIVO)` y se separa:

- Lectura `GET`: `ADMIN`, `ADMINISTRATIVO`, `SUPERVISOR`.
- Escritura `POST`, `PUT`, `DELETE`, `restore`: `ADMIN`, `ADMINISTRATIVO`.

### SecurityConfig

Se agrega permiso explícito para:

- `POST /api/v1/demand/notifications/email` como `permitAll()`.
- `GET /api/v1/states` y `/api/v1/states/**` para `ROLE_SUPERVISOR`.

### CORS

Se amplían orígenes de desarrollo:

- `http://localhost:*`
- `https://localhost:*`
- `http://127.0.0.1:*`
- `https://127.0.0.1:*`
- `http://[::1]:*`

Se permiten headers `*` para evitar rechazos de preflight por headers agregados desde Angular.

## Validación sugerida

```bash
curl -i http://localhost:8095/api/v1/states \
  -H "Authorization: Bearer $TOKEN_SUPERVISOR"

curl -i -X OPTIONS http://localhost:8095/api/v1/demand/notifications/email \
  -H "Origin: http://localhost:4200" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: content-type"

curl -i -X POST http://localhost:8095/api/v1/demand/notifications/email \
  -H "Origin: http://localhost:4200" \
  -H "Content-Type: application/json" \
  -d '{"to":"correo@dominio.cl","subject":"Prueba","message":"Prueba recuperación"}'
```

No requiere SQL.
