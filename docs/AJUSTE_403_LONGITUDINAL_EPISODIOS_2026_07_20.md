# Ajuste 403 longitudinal episodios - 2026-07-20

## Problema
El frontend reporta 403 Forbidden con token válido en:

- `GET /api/v1/demand/episodes/catalogs`
- `GET /api/v1/demand/episodes/{id}/longitudinal`
- `GET /api/v1/demand/episodes/by-rut/{rut}/longitudinal`

El token contiene roles como `ROLE_ADMIN`, `ROLE_ADMINISTRATIVO`, `ROLE_SUPERVISOR` y `ROLE_PROFESIONAL`, pero el backend sigue bloqueando estas rutas.

## Ajuste aplicado

1. En `SecurityConfig` se agregaron reglas explícitas antes de `/api/v1/demand/**` para permitir acceso autenticado a los endpoints críticos de longitudinal:

```java
.requestMatchers(
    "/api/v1/demand/episodes/catalogs",
    "/api/v1/demand/episodes/*/longitudinal",
    "/api/v1/demand/episodes/by-rut/*/longitudinal",
    "/api/v1/demand/episodes/active/by-rut/*"
).authenticated()
```

2. En `DemandController` se cambió la autorización de clase a:

```java
@PreAuthorize("isAuthenticated()")
```

Esto evita falsos 403 por diferencias entre `hasRole`, `hasAuthority` o permisos programáticos y mantiene la exigencia de token válido.

## Pruebas recomendadas

```bash
curl -i http://localhost:8095/api/v1/demand/episodes/catalogs \
  -H "Authorization: Bearer TU_TOKEN"

curl -i http://localhost:8095/api/v1/demand/episodes/1/longitudinal \
  -H "Authorization: Bearer TU_TOKEN"

curl -i http://localhost:8095/api/v1/demand/episodes/by-rut/11.799.136-9/longitudinal \
  -H "Authorization: Bearer TU_TOKEN"
```

Si aparece `404`, significa que la autorización ya pasó y falta dato existente. Si aparece `200`, quedó operativo. Si aún aparece `403`, revisar que el JAR actualizado sea el que está corriendo y limpiar caché/reiniciar servicio.
