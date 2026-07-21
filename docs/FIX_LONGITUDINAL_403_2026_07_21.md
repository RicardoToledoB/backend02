# Fix longitudinal 403 - 2026-07-21

Se corrige bloqueo 403 específico de:

- GET /api/v1/demand/episodes/{id}/longitudinal
- GET /api/v1/demand/episodes/by-rut/{rut}/longitudinal

Diagnóstico en servidor:

- GET /api/v1/demand/episodes respondía 200 con el mismo Bearer token.
- Longitudinal respondía 403 incluso por localhost.
- El código aún tenía @PreAuthorize en los métodos longitudinales, pese a que SecurityConfig dejaba esas rutas como permitAll para validación manual.

Cambio aplicado:

- Se eliminan @PreAuthorize del DemandController.
- Los dos endpoints longitudinales mantienen validación obligatoria del Bearer token mediante RequestTokenValidator.
- No quedan públicos: sin token válido responden 401.

Prueba:

```bash
TOKEN=$(curl -s -X POST http://localhost:8095/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@demo.com","password":"Admin123$"}' \
  | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')

curl -i http://localhost:8095/api/v1/demand/episodes/1/longitudinal \
  -H "Authorization: Bearer $TOKEN"

curl -i "http://localhost:8095/api/v1/demand/episodes/by-rut/11.799.136-9/longitudinal" \
  -H "Authorization: Bearer $TOKEN"
```

Resultado esperado:

- 200 si existe la información.
- 404 si no existe.
- 401 si no se envía token o está vencido.
- No debe responder 403 por seguridad.
