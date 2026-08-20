# Ajuste búsqueda bandeja priorizada - 2026-08-20

## Endpoint ajustado

`GET /api/v1/demand/episodes/prioritized`

Se incorpora el parámetro opcional `search`, combinable con:

- `page`
- `size`
- `programId`
- `stateCode`
- `resultCode`
- `sort`

## Campos considerados

La búsqueda filtra demandas por datos del postulante asociado al episodio:

- RUN/RUT
- nombre
- primer apellido
- segundo apellido
- nombre completo

## Reglas

- Búsqueda parcial.
- No distingue mayúsculas/minúsculas.
- Tolera acentos en nombres.
- Tolera formato del RUN/RUT: `140368180` encuentra `14.036.818-0` y viceversa.
- Mantiene la estructura paginada existente.
- Se aplica antes de ordenar y paginar la respuesta.

## Ejemplos

```http
GET /api/v1/demand/episodes/prioritized?search=Sofia
GET /api/v1/demand/episodes/prioritized?search=14.036.818-0
GET /api/v1/demand/episodes/prioritized?search=140368180
GET /api/v1/demand/episodes/prioritized?search=Bustamante
GET /api/v1/demand/episodes/prioritized?programId=2&stateCode=EN_TRAMITE&search=Sofia&page=0&size=20&sort=originalRequestDate,desc
```
