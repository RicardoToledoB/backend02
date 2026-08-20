# Ajuste `search` en bandeja priorizada — 2026-08-20

## Endpoint modificado

`GET /api/v1/demand/episodes/prioritized`

Se agrega el parámetro opcional:

```http
search=<texto>
```

## Alcance

El filtro permite consultar demandas desde una caja única, buscando sobre los datos del demandante/persona asociados al episodio:

- RUN/RUT, con o sin puntos y guion.
- Nombre.
- Primer apellido.
- Segundo apellido.
- Nombre completo.

## Reglas

- Búsqueda parcial.
- Sin distinguir mayúsculas/minúsculas.
- Tolerante a acentos en nombres y apellidos.
- Tolerante al formato del RUN/RUT. Por ejemplo, `140368180` encuentra `14.036.818-0`.
- Compatible con filtros existentes: `page`, `size`, `programId`, `stateCode`, `resultCode` y `sort`.
- Mantiene la respuesta paginada actual.

## Ejemplos

```http
GET /api/v1/demand/episodes/prioritized?search=Sofia
GET /api/v1/demand/episodes/prioritized?search=14.036.818-0
GET /api/v1/demand/episodes/prioritized?search=140368180
GET /api/v1/demand/episodes/prioritized?search=Bustamante
GET /api/v1/demand/episodes/prioritized?programId=2&stateCode=EN_TRAMITE&search=Sofia&sort=lastManagementDate,desc
```
