# Ajuste backend - PUT `/api/v1/results/{id}` y registros históricos

Fecha: 2026-07-14

## Problema informado

El frontend reportó que:

```http
PUT /api/v1/results/{id}
```

respondía `403 Forbidden` al intentar editar los resultados históricos/base `1–8`, aun con token Bearer válido.

Además, los registros duplicados `9–16` ya habían sido eliminados lógicamente, pero se requiere completar `code`, `scope`, `description` y `active` en los registros históricos `1–8`.

## Ajustes realizados

### 1. Seguridad

Se reforzó la autorización de:

```http
/api/v1/results/**
```

permitiendo acceso a:

```text
ROLE_ADMIN
ROLE_ADMINISTRATIVO
ROLE_SUPERVISOR
ROLE_PROFESIONAL
```

También se agregó autorización explícita al método:

```http
PUT /api/v1/results/{id}
```

### 2. Sin bloqueo por registro histórico/base

El servicio de resultados ya no aplica ninguna regla que impida modificar los IDs históricos/base `1–8`.

### 3. Duplicados eliminados lógicamente

Como MySQL mantiene la restricción `UNIQUE(code)` aunque un registro tenga `deleted_at`, se incorporó una protección:

- Si el código enviado al registro histórico está ocupado por un registro eliminado lógicamente, el backend libera el código del eliminado cambiándolo a:

```text
DELETED_{id}_{codigo_original}
```

- Luego actualiza el registro histórico con el código solicitado.

Esto permite completar correctamente los registros `1–8` aunque los duplicados `9–16` sigan existiendo como eliminados lógicos.

### 4. Soft delete y restore

El delete ahora marca:

```sql
deleted_at = CURRENT_TIMESTAMP,
active = false
```

El restore ahora marca:

```sql
deleted_at = NULL,
active = true
```

## Prueba sugerida

```bash
curl -i -X PUT http://localhost:8095/api/v1/results/1 \
  -H "Authorization: Bearer TU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Egreso",
    "code": "EGRESO",
    "scope": "DEMAND",
    "description": "Resultado de egreso del episodio",
    "active": true
  }'
```

Respuesta esperada: `200 OK`.

