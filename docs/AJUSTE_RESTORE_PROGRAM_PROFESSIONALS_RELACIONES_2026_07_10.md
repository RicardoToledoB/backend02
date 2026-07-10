# Ajuste restore facultativos / programas

## Problema detectado

Al ejecutar:

```http
DELETE /api/v1/program_professionals/{id}
PUT /api/v1/program_professionals/restore/{id}
GET /api/v1/program_professionals/{id}
```

el facultativo quedaba restaurado (`active=true`, `deletedAt=null`), pero las relaciones con programas quedaban vacías (`programIds=[]`, `programs=[]`).

## Causa

La tabla intermedia `program_professional_programs` se marca con `deleted_at` al eliminar el facultativo. El método de restauración usaba un bulk update JPQL sobre una entidad con `@Where(clause = "deleted_at IS NULL")`. En Hibernate, esta condición puede impedir que el update alcance filas eliminadas lógicamente.

## Corrección aplicada

Se cambió `ProgramProfessionalProgramRepository.restoreAllPrograms` a SQL nativo:

```sql
UPDATE program_professional_programs
SET deleted_at = NULL
WHERE program_professional_id = :professionalId;
```

Con esto el restore vuelve a activar todas las relaciones históricas del facultativo con sus programas.

## Validación esperada

```http
DELETE /api/v1/program_professionals/5
PUT /api/v1/program_professionals/restore/5
GET /api/v1/program_professionals/5
```

Respuesta esperada:

```json
{
  "id": 5,
  "active": true,
  "deletedAt": null,
  "programIds": [2, 1],
  "programs": [ ... ]
}
```

## Corrección manual si ya quedó un registro afectado

Si el facultativo ya fue restaurado antes de aplicar este fix, puede corregirse manualmente con:

```sql
UPDATE program_professional_programs
SET deleted_at = NULL
WHERE program_professional_id = 5;
```

Luego volver a consultar:

```http
GET /api/v1/program_professionals/5
```
