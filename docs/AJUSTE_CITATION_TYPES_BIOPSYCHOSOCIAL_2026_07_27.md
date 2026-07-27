# Ajuste backend Gestión de Demanda — citation_types y compromiso biopsicosocial

Fecha: 2026-07-27

## Resumen

Se incorporan dos catálogos independientes y dos nuevas relaciones en `episode_events`:

- `citation_types`: usado solo para eventos `CITACION`.
- `biopsychosocial_commitment_levels`: usado solo para eventos `RETROALIMENTACION`.

No se crean endpoints nuevos. Se modifican los endpoints existentes de citaciones, eventos, cierre, longitudinal y catálogos.

## Base de datos

### Tablas nuevas

#### citation_types

Campos:

- `id`
- `code`
- `name`
- `sort_order`
- `active`

Registros:

1. `PRIMERA_CITACION_PRIMERA_ENTREVISTA` — Primera citación a primera entrevista.
2. `SEGUNDA_CITACION_PRIMERA_ENTREVISTA` — Segunda citación a primera entrevista.
3. `PRIMERA_CITACION_SEGUNDA_ENTREVISTA` — Primera citación a segunda entrevista.
4. `SEGUNDA_CITACION_SEGUNDA_ENTREVISTA` — Segunda citación a segunda entrevista.
5. `ENTREVISTA_OPCIONAL` — Entrevista opcional.

#### biopsychosocial_commitment_levels

Campos:

- `id`
- `code`
- `name`
- `active`

Registros:

- `LEVE` — Leve.
- `MODERADO` — Moderado.
- `SEVERO` — Severo.

### Tabla modificada

`episode_events` agrega:

- `citation_type_id INT NULL`, FK a `citation_types(id)`.
- `biopsychosocial_commitment_level_id INT NULL`, FK a `biopsychosocial_commitment_levels(id)`.

Ambos campos permiten `NULL` y tienen índices independientes.

SQL incluido:

```bash
sql/2026_07_27_citation_types_biopsychosocial_commitment.sql
```

## API de citaciones

Endpoint:

```http
POST /api/v1/demand/episodes/{id}/citations
```

Se agrega a `CreateCitationRequest`:

```json
{
  "citationTypeCode": "PRIMERA_CITACION_PRIMERA_ENTREVISTA"
}
```

El backend resuelve el catálogo y guarda `episode_events.citation_type_id`.

`EpisodeEventDTO` ahora devuelve:

```json
"citationType": {
  "id": 1,
  "code": "PRIMERA_CITACION_PRIMERA_ENTREVISTA",
  "name": "Primera citación a primera entrevista."
}
```

## API de eventos

Endpoint:

```http
POST /api/v1/demand/episodes/{id}/events
```

Para:

```json
{
  "eventTypeCode": "RETROALIMENTACION"
}
```

Se agrega a `CreateEventRequest`:

```json
{
  "biopsychosocialCommitmentCode": "MODERADO"
}
```

Para eventos `RETROALIMENTACION` se exige:

- `eventDate`
- `eventTime`
- profesional: `professionalUserId`, `programProfessionalId` o `professionName`
- `biopsychosocialCommitmentCode`
- `resultCode`

El backend guarda `episode_events.biopsychosocial_commitment_level_id`.

`EpisodeEventDTO` ahora devuelve:

```json
"biopsychosocialCommitmentLevel": {
  "id": 2,
  "code": "MODERADO",
  "name": "Moderado"
}
```

Si `resultCode = INGRESO_TRATAMIENTO`, el backend:

- setea `episodes.entry_to_treatment_at` usando `eventDate + eventTime` de la retroalimentación.
- setea `episodes.waiting_stopped = true`.
- actualiza el resultado de la etapa vigente a `INGRESO_TRATAMIENTO`.

## API de cierre

Endpoint:

```http
POST /api/v1/demand/episodes/{id}/close
```

Se agrega a `CloseEpisodeRequest`:

```json
{
  "closureDate": "2026-07-27T10:30:00"
}
```

Si viene informado, se guarda en `episodes.closed_at` y en el `closed_at` de la etapa vigente. Si no viene informado, se usa la fecha/hora actual.

El cierre mantiene la conducta requerida:

- cierra la etapa vigente.
- marca el episodio como inactivo.
- deja estado cerrado.

## API longitudinal

Endpoint:

```http
GET /api/v1/demand/episodes/{id}/longitudinal
```

En `events[]` se agregan:

- `citationType`
- `biopsychosocialCommitmentLevel`

Se mantiene `relatedEventId` para conservar la relación citación/asistencia.

## API de catálogos

Endpoint:

```http
GET /api/v1/demand/catalogs
```

`DemandCatalogsDTO` ahora agrega:

```json
{
  "citationTypes": [],
  "biopsychosocialCommitmentLevels": []
}
```

## Ejemplos

### Crear citación

```json
{
  "stageId": 2,
  "citationDate": "2026-07-27",
  "citationTime": "10:30:00",
  "citationTypeCode": "PRIMERA_CITACION_PRIMERA_ENTREVISTA",
  "programProfessionalId": 5,
  "citationComment": "Primera entrevista agendada"
}
```

### Crear retroalimentación con ingreso a tratamiento

```json
{
  "stageId": 2,
  "eventTypeCode": "RETROALIMENTACION",
  "eventDate": "2026-07-27",
  "eventTime": "11:30:00",
  "programProfessionalId": 5,
  "biopsychosocialCommitmentCode": "MODERADO",
  "resultCode": "INGRESO_TRATAMIENTO",
  "comment": "Retroalimentación realizada e ingreso a tratamiento confirmado"
}
```
