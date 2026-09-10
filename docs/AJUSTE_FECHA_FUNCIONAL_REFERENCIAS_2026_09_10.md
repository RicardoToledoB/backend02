# Ajuste fecha funcional de referencias

## Problema
En trayectorias históricas, la nueva etapa destino podía quedar con `receivedAt` igual a la fecha/hora de registro en sistema, aunque la fecha funcional de la referencia/cierre fuera anterior.

Ejemplo observado: DEM-000051 con cierre funcional por REFERENCIA el 2026-04-16, pero etapa destino registrada el 2026-09-08.

## Regla aplicada
- `episode_stages.closedAt` de la etapa origen: fecha funcional de cierre de esa etapa.
- `episode_references.referenceDate`: fecha funcional de referencia.
- `episode_stages.receivedAt` de la etapa destino: fecha funcional de ingreso al programa destino.
- `episode_events.eventDate/eventTime` del evento REFERENCIA: fecha funcional de la referencia.
- `createdAt`: fecha real de registro/auditoría; no se modifica.

## Cambios backend
- `POST /api/v1/demand/episodes/{id}/references`:
  - Si viene `referenceDate`, se usa como fecha funcional.
  - Si no viene y la etapa origen ya está cerrada por REFERENCIA, usa `originStage.closedAt`.
  - Si no existe ninguna de las anteriores, usa la fecha/hora de registro.
  - Ya no limpia el cierre de una etapa origen que ya estaba cerrada.

- `POST /api/v1/demand/episodes/{id}/close` con causal REFERENCIA:
  - Cierra funcionalmente solo la etapa origen.
  - Sincroniza referencias salientes existentes: `referenceDate`, `receivedAt` de etapa destino y evento REFERENCIA.
  - El evento CIERRE queda con fecha funcional en `eventDate/eventTime`, manteniendo `createdAt` como registro/auditoría.

- `PUT /api/v1/demand/episodes/{id}/administrative-correction`:
  - Al cerrar una etapa por REFERENCIA, sincroniza las referencias salientes con la fecha funcional de cierre.
  - En CREATE de referencia sin `referenceDate`, si la etapa origen está cerrada por REFERENCIA, usa `originStage.closedAt`.

## DTOs extendidos
- `EpisodeReferenceDTO` agrega `createdAt` y `updatedAt`.
- `EpisodeStageDTO` agrega `createdAt` y `updatedAt`.
- `PrioritizedEpisodeStageDTO` agrega:
  - `stageCreatedAt`
  - `stageUpdatedAt`
  - `inboundReferenceId`
  - `inboundReferenceDate`
  - `inboundReferenceCreatedAt`

## SQL de corrección histórica
Se incorpora:

`sql/2026_09_10_sync_reference_functional_dates_from_origin_closure.sql`

Sirve para corregir registros ya existentes donde la etapa origen tiene cierre por REFERENCIA y la etapa destino quedó con fecha de registro en lugar de fecha funcional.
