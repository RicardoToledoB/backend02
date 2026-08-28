# Ajuste backend - Corrección administrativa de episodios

Fecha: 2026-08-28

## Endpoint nuevo

```http
PUT /api/v1/demand/episodes/{episodeId}/administrative-correction
```

Endpoint administrativo restringido a `ROLE_ADMIN`. Permite corregir en una sola transacción información asociada a un episodio y a una etapa/programa concreto.

## Objetivo funcional

El mantenedor frontend puede editar antecedentes históricos o corregir errores de registro sin usar la reversión completa ni el purge del episodio.

La reversión operativa sigue en:

```http
POST /api/v1/demand/episodes/{episodeId}/reverse
```

La eliminación completa de pruebas sigue en:

```http
DELETE /api/v1/demand/episodes/{episodeId}/purge
```

## Request mínimo

```json
{
  "programId": 2,
  "stageId": 11,
  "correctionReason": "Corrección administrativa solicitada por supervisión."
}
```

`stageId` tiene prioridad sobre `programId`. Si no se informa `stageId` y sí se informa `programId`, el backend usa la última etapa de ese programa dentro del episodio.

## Corrección de datos generales

```json
{
  "correctionReason": "Corrección de fecha y número de tratamientos previos.",
  "episode": {
    "previousTreatmentNumber": 2,
    "originalRequestDate": "2026-08-01",
    "contactTypeId": 1,
    "senderId": 1,
    "diverterId": 1
  }
}
```

Solo se actualizan los campos enviados. Los campos no enviados se conservan.

## Corrección de sustancias

```json
{
  "stageId": 11,
  "correctionReason": "Corrección de sustancias informadas.",
  "substances": [
    {
      "action": "UPDATE",
      "id": 5,
      "substanceId": 3,
      "primarySubstance": true,
      "useOrder": 1,
      "observation": "Se corrige sustancia principal."
    },
    {
      "action": "CREATE",
      "substanceId": 7,
      "primarySubstance": false,
      "useOrder": 2
    },
    {
      "action": "DELETE",
      "id": 8
    }
  ]
}
```

Reglas:

- Evita duplicar la misma sustancia dentro del episodio.
- Mantiene solo una sustancia principal.
- Si se elimina la principal, promueve la primera restante según `useOrder`.
- La eliminación es lógica por `@SQLDelete`.

## Corrección de eventos

Se soportan listas específicas y una lista genérica:

- `citations`
- `attendances`
- `feedbacks`
- `observations`
- `events`

Ejemplo:

```json
{
  "stageId": 11,
  "correctionReason": "Corrección de citación y asistencia.",
  "citations": [
    {
      "action": "UPDATE",
      "id": 20,
      "citationTypeCode": "PRIMERA_CITACION_SEGUNDA_ENTREVISTA",
      "eventDate": "2026-08-28",
      "eventTime": "10:30:00",
      "citationComment": "Fecha corregida."
    }
  ],
  "attendances": [
    {
      "action": "CREATE",
      "relatedEventId": 20,
      "eventDate": "2026-08-28",
      "eventTime": "10:30:00",
      "attendanceStatusCode": "SE_PRESENTO",
      "comment": "Asistencia corregida administrativamente."
    }
  ]
}
```

Reglas:

- `CREATE`: crea un nuevo evento.
- `UPDATE`: actualiza el evento existente.
- `DELETE`: anula/elimina lógicamente el evento existente.
- En asistencias, `relatedEventId` conserva la relación con la citación.
- Si se crea o actualiza una asistencia vinculada a una citación, se actualiza el estado de asistencia de la citación relacionada.
- Si se elimina una asistencia, se recalcula el estado de la citación según la última asistencia restante; si no queda ninguna, vuelve a `AGENDADO` cuando existe ese catálogo.

## Retroalimentación

```json
{
  "stageId": 11,
  "correctionReason": "Corrección de retroalimentación.",
  "feedbacks": [
    {
      "action": "CREATE",
      "eventDate": "2026-08-28",
      "eventTime": "11:00:00",
      "biopsychosocialCommitmentCode": "NO_EVALUADO",
      "resultCode": "INGRESO_TRATAMIENTO",
      "comment": "Se registra retroalimentación corregida."
    }
  ]
}
```

Si el evento de retroalimentación queda con `resultCode = INGRESO_TRATAMIENTO`, el backend actualiza `entryToTreatmentAt` y detiene el KPI de espera (`waitingStopped = true`).

## Corrección de cierre de etapa/programa

```json
{
  "stageId": 11,
  "correctionReason": "Corrección de cierre formal por referencia.",
  "closure": {
    "closureReasonCode": "REFERENCIA",
    "closureDate": "2026-08-28",
    "closureComment": "Cierre formal de etapa origen."
  }
}
```

Por defecto, el cierre corrige solo la etapa indicada. No cierra el episodio completo salvo que se envíe:

```json
{
  "closure": {
    "closeEpisode": true
  }
}
```

## Auditoría

Cada bloque corregido deja auditoría en `episode_audit_logs` con:

- usuario ejecutor;
- fecha/hora;
- motivo de corrección;
- valores antes;
- valores después;
- etapa y evento cuando corresponda.

## SQL

No requiere SQL nuevo.
