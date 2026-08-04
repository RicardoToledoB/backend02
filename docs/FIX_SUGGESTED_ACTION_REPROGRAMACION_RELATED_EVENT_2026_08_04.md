# Fix suggestedAction por asistencia asociada a citación

Fecha: 2026-08-04

## Requerimiento

En `GET /api/v1/demand/episodes/prioritized`, corregir `suggestedAction` para considerar el estado de asistencia asociado a cada citación mediante `relatedEventId`.

Si la asistencia asociada a la citación tiene estado:

- `CANCELA_PROGRAMA`
- `REPROGRAMADA`

el sistema debe sugerir reprogramar el mismo tipo de citación, sin avanzar a registrar asistencia ni habilitar la segunda citación.

Al crear una nueva citación debe conservarse la anterior y generarse un nuevo evento con el mismo `citationTypeCode`, nueva fecha y nuevo `id`.

## Ajuste implementado

Se modificó `DemandService.suggestedInterviewWorkflowAction(...)` para evaluar, por cada citación de la etapa actual, el último estado de asistencia relacionado por `relatedEventId`.

La evaluación ahora distingue:

- `SE_PRESENTO`: entrevista completada.
- `NO_SE_PRESENTO`: habilita segunda citación del mismo bloque cuando corresponda.
- `CANCELA_PROGRAMA` o `REPROGRAMADA`: sugiere reprogramar el mismo tipo de citación.
- `AGENDADO` o sin asistencia asociada: sugiere registrar asistencia.

## Reglas de salida

Ejemplos:

- C1-E1 con asistencia `REPROGRAMADA`:
  - `Reprogramar primera citación a primera entrevista`

- C1-E1 con asistencia `CANCELA_PROGRAMA`:
  - `Reprogramar primera citación a primera entrevista`

- C1-E2 con asistencia `REPROGRAMADA`:
  - `Reprogramar primera citación a segunda entrevista`

- C1-E3 con asistencia `CANCELA_PROGRAMA`:
  - `Reprogramar primera citación a tercera entrevista`

## Consideración de datos

No se elimina ni se reemplaza la citación anterior. La nueva citación se registra como un nuevo evento `CITACION` con el mismo `citationTypeCode`, lo que permite conservar trazabilidad histórica.

## SQL

No requiere SQL nuevo.
