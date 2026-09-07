# Ajuste comunicaciones por usuario/programa - 2026-09-07

Se extiende la relación `users_programs` para permitir parametrizar desde Directorio y comunicaciones qué funcionarios reciben notificaciones automáticas por tipo de acción operativa.

## Campos agregados

- `canManageCommunications`
- `canReceiveCitations`
- `canReceiveAttendances`
- `canReceiveFeedback`
- `canReceiveClosures`
- `canReceiveDocuments`
- `canReceiveObservations`

`canReceiveReferences` ya existía y se mantiene sin cambios.

## APIs impactadas

- `GET /api/v1/users_programs`
- `GET /api/v1/users_programs/all`
- `GET /api/v1/users_programs/{id}`
- `GET /api/v1/users_programs/user/{id}`
- `POST /api/v1/users_programs`
- `PUT /api/v1/users_programs/{id}`
- `POST /auth/login` / sesión: se incluyen los nuevos flags en `programs` y en los claims del token.

## Base de datos

Ejecutar:

```bash
sudo mysql -u root -p demanda_drogas < sql/2026_09_07_users_programs_communication_flags.sql
```

El script es idempotente: agrega columnas solo si no existen y normaliza valores nulos a `0`.
