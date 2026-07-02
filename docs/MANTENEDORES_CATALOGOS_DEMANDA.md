# Mantenedores de catálogos — Gestión Demanda

Se incorporó un mantenedor genérico para catálogos del módulo Demanda.

## Seguridad

Todos los endpoints requieren JWT y perfiles:

- `ADMIN`
- `ADMINISTRATIVO`

Header:

```http
Authorization: Bearer <TOKEN>
Content-Type: application/json
```

## Base URL

```text
http://localhost:8095/api/v1/demand/maintainers
http://10.8.74.156:8095/api/v1/demand/maintainers
https://gestiondemanda-api.dssm.cl/api/v1/demand/maintainers
```

## Catálogos soportados

| Tabla | Endpoint |
|---|---|
| `episode_types` | `/episode-types` |
| `event_types` | `/event-types` |
| `attendance_statuses` | `/attendance-statuses` |
| `closure_reasons` | `/closure-reasons` |
| `program_populations` | `/program-populations` |
| `program_modalities` | `/program-modalities` |
| `program_plans` | `/program-plans` |
| `document_types` | `/document-types` |
| `regions` | `/regions` |
| `cities` | `/cities` |
| `semaphore_rules` | `/semaphore-rules` |

También acepta nombres con guion bajo en el path, por ejemplo:

```text
/api/v1/demand/maintainers/episode_types
```

## Endpoints disponibles por catálogo

### Listar catálogos soportados

```http
GET /api/v1/demand/maintainers
```

### Listar registros

```http
GET /api/v1/demand/maintainers/{catalog}
```

Filtros opcionales:

```text
?q=texto
?active=true
?active=false
```

Ejemplo:

```http
GET /api/v1/demand/maintainers/document-types?q=informe&active=true
```

### Listar paginado

```http
GET /api/v1/demand/maintainers/{catalog}/getAllPaginated?page=0&size=20&q=texto&active=true
```

### Obtener por ID

```http
GET /api/v1/demand/maintainers/{catalog}/{id}
```

### Crear

```http
POST /api/v1/demand/maintainers/{catalog}
```

Body general:

```json
{
  "code": "INFORME_CLINICO",
  "name": "Informe clínico",
  "description": "Documento clínico asociado al episodio",
  "active": true
}
```

### Actualizar

```http
PUT /api/v1/demand/maintainers/{catalog}/{id}
```

Body:

```json
{
  "code": "INFORME_CLINICO",
  "name": "Informe clínico actualizado",
  "description": "Descripción actualizada",
  "active": true
}
```

### Eliminar lógicamente

```http
DELETE /api/v1/demand/maintainers/{catalog}/{id}
```

Usa `deleted_at` por las reglas `@SQLDelete` de cada entidad.

### Restaurar

```http
POST /api/v1/demand/maintainers/{catalog}/{id}/restore
```

Limpia `deleted_at` y deja `active = true`.

## Caso especial: cities

Para ciudades se debe enviar `regionId` cuando corresponda.

```http
POST /api/v1/demand/maintainers/cities
```

```json
{
  "code": "PUNTA_ARENAS",
  "name": "Punta Arenas",
  "description": "Ciudad de Punta Arenas",
  "regionId": 1,
  "active": true
}
```

Respuesta:

```json
{
  "id": 1,
  "code": "PUNTA_ARENAS",
  "name": "Punta Arenas",
  "description": "Ciudad de Punta Arenas",
  "active": true,
  "regionId": 1,
  "regionCode": "MAGALLANES",
  "regionName": "Región de Magallanes y de la Antártica Chilena"
}
```

## Caso especial: semaphore_rules

Aunque no estaba en la última lista, se dejó incorporado para mantener consistencia con el modelo.

```http
POST /api/v1/demand/maintainers/semaphore-rules
```

```json
{
  "colorCode": "ROJO",
  "name": "Rojo",
  "minDays": 31,
  "maxDays": 9999,
  "active": true
}
```

También acepta `code` como equivalente de `colorCode`.

## Ejemplos curl

### Crear tipo de documento

```bash
curl -X POST http://localhost:8095/api/v1/demand/maintainers/document-types \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_TOKEN" \
  -d '{
    "code": "INFORME_CLINICO",
    "name": "Informe clínico",
    "description": "Documento clínico asociado al episodio",
    "active": true
  }'
```

### Crear región

```bash
curl -X POST http://localhost:8095/api/v1/demand/maintainers/regions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_TOKEN" \
  -d '{
    "code": "MAGALLANES",
    "name": "Región de Magallanes y de la Antártica Chilena",
    "description": "Región de Magallanes",
    "active": true
  }'
```

### Crear ciudad

```bash
curl -X POST http://localhost:8095/api/v1/demand/maintainers/cities \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_TOKEN" \
  -d '{
    "code": "PUNTA_ARENAS",
    "name": "Punta Arenas",
    "regionId": 1,
    "active": true
  }'
```
