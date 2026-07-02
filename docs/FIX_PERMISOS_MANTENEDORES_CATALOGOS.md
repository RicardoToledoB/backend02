# Fix permisos mantenedores de catálogos

Fecha: 2026-07-02

## Problema reportado

El frontend indicó que las rutas del mantenedor genérico existían en Swagger, pero respondían `403 Forbidden` con token vigente:

- `GET /api/v1/demand/maintainers/episodeTypes`
- `GET /api/v1/demand/maintainers/episodeTypes/1`
- `GET /api/v1/demand/maintainers/episodeTypes/getAllPaginated?page=0&size=10`
- `POST /api/v1/demand/maintainers/episodeTypes`
- `PUT /api/v1/demand/maintainers/episodeTypes/1`

## Ajustes realizados

### 1. Seguridad HTTP

Se agregó autorización explícita en `SecurityConfig` para:

```text
/api/v1/demand/maintainers/**
```

Roles autorizados:

- `ROLE_ADMIN`
- `ROLE_ADMINISTRATIVO`
- `ROLE_SUPERVISOR`

### 2. Seguridad a nivel controller

Se actualizó `CatalogMaintainerController` para validar por authority completa:

```java
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_ADMINISTRATIVO','ROLE_SUPERVISOR')")
```

Esto evita inconsistencias entre `hasAnyRole(...)` y authorities emitidas en el JWT.

### 3. Compatibilidad de nombres de catálogo

Se ajustó la normalización para soportar los formatos usados por frontend y backend:

- `episode-types`
- `episode_types`
- `episodeTypes`
- `EpisodeTypes`

Aplica también para:

- `eventTypes`
- `attendanceStatuses`
- `closureReasons`
- `programPopulations`
- `programModalities`
- `programPlans`
- `documentTypes`
- `regions`
- `cities`
- `semaphoreRules`

## Prueba sugerida

```bash
curl -i -X GET http://localhost:8095/api/v1/demand/maintainers/episodeTypes \
  -H "Authorization: Bearer TU_TOKEN"
```

Respuesta esperada para usuario ADMIN:

```text
HTTP/1.1 200 OK
```

También deben responder:

```text
GET    /api/v1/demand/maintainers/episodeTypes
GET    /api/v1/demand/maintainers/episodeTypes/1
GET    /api/v1/demand/maintainers/episodeTypes/getAllPaginated?page=0&size=10
POST   /api/v1/demand/maintainers/episodeTypes
PUT    /api/v1/demand/maintainers/episodeTypes/1
DELETE /api/v1/demand/maintainers/episodeTypes/1
POST   /api/v1/demand/maintainers/episodeTypes/1/restore
```
