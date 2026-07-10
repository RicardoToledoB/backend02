# Ajuste backend 2026-07-10 — Facultativos y eventos de demanda

## 1. PUT de facultativos / programas asociados

Se corrige la sincronización de `programIds` en:

```http
PUT /api/v1/program_professionals/{id}
```

Regla aplicada:

1. Mantiene activas las relaciones que vienen en `programIds`.
2. Crea relaciones nuevas si no existen.
3. Reactiva relaciones existentes si estaban eliminadas lógicamente.
4. Marca con `deleted_at` las relaciones que ya no vienen en `programIds`.
5. Las relaciones removidas quedan disponibles en:

```http
GET /api/v1/program_professionals/{id}/program-relations/deleted
```

La eliminación de relaciones omitidas se ejecuta ahora con SQL nativo sobre `program_professional_programs`, para evitar problemas con filtros lógicos de Hibernate.

## 2. Restore de facultativos

El restore mantiene la regla ya solicitada:

```http
PUT  /api/v1/program_professionals/restore/{id}
POST /api/v1/program_professionals/{id}/restore
PATCH /api/v1/program_professionals/restore/{id}
```

Al restaurar un facultativo también se restauran sus relaciones históricas en `program_professional_programs`.

## 3. Aclaración de IDs profesionales en eventos

Se deja formalizada la diferencia:

- `professionalUserId`: corresponde a `users.id`.
- `programProfessionalId`: corresponde a `program_professionals.id`.

El mantenedor de facultativos entrega `program_professionals.id`, por lo tanto el frontend debe enviar ese valor en `programProfessionalId`, no en `professionalUserId`.

Ejemplo para crear citación/evento con facultativo del mantenedor:

```json
{
  "eventTypeCode": "CITACION",
  "stageId": 1,
  "programProfessionalId": 5,
  "eventDate": "2026-07-10",
  "eventTime": "10:30:00",
  "attendanceStatusCode": "AGENDADO",
  "citationComment": "Primera citación"
}
```

Ejemplo para asistencia asociada a citación:

```json
{
  "stageId": 1,
  "relatedEventId": 6,
  "programProfessionalId": 5,
  "eventDate": "2026-07-10",
  "eventTime": "10:30:00",
  "attendanceStatusCode": "NO_SE_PRESENTO",
  "comment": "No se presenta a citación"
}
```

## 4. Modelo de eventos

Se agrega en `episode_events`:

```sql
program_professional_id
```

Además de `related_event_id`, que ya vincula la asistencia con la citación original.

La respuesta `EpisodeEventDTO` ahora devuelve:

```json
{
  "professionalUser": null,
  "programProfessionalId": 5,
  "programProfessionalName": "Andres Vakencia"
}
```

## 5. Regla oficial para frontend

- Si se usa usuario funcionario del sistema: enviar `professionalUserId`.
- Si se usa facultativo desde el mantenedor: enviar `programProfessionalId`.
- No enviar `program_professionals.id` en `professionalUserId`.
