# Ajuste catálogo Compromiso Biopsicosocial - NO_EVALUADO

Fecha: 2026-08-27

## Requerimiento

Incorporar al catálogo `biopsychosocial_commitment_levels` el registro:

| Código | Descripción |
|---|---|
| `NO_EVALUADO` | No fue evaluado |

Actualmente el catálogo contenía `LEVE`, `MODERADO` y `SEVERO`.

## Cambios realizados

- Se agregó `NO_EVALUADO` al seeder `DemandCatalogSeeder`.
- Se agregó SQL idempotente:
  - `sql/2026_08_27_biopsychosocial_commitment_no_evaluado.sql`
- Se actualizó el SQL base del catálogo `2026_07_27_citation_types_biopsychosocial_commitment.sql` para instalaciones nuevas.
- Se dejó explícito el orden lógico usado por la bandeja priorizada:
  1. `SEVERO`
  2. `MODERADO`
  3. `LEVE`
  4. `NO_EVALUADO`
  5. `null`

## Aplicación

```bash
cd /var/www/html/gestiondemanda-api/sql
sudo mysql -u root -p demanda_drogas < 2026_08_27_biopsychosocial_commitment_no_evaluado.sql
```

Luego compilar y reiniciar el backend:

```bash
cd /var/www/html/gestiondemanda-api
./mvnw clean package -DskipTests
sudo systemctl restart gestiondemanda-api
```

## Validación

```sql
SELECT id, code, name, active
FROM biopsychosocial_commitment_levels
WHERE code = 'NO_EVALUADO';
```

El catálogo expuesto en `GET /api/v1/demand/catalogs` debe incluir:

```json
{
  "code": "NO_EVALUADO",
  "name": "No fue evaluado"
}
```
