# Ajuste explícito PreAuthorize longitudinal episodios

Se agregó `@PreAuthorize` explícito para los endpoints críticos de episodios que estaban respondiendo 403 pese a que el token era válido.

Endpoints reforzados:

- GET /api/v1/demand/episodes
- GET /api/v1/demand/episodes/{id}
- GET /api/v1/demand/episodes/{id}/longitudinal
- GET /api/v1/demand/episodes/by-rut/{rut}/longitudinal
- GET /api/v1/demand/episodes/active/by-rut/{rut}

Roles autorizados:

- ROLE_ADMIN
- ROLE_ADMINISTRATIVO
- ROLE_SUPERVISOR
- ROLE_PROFESIONAL

Si el endpoint funciona por `localhost:8095` y falla por dominio, revisar proxy/Nginx.
