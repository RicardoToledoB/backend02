# Ajuste Backend - Relaciones eliminadas de facultativos

Fecha: 2026-07-10

## Requerimiento

El frontend solicitó el endpoint:

```http
GET /api/v1/program_professionals/{id}/program-relations/deleted
```

para poder consultar las relaciones eliminadas lógicamente entre un facultativo y sus programas.

## Endpoint agregado

```http
GET /api/v1/program_professionals/{id}/program-relations/deleted
Authorization: Bearer <token>
```

## Respuesta esperada

```json
[
  {
    "id": 10,
    "programProfessionalId": 5,
    "programId": 2,
    "programName": "Programa Adulto Ambulatorio",
    "active": false,
    "createdAt": "2026-07-10T10:30:00",
    "deletedAt": "2026-07-10T11:00:00"
  }
]
```

## Uso funcional

Este endpoint permite al frontend validar qué vínculos fueron eliminados con `deleted_at` en `program_professional_programs`.

También sirve para verificar que el flujo:

```http
DELETE /api/v1/program_professionals/{id}
PUT    /api/v1/program_professionals/restore/{id}
GET    /api/v1/program_professionals/{id}
```

restaure correctamente las relaciones del facultativo con sus programas.
