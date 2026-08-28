# Ajuste administrativo: reapertura de etapa y corrección de fecha de ingreso a programa

Fecha: 2026-08-28

## 1. Reapertura administrativa de etapa

Endpoint existente:

```http
PUT /api/v1/demand/episodes/{id}/administrative-correction
```

Cuando el request incluya `closure.closed=false`, el backend interpreta explícitamente una reapertura administrativa de la etapa indicada por `closure.stageId`, `stageId` o `programId`.

Campos aplicados de manera transaccional sobre `episode_stages`:

```text
closedAt = null
closureReasonId = null
closureComment = null
stateCode = EN_TRAMITE, salvo que se informe stateCode
resultCode = AUN_SIN_RESULTADO, salvo que se informe resultCode
current = true
```

No se elimina el evento histórico de cierre. La acción queda registrada en `episode_audit_logs` como `CORRECCION_ADMINISTRATIVA_CIERRE_ETAPA`.

El episodio global no se cierra ni modifica por esta reapertura, salvo que el request envíe explícitamente correcciones en el bloque `episode` o `closeEpisode=true` en un cierre.

## 2. Corrección administrativa de fecha de ingreso a programa

Nuevo endpoint:

```http
PUT /api/v1/demand/episodes/{episodeId}/programs/{programId}/received-at
```

Request:

```json
{
  "receivedAt": "2026-01-23",
  "correctionReason": "Corrección de fecha de ingreso al programa"
}
```

Opcionalmente acepta `stageId` cuando el mismo programa participa más de una vez en el episodio:

```json
{
  "stageId": 35,
  "receivedAt": "2026-01-23",
  "correctionReason": "Corrección de fecha de ingreso al programa"
}
```

Reglas:

- Si no se informa `stageId`, se corrige la última etapa del programa en el episodio.
- Se actualiza `episode_stages.received_at`.
- `daysInStage` e indicadores temporales se recalculan automáticamente desde la nueva fecha porque son campos calculados.
- Si corresponde al programa inicial y a la primera etapa, se sincroniza `episodes.original_request_date` con la fecha recibida.
- Se registra auditoría con valores antes/después.
- Requiere `ROLE_ADMIN`.
