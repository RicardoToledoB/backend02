# Ajuste final 403 longitudinal episodios

Se reforzaron ambas rutas reportadas por frontend:

- `GET /api/v1/demand/episodes/{id}/longitudinal`
- `GET /api/v1/demand/episodes/by-rut/{rut}/longitudinal`

Cambios:

1. `SecurityConfig` usa `AntPathRequestMatcher` explícito para evitar problemas de match con puntos, guiones o rutas con RUN formateado.
2. Ambas rutas quedan con `authenticated()` y no con validación rígida de rol/programa.
3. `JwtAuthenticationFilter` acepta `Authorization`, `X-Forwarded-Authorization` y `X-Authorization`, útil si un proxy no reenvía correctamente `Authorization`.
4. Se agrega endpoint de diagnóstico autenticado:

```http
GET /api/v1/security/whoami
Authorization: Bearer <token>
```

Si `/api/v1/security/whoami` responde 403 por dominio pero funciona por `localhost:8095`, el problema está en Nginx/Apache/proxy y se debe reenviar el header Authorization:

```nginx
proxy_set_header Authorization $http_authorization;
proxy_set_header X-Forwarded-Authorization $http_authorization;
```

Pruebas recomendadas:

```bash
TOKEN=$(curl -s -X POST http://localhost:8095/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@demo.com","password":"Admin123$"}' \
  | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')

curl -i http://localhost:8095/api/v1/security/whoami \
  -H "Authorization: Bearer $TOKEN"

curl -i http://localhost:8095/api/v1/demand/episodes/1/longitudinal \
  -H "Authorization: Bearer $TOKEN"

curl -i "http://localhost:8095/api/v1/demand/episodes/by-rut/11.799.136-9/longitudinal" \
  -H "Authorization: Bearer $TOKEN"
```
