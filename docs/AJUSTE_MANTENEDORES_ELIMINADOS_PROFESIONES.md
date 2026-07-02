# Ajuste mantenedores: eliminados/inactivos y profesiones

## 1. Mantenedor genérico de catálogos

Se ajustó `catalog-maintainer-controller` para que el frontend pueda listar registros activos, inactivos, eliminados lógicamente y todos los registros.

Ruta base:

```text
/api/v1/demand/maintainers
```

Catálogos soportados:

```text
episodeTypes / episode-types / episode_types
eventTypes / event-types / event_types
attendanceStatuses / attendance-statuses / attendance_statuses
closureReasons / closure-reasons / closure_reasons
programPopulations / program-populations / program_populations
programModalities / program-modalities / program_modalities
programPlans / program-plans / program_plans
documentTypes / document-types / document_types
regions
cities
semaphoreRules / semaphore-rules / semaphore_rules
```

### Endpoints agregados o ajustados

```http
GET /api/v1/demand/maintainers/{catalog}
GET /api/v1/demand/maintainers/{catalog}?active=false
GET /api/v1/demand/maintainers/{catalog}?includeDeleted=true
GET /api/v1/demand/maintainers/{catalog}?deleted=true
GET /api/v1/demand/maintainers/{catalog}/all
GET /api/v1/demand/maintainers/{catalog}/deleted
GET /api/v1/demand/maintainers/{catalog}/inactive
GET /api/v1/demand/maintainers/{catalog}/getAllPaginated?page=0&size=10&includeDeleted=true
GET /api/v1/demand/maintainers/{catalog}/{id}?includeDeleted=true
POST /api/v1/demand/maintainers/{catalog}
PUT /api/v1/demand/maintainers/{catalog}/{id}
DELETE /api/v1/demand/maintainers/{catalog}/{id}
POST /api/v1/demand/maintainers/{catalog}/{id}/restore
```

### Comportamiento oficial de eliminación

Al eliminar un catálogo, el backend ahora marca:

```text
deleted_at = CURRENT_TIMESTAMP
active = false
```

Al restaurar:

```text
deleted_at = NULL
active = true
```

Esto permite que el frontend pueda listar los eliminados y restaurarlos.

> Nota: el código del registro eliminado se conserva para trazabilidad. Si se intenta crear un nuevo registro con el mismo `code`, la base puede rechazarlo por índice único. En ese caso corresponde restaurar el registro eliminado o cambiar su código antes de crear uno nuevo.

## 2. Mantenedor de profesiones

Se agregó mantenedor API para profesiones.

Ruta base:

```text
/api/v1/professions
```

Endpoints:

```http
GET /api/v1/professions
GET /api/v1/professions/all
GET /api/v1/professions/deleted
GET /api/v1/professions/getAllPaginated?page=0&size=10
GET /api/v1/professions/{id}
POST /api/v1/professions
PUT /api/v1/professions/{id}
DELETE /api/v1/professions/{id}
POST /api/v1/professions/{id}/restore
```

### JSON para crear profesión

```json
{
  "code": "PSICOLOGO",
  "name": "Psicólogo/a",
  "description": "Profesional psicólogo/a",
  "active": true
}
```

## 3. Seguridad

Se habilitó acceso para:

```text
ROLE_ADMIN
ROLE_ADMINISTRATIVO
ROLE_SUPERVISOR
```

sobre:

```text
/api/v1/demand/maintainers/**
/api/v1/professions/**
```
