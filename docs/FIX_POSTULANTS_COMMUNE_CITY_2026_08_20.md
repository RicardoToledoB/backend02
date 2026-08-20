# Fix postulants commune/city

## Problema

El frontend utiliza el catálogo oficial:

```http
GET /api/v1/demand/maintainers/cities
```

pero el endpoint histórico:

```http
POST /api/v1/postulants
PUT  /api/v1/postulants/{id}
```

persistía el campo recibido en `commune.id` contra la entidad histórica `CommuneEntity`, asociada a la tabla `communes`.

Cuando se enviaba:

```json
"commune": { "id": 3 }
```

y el ID existía en `cities` pero no estaba disponible en `communes`, el backend podía retornar 500.

## Cambio

`PostulantServiceImpl` ahora resuelve `commune.id` contra el catálogo oficial `cities` y sincroniza la tabla histórica `communes` cuando corresponde.

Reglas:

- Si `communes.id` existe y está activo, se utiliza.
- Si `communes.id` existe eliminado lógicamente, se restaura y actualiza su nombre desde `cities`.
- Si `communes.id` no existe, pero `cities.id` existe y está activo, se crea el registro equivalente en `communes`.
- Si el ID no existe o está inactivo en `cities`, responde 404 controlado en vez de 500.

## SQL

Ejecutar una vez:

```bash
sudo mysql -u root -p demanda_drogas < sql/2026_08_20_sync_communes_from_cities_for_postulants.sql
```

## Prueba

```bash
curl -i -X POST http://localhost:8095/api/v1/postulants \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "rut": "14.036.818-0",
    "firstName": "Sofia",
    "firstLastName": "Bustamante",
    "secondLastName": "Prueba",
    "birthdate": "1990-01-01",
    "commune": { "id": 3 }
  }'
```

El resultado esperado es 200/201 con:

```json
"commune": {
  "id": 3,
  "name": "Porvenir"
}
```
