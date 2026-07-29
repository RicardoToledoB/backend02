# Fix prioritized/dashboard etapa actual y demandas cerradas - 2026-07-29

## Requerimiento

Corregir cálculos de bandeja y dashboard para que ciertos datos se basen exclusivamente en la etapa vigente o indicada por `episodes.current_stage_id`.

## Cambios aplicados

### GET /api/v1/demand/episodes/prioritized

- `suggestedAction` ahora considera solamente eventos de la etapa resuelta como actual para lectura:
  - `event.stage_id = episode.current_stage_id`
  - Si no hay primera citación a primera entrevista en esa etapa, retorna: `Programar primera citación a primera entrevista`.
- Se mantiene `lastManagement`, `lastManagementDate` y `lastManagementTime` calculados desde la etapa actual.
- La consulta permite `stateCode=CERRADO` sin exigir:
  - `episode.active = true`
  - `episode.closed_at IS NULL`
  - etapa con `is_current = true`
- Para demandas cerradas se utiliza la etapa apuntada por `episode.current_stage_id`, aunque esa etapa tenga `is_current = false`.

### GET /api/v1/demand/dashboard/supervisor

- `withoutFirstCitation` ahora se calcula considerando solamente la primera citación a primera entrevista de la etapa actual.
- No considera citaciones registradas en etapas anteriores.

### GET /api/v1/demand/dashboard/supervisor/programs

- `withoutFirstCitation` por programa ahora se calcula con eventos de la etapa actual del episodio.
- No considera eventos de etapas anteriores.

## Archivos modificados

- `src/main/java/com/cosam/project01/demand/repository/EpisodeRepository.java`
- `src/main/java/com/cosam/project01/demand/service/DemandService.java`

## SQL

No requiere SQL nuevo.

## Pruebas sugeridas

```bash
curl -i "http://localhost:8095/api/v1/demand/episodes/prioritized" \
  -H "Authorization: Bearer $TOKEN"

curl -i "http://localhost:8095/api/v1/demand/episodes/prioritized?stateCode=CERRADO" \
  -H "Authorization: Bearer $TOKEN"

curl -i "http://localhost:8095/api/v1/demand/dashboard/supervisor" \
  -H "Authorization: Bearer $TOKEN"

curl -i "http://localhost:8095/api/v1/demand/dashboard/supervisor/programs" \
  -H "Authorization: Bearer $TOKEN"
```

## Casos esperados

- DEM-000001, con etapa actual sin citaciones:
  - `suggestedAction`: `Programar primera citación a primera entrevista`
  - `withoutFirstCitation` general: debe sumar 1.
  - `withoutFirstCitation` en PAB Thomas Fenton: debe sumar 1.
- DEM-000002 cerrado:
  - Debe aparecer en `GET /api/v1/demand/episodes/prioritized?stateCode=CERRADO`.
