# Ajustes backend 2026-07-10 — Gestión Demanda

## 1. Hora del servidor

Se habilita/actualiza:

```http
GET /api/v1/time/server
```

Respuesta:

```json
{
  "epochMillis": 1783361644000,
  "dateTime": "2026-07-06T18:14:04-03:00",
  "timezone": "America/Punta_Arenas"
}
```

Regla frontend:
1. Usar `epochMillis` como fuente principal.
2. Usar `dateTime` ISO con offset como respaldo legible.
3. Usar `timezone` para mostrar zona horaria del servidor.

## 2. Convenios previsionales

Se mantiene habilitado:

```http
/api/v1/conv_prevs/**
```

Rutas disponibles:

```http
GET    /api/v1/conv_prevs/all
GET    /api/v1/conv_prevs/getAllPaginated
GET    /api/v1/conv_prevs/deleted
POST   /api/v1/conv_prevs
PUT    /api/v1/conv_prevs/{id}
DELETE /api/v1/conv_prevs/{id}
PATCH  /api/v1/conv_prevs/restore/{id}
```

El mantenedor permite asociar convenio previsional a `intPrevId`.

## 3. Búsqueda de postulantes por RUN

Se mantiene habilitado:

```http
GET /api/v1/postulants/searchByRut?rut=11.799.136-9&page=0&size=1
GET /api/v1/postulants/searchByRut?rut=117991369&page=0&size=1
```

El backend acepta RUN formateado y sin formato.

## 4. Episodio activo por RUN

Se mantiene habilitado:

```http
GET /api/v1/demand/episodes/active/by-rut/{rut}
```

Uso funcional: validar que una persona tenga como máximo un episodio activo.

## 5. Facultativos por programa

Se mantiene el mantenedor:

```http
/api/v1/program_professionals/**
```

Rutas principales:

```http
GET    /api/v1/program_professionals
GET    /api/v1/program_professionals/all
GET    /api/v1/program_professionals/deleted
GET    /api/v1/program_professionals/{id}
POST   /api/v1/program_professionals
PUT    /api/v1/program_professionals/{id}
DELETE /api/v1/program_professionals/{id}
POST   /api/v1/program_professionals/{id}/restore
GET    /api/v1/program_professionals/program/{programId}
```

Request esperado:

```json
{
  "name": "Dra. María González",
  "professionId": 3,
  "email": "maria.gonzalez@redsalud.gob.cl",
  "phone": "+56 9 1234 5678",
  "observation": "Atiende citaciones de ingreso y evaluación.",
  "programIds": [1, 2, 5]
}
```

Ajustes aplicados:

- `PUT /api/v1/program_professionals/{id}` queda habilitado para ADMIN, ADMINISTRATIVO y SUPERVISOR.
- El restore ahora reactiva también las relaciones históricas del facultativo con sus programas en `program_professional_programs`.
- Se habilita `OPTIONS /**` para preflight CORS en operaciones PUT/PATCH/DELETE desde Angular.

## 6. Relación asistencia ↔ citación

Se agrega campo nuevo al modelo `episode_events`:

```sql
related_event_id
```

Uso esperado:

```text
CITACION id 6
ASISTENCIA id 11
ASISTENCIA.related_event_id = 6
ASISTENCIA.attendance_status_id = NO_SE_PRESENTO / SE_PRESENTO / etc.
```

Request esperado para asistencia:

```json
{
  "stageId": 1,
  "relatedEventId": 6,
  "eventDate": "2026-07-10",
  "eventTime": "10:30:00",
  "attendanceStatusCode": "NO_SE_PRESENTO",
  "comment": "No se presenta a citación"
}
```

Respuesta `EpisodeEventDTO` ahora incluye:

```json
{
  "id": 11,
  "episodeId": 1,
  "stageId": 1,
  "relatedEventId": 6,
  "eventType": { "code": "ASISTENCIA" },
  "attendanceStatus": { "code": "NO_SE_PRESENTO" }
}
```

Además, si una asistencia se registra vinculada a una citación, el backend actualiza el estado de asistencia de la citación relacionada para facilitar visualización en frontend.

## 7. Seguridad

Endpoints habilitados para roles:

```text
ROLE_ADMIN
ROLE_ADMINISTRATIVO
ROLE_SUPERVISOR
```

En búsquedas operativas de demanda también se permite:

```text
ROLE_PROFESIONAL
```

