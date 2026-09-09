# Ajuste comunicaciones transversales - Gestión de Demanda

## Objetivo

Permitir configurar destinatarios de comunicaciones automáticas tanto por programa como de forma transversal, sin programa asociado.

## Regla funcional

- `programId = X`: configuración específica para el programa X.
- `programId = null`: configuración transversal, independiente de programa.
- Para una comunicación asociada al programa X, el backend retorna tanto las configuraciones específicas del programa X como las transversales.
- El rol no determina quién recibe correos; los destinatarios se resuelven exclusivamente mediante flags `canReceive...`.
- Las modificaciones de configuración quedan restringidas inicialmente a `ROLE_ADMIN`.

## Endpoints relevantes

### Crear configuración transversal

```http
POST /api/v1/users_programs
```

```json
{
  "userId": 27,
  "programId": null,
  "isActive": true,
  "canReceiveReferences": true,
  "canReceiveCitations": true,
  "canReceiveAttendances": false,
  "canReceiveFeedback": true,
  "canReceiveClosures": true,
  "canReceiveDocuments": false,
  "canReceiveObservations": true,
  "canManageCommunications": false
}
```

También es compatible con la estructura histórica:

```json
{
  "user": { "id": 27 },
  "program": null,
  "isActive": true,
  "canReceiveReferences": true
}
```

### Crear configuración específica de programa

```json
{
  "userId": 27,
  "programId": 2,
  "isActive": true,
  "canReceiveReferences": true
}
```

### Consultar configuración unificada

```http
GET /api/v1/users_programs/communications?programId=2
```

Retorna configuraciones específicas del programa 2 y configuraciones transversales.

### Consultar destinatarios por tipo de comunicación

```http
GET /api/v1/users_programs/communications/recipients?programId=2&type=references
```

Tipos soportados:

- `references` / `referencias`
- `citations` / `citaciones`
- `attendances` / `asistencias`
- `feedback` / `retroalimentacion`
- `closures` / `cierres`
- `documents` / `documentos`
- `observations` / `observaciones`
- `manage_communications` / `administrar_comunicaciones`

## SQL

Ejecutar:

```bash
sudo mysql -u root -p demanda_drogas < sql/2026_09_08_users_programs_transversal_communications.sql
```
