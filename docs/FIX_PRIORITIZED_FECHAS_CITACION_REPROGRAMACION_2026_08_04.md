# Fix prioritized: fechas de citación reprogramadas

Fecha: 2026-08-04

## Requerimiento

En `GET /api/v1/demand/episodes/prioritized`, los campos de fecha de citación no deben tomar el primer evento histórico del `citationTypeCode` cuando una citación fue cancelada o reprogramada y luego se creó una nueva citación del mismo tipo.

Caso validado:

- Episodio `DEM-000001`.
- Existen dos eventos `PRIMERA_CITACION_SEGUNDA_ENTREVISTA`:
  - 2026-08-03, cancelada por programa.
  - 2026-08-04, nueva citación con asistencia `SE_PRESENTO`.
- `firstCitationSecondInterviewDate` debe devolver `2026-08-04`.

## Cambio aplicado

Los campos de citación de la bandeja priorizada ahora se calculan usando el evento `CITACION` más reciente de la etapa actual para cada `citationTypeCode`:

- `firstCitationFirstInterviewDate`
- `secondCitationFirstInterviewDate`
- `firstCitationSecondInterviewDate`
- `secondCitationSecondInterviewDate`
- `firstCitationThirdInterviewDate`
- `secondCitationThirdInterviewDate`
- `optionalInterviewDate`

Si no existe registro para el tipo de citación en la etapa actual, el campo retorna `null`.

## Reprogramación

La creación de una nueva citación mantiene la citación anterior y crea un nuevo evento con:

- el mismo `citationTypeCode`,
- nueva fecha,
- nuevo `id`.

Por eso la bandeja debe mostrar la fecha de la citación vigente/más reciente, no la primera fecha histórica.

## SQL

No requiere SQL nuevo.
