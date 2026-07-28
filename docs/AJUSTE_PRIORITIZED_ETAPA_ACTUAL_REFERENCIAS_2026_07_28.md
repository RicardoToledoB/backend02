# Ajuste Gestión de Demanda - Priorizados etapa actual y dashboard de referencias

Fecha: 2026-07-28

## 1. GET /api/v1/demand/episodes/prioritized

Se agregan por episodio los siguientes campos:

- `currentStageId`
- `currentStageStateCode`
- `currentStageResultCode`
- `currentStageReceivedAt`
- `currentStageDays`
- `originProgramId`
- `originProgramName`
- `referenceCount`

Además, `stateCode`, `resultCode` y `suggestedAction` se calculan desde la etapa vigente (`current=true`) cuando existe. Esto evita que, luego de una referencia, la bandeja siga mostrando el resultado histórico del episodio en lugar del resultado vigente de la etapa receptora.

`originProgramId` y `originProgramName` se obtienen desde la etapa inmediatamente anterior que originó la etapa actual. Para la primera etapa retornan `null`.

`referenceCount` corresponde al total de registros en `episode_references` asociados al episodio.

Se amplió el ordenamiento manual de la bandeja para aceptar `sort=campo,asc|desc` sobre los nuevos campos.

## 2. GET /api/v1/demand/dashboard/supervisor/programs/references

Nuevo endpoint:

```http
GET /api/v1/demand/dashboard/supervisor/programs/references?from=YYYY-MM-DD&to=YYYY-MM-DD
```

Si no se informa `from` ni `to`, se usa el mes actual.

Entrega por programa:

- `programId`
- `programName`
- `receivedReferences`
- `sentReferences`
- `pendingReferences`
- `referenceBalance`
- `averageDaysBeforeReference`
- `referenceReasons`

Criterios implementados:

- `receivedReferences`: referencias donde el programa es destino.
- `sentReferences`: referencias donde el programa es origen.
- `pendingReferences`: etapa actual con `resultCode = REFERENCIA`, sin referencia registrada con esa etapa como origen.
- `referenceBalance`: recibidas menos enviadas.
- `averageDaysBeforeReference`: promedio de días entre la recepción de la etapa de origen y la fecha de referencia, calculado para referencias recibidas por el programa.
- `referenceReasons`: motivos agrupados de referencias recibidas por el programa.

## 3. SQL

No requiere SQL nuevo. Usa las tablas y columnas ya existentes:

- `episode_stages`
- `episode_references`
- `episode_events`
- `citation_types`
- `biopsychosocial_commitment_levels`
