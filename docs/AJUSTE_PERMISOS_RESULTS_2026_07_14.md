# Ajuste permisos backend - Mantenedor Results

Fecha: 2026-07-14

## Problema detectado

El frontend confirmó que el endpoint:

```http
PUT /api/v1/results/{id}
```

respondía `403 Forbidden` aun usando Bearer Token válido.

## Causa probable

El controlador `ResultController` tenía autorización de método restringida con:

```java
@PreAuthorize("hasRole('ADMIN')")
```

y la ruta `/api/v1/results/**` no estaba incluida explícitamente en la regla de autorización del `SecurityFilterChain` junto a los demás mantenedores.

## Ajuste aplicado

### SecurityConfig

Se agregó explícitamente:

```text
/api/v1/results/**
```

a los mantenedores autorizados para:

```text
ROLE_ADMIN
ROLE_ADMINISTRATIVO
ROLE_SUPERVISOR
```

### ResultController

Se cambió la autorización de clase a:

```java
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ADMINISTRATIVO', 'ROLE_SUPERVISOR')")
```

Con esto quedan habilitadas las operaciones:

```http
GET    /api/v1/results
GET    /api/v1/results/all
GET    /api/v1/results/deleted
GET    /api/v1/results/getAllPaginated
GET    /api/v1/results/{id}
POST   /api/v1/results
PUT    /api/v1/results/{id}
DELETE /api/v1/results/{id}
POST   /api/v1/results/{id}/restore
```

## Prueba sugerida

```bash
curl -i -X PUT http://localhost:8095/api/v1/results/1 \
  -H "Authorization: Bearer TU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Resultado actualizado",
    "code": "RESULTADO_ACTUALIZADO",
    "scope": "DEMAND",
    "description": "Prueba de actualización",
    "active": true
  }'
```

Respuesta esperada:

```text
HTTP/1.1 200 OK
```
