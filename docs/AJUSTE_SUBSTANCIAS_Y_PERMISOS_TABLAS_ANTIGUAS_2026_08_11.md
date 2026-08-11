# Ajuste sustancias por episodio y permisos de catálogos antiguos - 2026-08-11

## Sustancias por episodio

Se completó el manejo de sustancias asociadas a episodios.

### Endpoints

- `GET /api/v1/demand/episodes/{id}/substances`
  - Lista la sustancia principal y todas las secundarias.
  - Ordena dejando primero la principal y luego por `useOrder`.
  - Retorna lista vacía si el episodio no tiene sustancias.

- `POST /api/v1/demand/episodes/{id}/substances`
  - Se mantiene endpoint existente.
  - Valida que no se duplique una sustancia dentro del mismo episodio.
  - Si se registra una nueva sustancia como principal, se desmarca cualquier otra principal previa.
  - Si es la primera sustancia del episodio y no se indica `primarySubstance`, queda como principal.
  - Si no se indica `useOrder`, se calcula automáticamente.

- `PUT /api/v1/demand/episodes/{episodeId}/substances/{substanceAssociationId}`
  - Permite modificar sustancia, nivel, principal/secundaria, orden y observación.
  - Evita duplicar sustancias dentro del episodio.
  - Si se marca como principal, se desmarca la anterior.
  - No permite dejar el episodio sin sustancia principal cuando existen sustancias asociadas.

- `DELETE /api/v1/demand/episodes/{episodeId}/substances/{substanceAssociationId}`
  - Elimina la sustancia asociada al episodio mediante soft delete.
  - Si se elimina la principal y quedan secundarias, promueve la primera según `useOrder` como nueva principal.

## Permisos para tablas antiguas

Se ampliaron permisos para evitar 403 en endpoints antiguos usados como catálogos por Angular.

Roles permitidos:

- `ROLE_ADMIN`
- `ROLE_ADMINISTRATIVO`
- `ROLE_SUPERVISOR`
- `ROLE_PROFESIONAL`
- `ROLE_EJECUTIVO`

Controladores ajustados:

- `/api/v1/programs`
- `/api/v1/sexs`
- `/api/v1/senders`
- `/api/v1/diverters`
- `/api/v1/not_revelants`
- `/api/v1/substances`
- `/api/v1/contacts_types`
- `/api/v1/users`
- `/api/v1/users_programs`

## SQL

No requiere SQL nuevo.
