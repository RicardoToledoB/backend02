# Homologación de Postulant commune/city

## Problema

`/api/v1/postulants` mantiene la relación histórica `postulants.commune_id -> communes.id`, pero el frontend utiliza el catálogo oficial nuevo:

`/api/v1/demand/maintainers/cities`

Por eso IDs existentes en `cities`, como Porvenir `id=3`, podían generar error 500 al crear o actualizar postulantes si no existían previamente en `communes`.

## Solución aplicada

Se mantiene la compatibilidad con el modelo histórico y se sincroniza `communes` desde el catálogo oficial `cities`.

La solución contempla dos capas:

1. SQL idempotente para sincronización inicial:

`sql/2026_08_20_sync_communes_from_cities_for_postulants.sql`

2. Sincronización automática al iniciar el backend:

`CommuneCitySynchronizationService`

Esto permite que `/api/v1/postulants` acepte `commune.id` proveniente de `cities`, por ejemplo:

```json
{
  "commune": { "id": 3 }
}
```

y lo resuelva correctamente como Porvenir.

## Criterio

No se migra aún la columna histórica `postulants.commune_id` a `city_id` para evitar romper módulos antiguos que siguen usando `communes`.

Esta alternativa es segura, compatible hacia atrás y evita el error 500.
