# Ajuste longitudinal episodios 2026-07-21

Se detectó que `GET /api/v1/demand/episodes` autenticaba correctamente, pero los endpoints longitudinales respondían 403 incluso con el mismo Bearer token válido.

Endpoints ajustados:

- `GET /api/v1/demand/episodes/{id}/longitudinal`
- `GET /api/v1/demand/episodes/by-rut/{rut}/longitudinal`

Para evitar el falso 403 de la cadena web, estas rutas se dejan `permitAll()` en `SecurityConfig`, pero **no quedan públicas**: el `DemandController` valida manualmente el JWT Bearer obligatorio mediante `RequestTokenValidator` antes de entregar información.

Si no hay token, si está vencido o si es refresh token, responde 401.
