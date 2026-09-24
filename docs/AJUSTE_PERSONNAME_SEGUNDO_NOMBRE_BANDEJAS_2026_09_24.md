# Ajuste personName en bandejas

Fecha: 2026-09-24

## Objetivo

Corregir la construcción de `personName` en las respuestas de bandeja para considerar también el campo `lastName`, utilizado en el modelo histórico como segundo nombre de la persona.

## Comportamiento anterior

`personName` se construía solo con:

- `firstName`
- `firstLastName`
- `secondLastName`

Ejemplo:

- Juan Alberto Ulloa Garcia se devolvía como Juan Ulloa Garcia.

## Comportamiento nuevo

`personName` se construye con:

- `firstName`
- `lastName`
- `firstLastName`
- `secondLastName`

Ejemplo:

- Juan Alberto Ulloa Garcia se devuelve como Juan Alberto Ulloa Garcia.

## Endpoints afectados

Este ajuste aplica a todos los DTO que usan el helper interno `personName(PostulantEntity)`, incluyendo:

- `GET /api/v1/demand/episodes/prioritized`
- `GET /api/v1/demand/episodes/prioritized/stages`

## SQL

No requiere SQL.
