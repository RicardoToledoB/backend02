# Ajustes backend - Tiempo servidor, conv_prevs, searchByRut y episodio activo

Fecha: 2026-07-06
Proyecto: RDA-SM Gestión Demanda

## 1. Hora del servidor

Se agregó endpoint público:

```http
GET /api/v1/time/server
```

Respuesta:

```json
{
  "epochMillis": 1783092600000,
  "dateTime": "2026-07-03T12:30:00-03:00"
}
```

- `epochMillis`: hora Unix del servidor en milisegundos.
- `dateTime`: fecha/hora ISO con zona horaria explícita.
- Zona horaria por defecto: `America/Punta_Arenas`.
- Configurable con `APP_TIME_ZONE` o `app.time.zone`.

## 2. Mantenedor Convenios previsionales

Se agregó/habilitó:

```http
GET    /api/v1/conv_prevs
GET    /api/v1/conv_prevs/all
GET    /api/v1/conv_prevs/getAllPaginated
GET    /api/v1/conv_prevs/deleted
GET    /api/v1/conv_prevs/findById/{id}
GET    /api/v1/conv_prevs/{id}
POST   /api/v1/conv_prevs
PUT    /api/v1/conv_prevs/{id}
DELETE /api/v1/conv_prevs/{id}
DELETE /api/v1/conv_prevs/softDelete/{id}
PATCH  /api/v1/conv_prevs/restore/{id}
PUT    /api/v1/conv_prevs/restore/{id}
POST   /api/v1/conv_prevs/{id}/restore
```

Permisos habilitados:

- `ROLE_ADMIN`
- `ROLE_ADMINISTRATIVO`
- `ROLE_SUPERVISOR`

Body recomendado:

```json
{
  "code": "FONASA_A",
  "name": "FONASA A",
  "description": "Convenio previsional FONASA tramo A",
  "active": true,
  "intPrevId": 1
}
```

También acepta estructura padre anidada:

```json
{
  "code": "FONASA_A",
  "name": "FONASA A",
  "active": true,
  "intPrev": {
    "id": 1
  }
}
```

## 3. Búsqueda de postulantes por RUN

Se habilitó:

```http
GET /api/v1/postulants/searchByRut?rut=11.799.136-9&page=0&size=1
GET /api/v1/postulants/searchByRut?rut=117991369&page=0&size=1
```

Permisos habilitados:

- `ROLE_ADMIN`
- `ROLE_ADMINISTRATIVO`
- `ROLE_SUPERVISOR`
- `ROLE_PROFESIONAL`

La búsqueda compara RUN formateado y RUN limpio, ignorando puntos y guion.

## 4. Episodio activo por RUN

Se reforzó permiso para:

```http
GET /api/v1/demand/episodes/active/by-rut/{rut}
```

Permisos habilitados:

- `ROLE_ADMIN`
- `ROLE_ADMINISTRATIVO`
- `ROLE_SUPERVISOR`
- `ROLE_PROFESIONAL`

También se mejoró la comparación de RUN para aceptar formato con puntos/guion y sin formato.

## 5. Seguridad

Se actualizó `SecurityConfig` para:

```text
/api/v1/time/server                      -> público
/api/v1/conv_prevs/**                    -> ADMIN / ADMINISTRATIVO / SUPERVISOR
/api/v1/postulants/searchByRut           -> ADMIN / ADMINISTRATIVO / SUPERVISOR / PROFESIONAL
/api/v1/demand/episodes/active/by-rut/** -> ADMIN / ADMINISTRATIVO / SUPERVISOR / PROFESIONAL
```

## 6. Pruebas sugeridas

```bash
curl -i http://localhost:8095/api/v1/time/server
```

```bash
curl -i -X GET "http://localhost:8095/api/v1/conv_prevs/all" \
  -H "Authorization: Bearer TU_TOKEN"
```

```bash
curl -i -X GET "http://localhost:8095/api/v1/postulants/searchByRut?rut=11.799.136-9&page=0&size=1" \
  -H "Authorization: Bearer TU_TOKEN"
```

```bash
curl -i -X GET "http://localhost:8095/api/v1/demand/episodes/active/by-rut/11.799.136-9" \
  -H "Authorization: Bearer TU_TOKEN"
```
