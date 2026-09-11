# Ajuste users_roles lectura SUPERVISOR - 2026-09-11

## Requerimiento

Habilitar acceso de solo lectura para el perfil SUPERVISOR en las APIs de relaciones usuario-rol necesarias para el Directorio institucional:

- GET /api/v1/users_roles
- GET /api/v1/users_roles/all
- GET /api/v1/users_roles/user/{id}
- GET /api/v1/users_roles/{id}
- GET /api/v1/users_roles/getAllPaginated
- GET /api/v1/users_roles/deleted

## Regla aplicada

- ADMIN y ADMINISTRATIVO mantienen permisos de lectura y escritura.
- SUPERVISOR queda habilitado solo para operaciones GET.
- POST, PUT, DELETE y restore permanecen restringidos a ADMIN y ADMINISTRATIVO.
- No se crean roles nuevos ni tablas nuevas.
- No requiere SQL.

## Archivos modificados

- src/main/java/com/cosam/project01/controller/UserRoleController.java
- src/main/java/com/cosam/project01/security/SecurityConfig.java
