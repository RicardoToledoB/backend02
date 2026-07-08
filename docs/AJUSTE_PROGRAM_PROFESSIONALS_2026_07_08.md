# Ajuste Backend - Facultativos por programa

## Objetivo

Se incorpora el mantenedor `program_professionals` solicitado por frontend para administrar facultativos/profesionales externos o asociados a programas, permitiendo que un mismo facultativo esté asociado a uno o varios programas.

## Tablas incorporadas

### `program_professionals`

Campos principales:

- `id`
- `name`
- `profession_id`
- `email`
- `phone`
- `observation`
- `active`
- `created_at`
- `updated_at`
- `deleted_at`

### `program_professional_programs`

Tabla intermedia para relación muchos-a-muchos entre facultativo y programas.

Campos principales:

- `id`
- `program_professional_id`
- `program_id`
- `created_at`
- `deleted_at`

Incluye restricción única:

- `program_professional_id + program_id`

La lógica del backend reutiliza vínculos eliminados si se vuelve a asociar el mismo programa, para evitar conflicto con la restricción única.

## Endpoint base

```http
/api/v1/program_professionals
```

## Seguridad

Habilitado para:

- `ROLE_ADMIN`
- `ROLE_ADMINISTRATIVO`
- `ROLE_SUPERVISOR`

## Endpoints disponibles

```http
GET    /api/v1/program_professionals
GET    /api/v1/program_professionals/all
GET    /api/v1/program_professionals/deleted
GET    /api/v1/program_professionals/getAllPaginated?page=0&size=10
GET    /api/v1/program_professionals/{id}
GET    /api/v1/program_professionals/findById/{id}
POST   /api/v1/program_professionals
PUT    /api/v1/program_professionals/{id}
DELETE /api/v1/program_professionals/{id}
DELETE /api/v1/program_professionals/softDelete/{id}
POST   /api/v1/program_professionals/{id}/restore
PATCH  /api/v1/program_professionals/restore/{id}
PUT    /api/v1/program_professionals/restore/{id}
GET    /api/v1/program_professionals/program/{programId}
```

## Filtros de paginación

```http
GET /api/v1/program_professionals/getAllPaginated?q=maria&professionId=3&programId=1&page=0&size=10
```

Parámetros opcionales:

- `q`: busca por nombre, correo, teléfono, observación o profesión.
- `professionId`: filtra por profesión.
- `programId`: filtra por programa asociado.

## Request para crear/editar

```json
{
  "name": "Dra. María González",
  "professionId": 3,
  "email": "maria.gonzalez@redsalud.gob.cl",
  "phone": "+56 9 1234 5678",
  "observation": "Atiende citaciones de ingreso y evaluación.",
  "programIds": [1, 2, 5]
}
```

## Respuesta esperada

```json
{
  "id": 1,
  "name": "Dra. María González",
  "professionId": 3,
  "professionCode": "MEDICO",
  "professionName": "Médico",
  "email": "maria.gonzalez@redsalud.gob.cl",
  "phone": "+56 9 1234 5678",
  "observation": "Atiende citaciones de ingreso y evaluación.",
  "active": true,
  "programIds": [1, 2, 5],
  "programs": [
    {
      "id": 1,
      "name": "Programa 1"
    }
  ],
  "createdAt": "2026-07-08T15:00:00",
  "updatedAt": null,
  "deletedAt": null
}
```

## Regla importante

Un facultativo puede estar asociado a varios programas. La tabla `program_professional_programs` maneja dicha relación.

Al editar `programIds`, el backend sincroniza los programas:

- Reactiva vínculos existentes si habían sido eliminados.
- Crea vínculos nuevos si no existían.
- Marca como eliminados (`deleted_at`) los vínculos que ya no vienen en el request.

## Prueba rápida

```bash
curl -X POST http://localhost:8095/api/v1/program_professionals \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_TOKEN" \
  -d '{
    "name": "Dra. María González",
    "professionId": 3,
    "email": "maria.gonzalez@redsalud.gob.cl",
    "phone": "+56 9 1234 5678",
    "observation": "Atiende citaciones de ingreso y evaluación.",
    "programIds": [1, 2, 5]
  }'
```
