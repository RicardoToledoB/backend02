# Ajuste Dashboard Supervisor por Programas y Alertas Longitudinales — 2026-07-27

## Endpoint nuevo

Se agrega:

```http
GET /api/v1/demand/dashboard/supervisor/programs
```

Entrega estadísticas consolidadas por programa:

- `programId`
- `programName`
- `activeDemands`
- `averageAccumulatedDays`
- `redCases`
- `withoutFirstCitation`
- `withoutFeedback`
- `severeCommitmentCases`
- `pendingReferences`
- `pendingClosures`
- `openAlerts`

## Criterios aplicados

- `activeDemands`: episodios activos, sin cierre, agrupados por `currentProgram`.
- `averageAccumulatedDays`: promedio de días acumulados de los episodios activos del programa.
- `redCases`: episodios cuyo semáforo calculado es `ROJO`.
- `withoutFirstCitation`: episodios sin primera citación de primera entrevista. Para compatibilidad, una citación antigua sin `citationType` también se considera primera citación.
- `withoutFeedback`: episodios sin evento `RETROALIMENTACION`.
- `severeCommitmentCases`: episodios cuya última retroalimentación tiene compromiso biopsicosocial `SEVERO`.
- `pendingReferences`: episodios activos con `resultCode = REFERENCIA`.
- `pendingClosures`: episodios activos con `entryToTreatmentAt` informado o `resultCode = INGRESO_TRATAMIENTO`, sin cierre.
- `openAlerts`: alertas con estado `ABIERTA`, `ACTIVA` u `OPEN`.

## Longitudinal

Se ajusta:

```http
GET /api/v1/demand/episodes/{id}/longitudinal
```

Ahora incluye:

- `openAlertCount`
- `alerts[].type`
- `alerts[].priority`
- `alerts[].status`
- `alerts[].nextReviewDate`
- `alerts[].responsibleUserName`

Se mantienen los campos anteriores de `EpisodeAlertDTO` para compatibilidad.

## SQL

No requiere SQL nuevo. Usa las tablas y campos ya incorporados previamente:

- `episode_alerts`
- `episode_events.citation_type_id`
- `episode_events.biopsychosocial_commitment_level_id`
- `citation_types`
- `biopsychosocial_commitment_levels`
