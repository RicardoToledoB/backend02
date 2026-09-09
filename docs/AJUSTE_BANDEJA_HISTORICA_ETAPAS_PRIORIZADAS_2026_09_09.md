# Ajuste backend - Bandeja histórica por programa/etapa

Fecha: 2026-09-09

## Objetivo

Agregar una API nueva para la bandeja longitudinal por programas sin modificar ni reemplazar el endpoint existente:

- Se mantiene sin cambios `GET /api/v1/demand/episodes/prioritized`.
- Se agrega `GET /api/v1/demand/episodes/prioritized/stages`.

## Endpoint nuevo

```http
GET /api/v1/demand/episodes/prioritized/stages
```

## Uso esperado

La respuesta entrega una fila por cada `EpisodeStage` del episodio. Si un episodio tiene varias etapas/programas, el `episodeId` y el `episodeCode` se repiten en cada fila.

Ejemplos:

```http
GET /api/v1/demand/episodes/prioritized/stages?page=0&size=20
GET /api/v1/demand/episodes/prioritized/stages?programId=2
GET /api/v1/demand/episodes/prioritized/stages?programId=2&stateCode=EN_TRAMITE
GET /api/v1/demand/episodes/prioritized/stages?programId=2&resultCode=REFERENCIA
GET /api/v1/demand/episodes/prioritized/stages?search=DEM-000011
GET /api/v1/demand/episodes/prioritized/stages?sort=receivedAt,desc
```

## Filtros

Los filtros se aplican sobre la fila de etapa/programa:

- `programId`: filtra por `episode_stages.program_id`.
- `stateCode`: filtra por `episode_stages.state_code`.
- `resultCode`: filtra por `episode_stages.result_code`.
- `search`: mantiene búsqueda por datos globales del episodio/persona, compatible con RUN, nombre, apellidos, nombre completo, `episodeId` y `episodeCode`.

## Paginación

La paginación se calcula sobre filas de etapas/programas, no sobre episodios.

## Datos incluidos por fila

Cada fila incluye:

- Datos globales del episodio: `episodeId`, `episodeCode`, RUN, persona, usuario creador, programa actual, etapa actual, `originalRequestDate`, `accumulatedDays`, semáforo.
- Datos propios de la etapa: `program`, `programId`, `programName`, `stageId`, `stageOrder`, `originStageId`, `receivedAt`, `closedAt`, `closureDate`, `daysInStage`, `stageStateCode`, `stageResultCode`, `closed`, `current`, `closureReason`, `closureComment`, responsable.
- Gestiones exclusivas de esa etapa: `events`, `lastManagement`, fechas de citaciones, retroalimentación, resultado de retroalimentación, compromiso biopsicosocial y acción sugerida calculada contra esa etapa.

## Criterio clave

La API histórica no reemplaza la bandeja priorizada actual. Su foco es permitir al frontend mostrar una vista longitudinal por programa, donde un mismo episodio puede aparecer varias veces, una por cada etapa/programa.
