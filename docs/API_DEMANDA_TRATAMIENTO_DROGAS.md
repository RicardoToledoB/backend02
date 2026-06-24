# Backend Gestión de Demanda de Tratamiento de Drogas

Este backend incorpora el rediseño del flujo funcional solicitado: persona, episodio, etapa por programa, eventos trazables, referencias entre programas, ingreso a tratamiento, egreso/cierre, alertas, documentos, sustancias y auditoría/reversión.

## Modelo nuevo implementado

Tablas principales nuevas:

- `episodes`: nueva demanda de una persona. Permite solo un episodio activo por persona.
- `episode_stages`: etapas por programa dentro del episodio.
- `episode_events`: citaciones, asistencias, entrevistas, observaciones, retroalimentaciones, referencias, ingresos, egresos, cierres, rectificaciones y reversiones.
- `episode_references`: referencias entre programas; cierra etapa origen y crea etapa destino sin reiniciar los días.
- `episode_documents`: metadatos de documentos asociados a episodio, etapa, evento o referencia.
- `episode_alerts`: alertas con responsable, prioridad, acción y próxima gestión.
- `episode_audit_logs`: auditoría de acciones críticas y reversiones.
- `episode_substances`: sustancias asociadas al episodio.

Catálogos nuevos:

- `episode_types`
- `event_types`
- `attendance_statuses`
- `closure_reasons`
- `program_populations`
- `program_modalities`
- `program_plans`
- `semaphore_rules`
- `regions`
- `cities`

La tabla `programs` fue ampliada con región, ciudad, población, modalidad, plan, dirección, teléfono, correo, descripción y activo.

## Endpoints principales

Base URL:

```text
/api/v1/demand
```

### Catálogos

```http
GET /api/v1/demand/catalogs
```

### Persona por RUT

```http
GET /api/v1/demand/persons/rut/{rut}
```

### Crear episodio

```http
POST /api/v1/demand/episodes
```

Ejemplo:

```json
{
  "postulantId": 1,
  "episodeTypeCode": "PRIMERA_SOLICITUD",
  "originalRequestDate": "2026-06-23",
  "initialProgramId": 1,
  "contactTypeId": 1,
  "senderId": 1,
  "diverterId": 1,
  "initialObservation": "Primera demanda registrada desde admisión."
}
```

Regla: si la persona ya tiene episodio activo, no crea otro; retorna el episodio activo existente.

### Buscar episodio activo por RUT

```http
GET /api/v1/demand/episodes/active/by-rut/{rut}
```

### Ficha longitudinal

```http
GET /api/v1/demand/episodes/by-rut/{rut}/longitudinal
GET /api/v1/demand/episodes/{id}/longitudinal
```

Devuelve persona, episodio activo, episodios históricos, etapas, eventos, referencias, alertas, documentos y auditoría.

### Bandeja priorizada

```http
GET /api/v1/demand/episodes/prioritized?page=0&size=20
GET /api/v1/demand/episodes/prioritized?programId=1&resultCode=LISTA_ESPERA
```

Ordena por fecha original de solicitud y calcula días acumulados y semáforo.

### Dashboard supervisor

```http
GET /api/v1/demand/dashboard/supervisor
```

Requiere rol `ADMIN` o `SUPERVISOR`.

## Eventos operativos

### Registrar citación

```http
POST /api/v1/demand/episodes/{id}/citations
```

```json
{
  "citationDate": "2026-06-25",
  "citationTime": "10:30:00",
  "professionalUserId": 2,
  "professionName": "Psicóloga",
  "citationComment": "Primera citación de evaluación."
}
```

### Registrar asistencia / inasistencia

```http
POST /api/v1/demand/episodes/{id}/attendance
```

```json
{
  "attendanceStatusCode": "NO_SE_PRESENTO",
  "professionalUserId": 2,
  "professionName": "Psicóloga",
  "comment": "No se presenta a la citación."
}
```

Regla: dos inasistencias con el mismo profesional cierran automáticamente el episodio como `CIERRE_POR_INASISTENCIAS`.

### Registrar evento genérico

```http
POST /api/v1/demand/episodes/{id}/events
```

```json
{
  "eventTypeCode": "OBSERVACION",
  "comment": "Observación general del caso.",
  "observation": "Texto clínico/operativo visible en la ficha longitudinal."
}
```

## Acciones críticas con confirmación

Todas estas operaciones exigen `confirmImpact=true`.

### Referir a otro programa

```http
POST /api/v1/demand/episodes/{id}/references
```

```json
{
  "destinationProgramId": 2,
  "reason": "Requiere evaluación en otro dispositivo.",
  "observation": "Se deriva manteniendo fecha original.",
  "confirmImpact": true
}
```

Regla: cierra la etapa de origen, crea etapa receptora, mantiene el mismo episodio y no reinicia días acumulados.

### Ingreso a tratamiento

```http
POST /api/v1/demand/episodes/{id}/treatment-entry
```

```json
{
  "entryToTreatmentAt": "2026-06-30T09:00:00",
  "comment": "Ingreso efectivo a tratamiento.",
  "confirmImpact": true
}
```

Regla: detiene el KPI de espera.

### Egreso

```http
POST /api/v1/demand/episodes/{id}/egress
```

```json
{
  "egressAt": "2026-07-30T15:00:00",
  "comment": "Egreso del proceso de tratamiento.",
  "confirmImpact": true
}
```

### Cierre formal

```http
POST /api/v1/demand/episodes/{id}/close
```

```json
{
  "closureReasonCode": "NO_ES_PERFIL",
  "closureComment": "No cumple perfil de ingreso al programa.",
  "confirmImpact": true
}
```

Si la causal contiene `OTRO`, el comentario es obligatorio.

### Reversión por superior

```http
POST /api/v1/demand/episodes/{id}/reverse
```

```json
{
  "reason": "Cierre precipitado; se revierte por instrucción de supervisión."
}
```

Requiere rol `ADMIN` o `SUPERVISOR`.

## Alertas, documentos y sustancias

```http
POST /api/v1/demand/episodes/{id}/alerts
POST /api/v1/demand/episodes/{id}/documents
POST /api/v1/demand/episodes/{id}/substances
```

Los documentos guardan metadatos; la carga física del archivo puede conectarse después a almacenamiento local, S3, MinIO o repositorio institucional.

## Roles contemplados

- `ADMIN`
- `SUPERVISOR`
- `PROFESIONAL`
- `ADMINISTRATIVO`

## Nota técnica

Se corrigieron además errores de queries con parámetros mal nombrados en `RegisterRepository`, `PostulantRepository` y `UserRepository`, se agregó `@Modifying` faltante en `UserRoleRepository`, y se evita devolver password en varios mapeos DTO existentes.
