# Ajuste catálogo closureReasons - Matriz Gestión de Demanda

Fecha: 2026-08-13

## Requerimiento

Completar el catálogo `closureReasons` para que contemple las tres causales vigentes de cierre definidas en la matriz de Gestión de Demanda:

- `REFERENCIA` — Referencia
- `INGRESO_TRATAMIENTO` — Ingreso a tratamiento
- `ABANDONO` — Abandono

## Cambios backend

- Se agregó seed de `REFERENCIA` e `INGRESO_TRATAMIENTO` en `DemandCatalogSeeder`.
- Se mantuvo `ABANDONO` activo.
- Las causales históricas quedan conservadas en base de datos, pero inactivas, para no exponerlas como opciones vigentes.
- `GET /api/v1/demand/catalogs` ahora entrega `closureReasons` filtrando solo registros activos.

## SQL

Ejecutar una vez:

```bash
sudo mysql -u root -p demanda_drogas < sql/2026_08_13_closure_reasons_matriz_demanda.sql
```

## Validación

```bash
curl -i http://localhost:8095/api/v1/demand/catalogs \
  -H "Authorization: Bearer $TOKEN"
```

En `closureReasons` deben aparecer las opciones activas `REFERENCIA`, `INGRESO_TRATAMIENTO` y `ABANDONO`.
