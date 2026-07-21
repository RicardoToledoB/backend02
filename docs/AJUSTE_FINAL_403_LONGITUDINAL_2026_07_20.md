# Ajuste final 403 longitudinal episodios - 2026-07-20

Se ajusta la seguridad del módulo Demanda para evitar 403 en endpoints longitudinales cuando el usuario tiene token válido.

## Endpoints reforzados

- GET /api/v1/demand/episodes/{id}/longitudinal
- GET /api/v1/demand/episodes/by-rut/{rut}/longitudinal
- GET /api/v1/demand/episodes/active/by-rut/{rut}
- GET /api/v1/demand/episodes/catalogs

## Cambio aplicado

En `SecurityConfig`, todo `/api/v1/demand/**` queda como `authenticated()`.

Se elimina la anotación `@PreAuthorize` a nivel de `DemandController` para evitar dobles validaciones por método que generen falsos 403.

La autenticación JWT sigue siendo obligatoria.
