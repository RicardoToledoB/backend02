# Fix corrección administrativa: DELETE de citación y asistencia relacionada

Fecha: 2026-09-02

## Endpoint ajustado

`PUT /api/v1/demand/episodes/{id}/administrative-correction`

## Problema detectado

Cuando el frontend enviaba en una misma solicitud la anulación lógica de una citación y la anulación lógica de su asistencia relacionada, el backend podía responder HTTP 500.

Caso reportado:

- Episodio 11
- Etapa 17
- DELETE asistencia 236 sola: 200
- DELETE citación 235 sola: 200
- DELETE ambas juntas en el mismo PUT: 500

La causa funcional era el orden de procesamiento de eventos dentro de la corrección administrativa: se procesaban las citaciones antes que las asistencias. Al quedar la citación anulada mediante `deleted_at`, Hibernate podía intentar resolver posteriormente `relatedEvent` desde la asistencia hacia una citación ya filtrada por `@Where(deleted_at IS NULL)`.

## Ajuste aplicado

Se centralizó el procesamiento de eventos administrativos en un flujo con orden seguro:

1. Primero se procesan DELETE de `ASISTENCIA`.
2. Luego otros DELETE.
3. Finalmente DELETE de `CITACION`.
4. Después se procesan CREATE/UPDATE.

Esto permite que, en una misma transacción, una asistencia relacionada sea anulada antes que su citación de origen.

## Resultado esperado

El siguiente request debe responder 200:

```json
{
  "stageId": 17,
  "correctionReason": "Corrección administrativa: eliminación de citación y asistencia relacionada.",
  "attendances": [
    {
      "action": "DELETE",
      "eventId": 236
    }
  ],
  "citations": [
    {
      "action": "DELETE",
      "eventId": 235
    }
  ]
}
```

También se mantiene compatible si el frontend envía ambas operaciones en el arreglo genérico `events[]`, ya que el backend ordena los DELETE según el tipo real del evento.

## SQL

No requiere cambios SQL.
