# Ajuste bandeja priorizada - 2026-07-27

## Endpoint modificado

`GET /api/v1/demand/episodes/prioritized`

## Nuevos campos por episodio

Se agregan al `PrioritizedEpisodeDTO`:

- `firstCitationFirstInterviewDate`
- `secondCitationFirstInterviewDate`
- `firstCitationSecondInterviewDate`
- `secondCitationSecondInterviewDate`
- `optionalInterviewDate`
- `feedbackDate`
- `closureDate`
- `biopsychosocialCommitmentCode`
- `lastManagementDate`
- `lastManagementTime`

Las fechas sin registro se devuelven como `null`.

## Origen de datos

- Los campos de citación se obtienen desde eventos `CITACION` según `citationType.code`.
- `feedbackDate` y `biopsychosocialCommitmentCode` se obtienen desde el último evento `RETROALIMENTACION` registrado.
- `closureDate` se obtiene desde `episodes.closed_at`.
- `lastManagementDate` y `lastManagementTime` se obtienen desde el último evento del episodio.
- Se conserva `relatedEventId` en eventos longitudinales; no se modifica la relación entre citación y asistencia.

## Ordenamiento

El endpoint permite `sort=campo,asc|desc` sobre todos los campos de la bandeja priorizada, incluidos los campos calculados.

Campos soportados principales:

- `episodeId`
- `episodeCode`
- `rut`
- `personName`
- `currentProgram` / `currentProgramName`
- `currentProgramId`
- `originalRequestDate`
- `accumulatedDays`
- `semaphoreColor`
- `stateCode`
- `resultCode`
- `lastManagement`
- `lastManagementDate`
- `lastManagementTime`
- `firstCitationFirstInterviewDate`
- `secondCitationFirstInterviewDate`
- `firstCitationSecondInterviewDate`
- `secondCitationSecondInterviewDate`
- `optionalInterviewDate`
- `feedbackDate`
- `closureDate`
- `biopsychosocialCommitmentCode`
- `suggestedAction`

El orden ascendente para `biopsychosocialCommitmentCode` es:

1. `SEVERO`
2. `MODERADO`
3. `LEVE`
4. `null`

En orden descendente se invierte el orden de los valores informados, manteniendo `null` al final.

## Cambio de texto

Se cambia la sugerencia:

- Antes: `Registrar egreso cuando corresponda`
- Ahora: `Registrar cierre cuando corresponda`
