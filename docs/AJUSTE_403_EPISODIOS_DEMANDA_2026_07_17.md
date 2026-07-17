# Ajuste 403 módulo episodios Demanda - 2026-07-17

## Problema reportado

Con token válido que contiene `ROLE_ADMIN`, `ROLE_ADMINISTRATIVO`, `ROLE_SUPERVISOR`, `ROLE_PROFESIONAL` y `programIds [1,2]`, el frontend puede consultar:

- `GET /api/v1/programs` → 200 OK

pero recibe 403 en:

- `GET /api/v1/demand/episodes/catalogs`
- `GET /api/v1/demand/episodes/1/longitudinal`
- `GET /api/v1/demand/episodes/by-rut/{rut}/longitudinal`
- `POST /api/v1/demand/episodes/{episodeId}/events`
- `POST /api/v1/demand/episodes/{episodeId}/references`

## Ajustes realizados

### 1. SecurityFilterChain

Se autorizó explícitamente todo el módulo de Demanda:

```java
.requestMatchers("/api/v1/demand/**")
.hasAnyAuthority("ROLE_ADMIN", "ROLE_ADMINISTRATIVO", "ROLE_SUPERVISOR", "ROLE_PROFESIONAL")
```

Esto cubre episodios, longitudinal, eventos, referencias, dashboard, documentos, catálogos y endpoints auxiliares.

### 2. DemandController

Se reemplazó la autorización genérica:

```java
@PreAuthorize("isAuthenticated()")
```

por autorización explícita:

```java
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_ADMINISTRATIVO','ROLE_SUPERVISOR','ROLE_PROFESIONAL')")
```

### 3. Alias de catálogos de episodios

Se agregó compatibilidad con la ruta usada por frontend:

```http
GET /api/v1/demand/episodes/catalogs
```

La ruta existente sigue funcionando:

```http
GET /api/v1/demand/catalogs
```

Ambas devuelven el mismo DTO de catálogos de demanda.

## Endpoints esperados después del ajuste

```http
GET  /api/v1/demand/episodes/catalogs
GET  /api/v1/demand/catalogs
GET  /api/v1/demand/episodes/{id}/longitudinal
GET  /api/v1/demand/episodes/by-rut/{rut}/longitudinal
GET  /api/v1/demand/episodes/active/by-rut/{rut}
POST /api/v1/demand/episodes/{id}/events
POST /api/v1/demand/episodes/{id}/references
```

Todos deben aceptar token Bearer con cualquiera de estos roles:

- `ROLE_ADMIN`
- `ROLE_ADMINISTRATIVO`
- `ROLE_SUPERVISOR`
- `ROLE_PROFESIONAL`

## Aplicación en servidor

```bash
cd /var/www/html/gestiondemanda-api
./mvnw clean package -DskipTests
sudo systemctl restart gestiondemanda-api
sudo systemctl status gestiondemanda-api
```

## Pruebas sugeridas

```bash
curl -i http://localhost:8095/api/v1/demand/episodes/catalogs \
  -H "Authorization: Bearer TU_TOKEN"

curl -i http://localhost:8095/api/v1/demand/episodes/1/longitudinal \
  -H "Authorization: Bearer TU_TOKEN"

curl -i http://localhost:8095/api/v1/demand/episodes/by-rut/11.799.136-9/longitudinal \
  -H "Authorization: Bearer TU_TOKEN"
```
