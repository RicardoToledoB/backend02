# Ajuste 403 listado de episodios - 2026-07-21

Se detectó que el frontend/Postman estaba consultando:

```http
GET /api/v1/demand/episodes
```

El backend tenía endpoints de creación, búsqueda por ID, longitudinal y bandeja priorizada, pero no tenía un `GET /episodes` explícito para listado general. Esto podía generar bloqueo o comportamiento no esperado en la capa de seguridad/ruteo.

## Cambios

1. Se agregó endpoint:

```http
GET /api/v1/demand/episodes
```

2. El endpoint reutiliza la lógica de bandeja priorizada:

```http
GET /api/v1/demand/episodes?programId=1&stateCode=EN_TRAMITE&resultCode=LISTA_ESPERA&page=0&size=20
```

3. Responde un `Page<PrioritizedEpisodeDTO>`.

4. Se agregó autorización explícita en `SecurityConfig`:

```java
new AntPathRequestMatcher("/api/v1/demand/episodes", "GET")
new AntPathRequestMatcher("/api/v1/demand/episodes/", "GET")
```

5. Se mantienen autenticados todos los endpoints de demanda:

```text
/api/v1/demand/**
```

## Pruebas sugeridas

```bash
TOKEN=$(curl -s -X POST http://localhost:8095/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@demo.com","password":"Admin123$"}' \
  | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')

curl -i http://localhost:8095/api/v1/demand/episodes \
  -H "Authorization: Bearer $TOKEN"

curl -i http://localhost:8095/api/v1/demand/episodes/1/longitudinal \
  -H "Authorization: Bearer $TOKEN"

curl -i "http://localhost:8095/api/v1/demand/episodes/by-rut/11.799.136-9/longitudinal" \
  -H "Authorization: Bearer $TOKEN"
```

Si por `localhost:8095` funciona y por dominio `https://gestiondemanda-api.dssm.cl` no, revisar proxy Nginx/Apache y confirmar que reenvía el header `Authorization`.
