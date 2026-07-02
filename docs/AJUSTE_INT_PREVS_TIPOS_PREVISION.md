# Ajuste backend: mantenedor Tipos de previsión (`int_prevs`)

Se habilita el mantenedor de Tipos de previsión requerido por el frontend y por el catálogo padre de Convenios previsionales.

## Seguridad

Se autoriza explícitamente la ruta:

```text
/api/v1/int_prevs/**
```

para los roles/authorities:

```text
ROLE_ADMIN
ROLE_ADMINISTRATIVO
ROLE_SUPERVISOR
```

## Endpoints disponibles

```http
GET    /api/v1/int_prevs
GET    /api/v1/int_prevs/all
GET    /api/v1/int_prevs/getAllPaginated?page=0&size=10
GET    /api/v1/int_prevs/deleted
GET    /api/v1/int_prevs/findById/{id}
GET    /api/v1/int_prevs/{id}
POST   /api/v1/int_prevs
PUT    /api/v1/int_prevs/{id}
DELETE /api/v1/int_prevs/softDelete/{id}
DELETE /api/v1/int_prevs/{id}
PUT    /api/v1/int_prevs/restore/{id}
POST   /api/v1/int_prevs/{id}/restore
```

## Body sugerido

```json
{
  "code": "FONASA",
  "name": "FONASA",
  "description": "Fondo Nacional de Salud",
  "active": true
}
```

## Eliminación y restauración

La eliminación es lógica:

```text
deleted_at = CURRENT_TIMESTAMP
active = false
```

La restauración deja:

```text
deleted_at = NULL
active = true
```
