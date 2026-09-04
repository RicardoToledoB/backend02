# Ajuste final bandeja priorizada: createdByUser y búsqueda por episodio

## Endpoint

`GET /api/v1/demand/episodes/prioritized`

## Cambios

- `PrioritizedEpisodeDTO` expone `createdByUser` con estructura `id`, `name`, `email`, igual que `EpisodeDTO`.
- `search` permite buscar por:
  - RUN/RUT de persona.
  - Nombre, primer apellido, segundo apellido y nombre completo.
  - `episodeId`, por ejemplo `11`.
  - `episodeCode`, por ejemplo `DEM-000011` o `DEM000011`.
- Cuando `search` es numérico corto, por ejemplo `11`, se interpreta como búsqueda operativa por `episodeId`/correlativo del episodio, evitando que la bandeja devuelva principalmente RUN que contienen `11`.
- No cambia la estructura paginada ni los parámetros existentes `page`, `size`, `programId`, `stateCode`, `resultCode` y `sort`.

## Ejemplos

```http
GET /api/v1/demand/episodes/prioritized?search=11
GET /api/v1/demand/episodes/prioritized?search=DEM-000011
GET /api/v1/demand/episodes/prioritized?search=DEM000011
GET /api/v1/demand/episodes/prioritized?search=14.036.818-0
GET /api/v1/demand/episodes/prioritized?search=Sofia
```

## SQL

No requiere SQL nuevo.
