# Ajuste stageId, referencia, cierre y suggestedAction — 2026-08-14

## Objetivo

Ajustar el backend para que las acciones del episodio no dependan solamente del `programId`, sino de la etapa concreta (`stageId`) sobre la cual cada programa está gestionando el episodio.

## Cambios funcionales

### 1. Referencia no cierra automáticamente la etapa origen

Endpoint afectado:

```http
POST /api/v1/demand/episodes/{episodeId}/references
```

Nuevo comportamiento:

- Crea la nueva etapa receptora.
- Actualiza `episode.currentStageId` y `episode.currentProgram` al programa receptor.
- La etapa origen queda con `current=false`, `stateCode=EN_TRAMITE`, `resultCode=REFERENCIA` y `closedAt=null`.
- La etapa origen queda disponible para cierre formal posterior mediante causal `REFERENCIA`.
- El evento `REFERENCIA` queda asociado a la etapa origen.

### 2. Cierre recibe stageId

Endpoint afectado:

```http
POST /api/v1/demand/episodes/{episodeId}/close
```

El request ahora acepta:

```json
{
  "stageId": 11,
  "closureReasonId": 1,
  "closureDate": "2026-08-14",
  "observation": "Cierre formal por referencia"
}
```

Para compatibilidad, si no se informa `stageId`, se mantiene el uso de la etapa actual.

También se aceptan como texto de cierre:

- `closureComment`
- `observation`
- `comment`

### 3. Cierre con causal REFERENCIA cierra solo la etapa indicada

Si la causal de cierre tiene código `REFERENCIA`:

- Cierra solamente el `stageId` recibido.
- Registra evento `CIERRE` asociado a ese `stageId`.
- No cierra el episodio.
- No modifica `episode.closedAt`.
- No cambia `episode.currentStageId`.
- No cierra la etapa receptora actual.

### 4. Cierres terminales sí cierran episodio completo

Para causales terminales como:

- `INGRESO_TRATAMIENTO`
- `ABANDONO`

se cierra la etapa indicada y el episodio completo.

### 5. suggestedAction sin acciones para episodios cerrados

En:

```http
GET /api/v1/demand/episodes/prioritized
```

si el episodio está realmente cerrado (`closedAt != null`, `active=false` o `stateCode=CERRADO`), `suggestedAction` retorna `null` para no proponer nuevas acciones operativas.

### 6. suggestedAction sigue calculando por etapa actual

La secuencia de citaciones, asistencias, reprogramaciones y cancelaciones sigue usando únicamente eventos de la etapa actual (`event.stage_id = episode.current_stage_id`) y la relación de asistencia con la citación mediante `relatedEventId`.
