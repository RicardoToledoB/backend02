# Ajuste referencias administrativas y bandeja priorizada - 2026-09-01

## 1. `POST /api/v1/demand/episodes/{id}/references`

Se incorpora el campo opcional `referenceDate` en el request.

Acepta:

```json
{
  "originStageId": 35,
  "destinationProgramId": 3,
  "referenceDate": "2026-09-01",
  "reason": "Derivación a programa destino",
  "observation": "Observación",
  "confirmImpact": true
}
```

También acepta fecha con hora:

```json
{
  "referenceDate": "2026-09-01T09:30:00"
}
```

La fecha indicada se sincroniza en:

- `episode_references.reference_date`.
- `episode_stages.received_at` de la etapa destino creada.
- `episode_events.event_date` y `episode_events.event_time` del evento `REFERENCIA`.

Si `referenceDate` no viene informado, se usa la fecha/hora actual.

## 2. `PUT /api/v1/demand/episodes/{id}/administrative-correction`

Se agrega `references[]` al request de corrección administrativa.

Soporta acciones:

- `CREATE`
- `UPDATE`
- `DELETE`

### Crear referencia administrativa

```json
{
  "stageId": 35,
  "correctionReason": "Corrección administrativa de referencia",
  "references": [
    {
      "action": "CREATE",
      "originStageId": 35,
      "destinationProgramId": 3,
      "referenceDate": "2026-09-01",
      "reason": "Derivación a programa destino",
      "observation": "Creación administrativa de referencia"
    }
  ]
}
```

Regla:

- crea referencia;
- crea etapa destino si no se informa `destinationStageId`;
- crea evento `REFERENCIA` en la etapa origen;
- sincroniza `referenceDate`, `eventDate/eventTime` y `receivedAt` de la etapa destino;
- registra auditoría.

Por defecto, una corrección administrativa no cambia la etapa actual del episodio. Si se requiere marcar la etapa destino como actual, enviar:

```json
{
  "makeDestinationCurrent": true
}
```

### Actualizar referencia administrativa

```json
{
  "stageId": 35,
  "correctionReason": "Corrección de fecha de referencia",
  "references": [
    {
      "action": "UPDATE",
      "referenceId": 10,
      "referenceDate": "2026-09-02",
      "reason": "Fecha efectiva corregida",
      "observation": "Ajuste solicitado por supervisión"
    }
  ]
}
```

Regla:

- actualiza `episode_references`;
- sincroniza evento `REFERENCIA` asociado;
- actualiza `receivedAt` de la etapa destino;
- registra auditoría con valores antes/después.

Si se informa `eventId`, se usa ese evento. Si no se informa, el backend intenta resolver el evento `REFERENCIA` por episodio, etapa origen y fecha. Si no existe evento, lo crea para mantener consistencia.

### Anular referencia administrativa

```json
{
  "correctionReason": "Referencia registrada por error",
  "references": [
    {
      "action": "DELETE",
      "referenceId": 10
    }
  ]
}
```

Regla:

- anula lógicamente la referencia;
- anula lógicamente el evento `REFERENCIA` asociado cuando se puede resolver;
- no elimina físicamente etapas;
- registra auditoría.

## 3. `GET /api/v1/demand/episodes/prioritized`

Se ajusta la bandeja priorizada:

- Si la etapa actual está abierta, no considera eventos históricos `CIERRE` como `lastManagement`.
- Si existe retroalimentación vigente en la etapa actual, `suggestedAction` se calcula desde el resultado de esa retroalimentación y no vuelve a proponer citaciones.
- Se agrega `feedbackResultCode` al `PrioritizedEpisodeDTO`.

Ejemplo esperado:

```json
{
  "lastManagement": "Retroalimentación",
  "feedbackResultCode": "REFERENCIA",
  "suggestedAction": "Referir a otro programa"
}
```
