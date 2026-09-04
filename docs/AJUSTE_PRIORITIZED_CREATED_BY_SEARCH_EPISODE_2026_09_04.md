# Ajuste bandeja priorizada: usuario creador y búsqueda por episodio

Fecha: 2026-09-04

## Endpoint ajustado

`GET /api/v1/demand/episodes/prioritized`

## Cambios

- `PrioritizedEpisodeDTO` incorpora `createdByUser` con la misma estructura usada por `EpisodeDTO`:

```json
"createdByUser": {
  "id": 27,
  "name": "PATRICIO IVAN JARA GARCES",
  "email": "patricio.jara@redsalud.gob.cl"
}
```

- El parámetro `search` mantiene la búsqueda por RUN/RUT, nombre, primer apellido, segundo apellido y nombre completo.
- Además permite buscar por:
  - `episodeId`, por ejemplo `search=11`
  - `episodeCode`, por ejemplo `search=DEM-000011` o `search=DEM000011`

## Compatibilidad

No modifica la estructura paginada ni los filtros existentes (`page`, `size`, `programId`, `stateCode`, `resultCode`, `sort`).

## Base de datos

No requiere SQL nuevo.
