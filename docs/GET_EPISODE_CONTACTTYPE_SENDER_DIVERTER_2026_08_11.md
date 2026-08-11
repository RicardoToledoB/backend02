# Ajuste GET /api/v1/demand/episodes/{id}

Fecha: 2026-08-11

## Objetivo

Incluir en la respuesta de `GET /api/v1/demand/episodes/{id}` los datos asociados del episodio:

- `contactType`
- `sender`
- `diverter`

Estos campos ya existían en `EpisodeEntity` y podían ser informados al crear el episodio mediante:

- `contactTypeId`
- `senderId`
- `diverterId`

El ajuste incorpora dichos campos en `EpisodeDTO` y los mapea desde `DemandService.toEpisodeDTO`.

## Respuesta esperada

```json
{
  "id": 7,
  "contactType": { "id": 1, "code": null, "name": "..." },
  "sender": { "id": 1, "code": null, "name": "..." },
  "diverter": { "id": 1, "code": null, "name": "..." }
}
```

Si el episodio no tiene alguno de estos datos, el campo retorna `null`.

## SQL

No requiere SQL nuevo.
