# Ajuste bandeja priorizada y dashboard de referencias - 2026-07-28

## Requerimiento corregido

Se realizan dos correcciones sobre los ajustes previos:

1. En `GET /api/v1/demand/episodes/prioritized`, los campos:
   - `lastManagement`
   - `lastManagementDate`
   - `lastManagementTime`

   ahora se calculan exclusivamente con eventos pertenecientes a la etapa vigente (`current=true`).

   Si la etapa actual no tiene gestiones/eventos asociados, los tres campos retornan `null`.

2. En `GET /api/v1/demand/dashboard/supervisor/programs/references`, los campos:
   - `averageDaysBeforeReference`
   - `referenceReasons`

   ahora se agrupan por programa origen, es decir, por el programa que realizó la referencia.

## Comportamiento esperado

- El programa destino mantiene el conteo de `receivedReferences`.
- El programa origen mantiene el conteo de `sentReferences`.
- El promedio de días antes de referir se calcula para el programa origen.
- Los motivos de referencia se agrupan para el programa origen.
- Si un programa solo recibe referencias y no envía, su promedio será `0.0` y sus motivos estarán vacíos.

## Ejemplo esperado

Para una referencia desde PAI Adulto Magallanes hacia PAB Thomas Fenton:

- PAI Adulto Magallanes:
  - `sentReferences: 1`
  - `averageDaysBeforeReference: 10.0`
  - `referenceReasons`: incluye el motivo registrado.

- PAB Thomas Fenton:
  - `receivedReferences: 1`
  - `averageDaysBeforeReference: 0.0`
  - `referenceReasons: []`

## SQL

No requiere cambios SQL.
