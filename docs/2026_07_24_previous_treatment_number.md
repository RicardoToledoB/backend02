# Ajuste previousTreatmentNumber / previous_treatment_number

## Contexto

En el modelo antiguo el número de tratamientos previos se almacenaba en `registers.number_tto`.

En el nuevo modelo se incorpora en `episodes.previous_treatment_number` y se expone como `previousTreatmentNumber`.

## Cambios incluidos

- Nueva columna `episodes.previous_treatment_number INT NOT NULL DEFAULT 0`.
- Restricción mínima a nivel de base de datos: `CHECK (previous_treatment_number >= 0)`.
- Campo agregado en `EpisodeEntity`.
- Campo agregado en `CreateEpisodeRequest` como `previousTreatmentNumber`.
- Validación `@Min(0)` en el request.
- Normalización en backend: si no se envía, se guarda `0`; si llega negativo, se rechaza con `400`.
- Campo incluido en `EpisodeDTO`.
- Campo incluido automáticamente en ficha longitudinal porque la longitudinal retorna `activeEpisode` y lista `episodes` como `EpisodeDTO`.
- Swagger/OpenAPI actualizado mediante anotaciones `@Schema` en request y DTO.

## Ejemplo de creación

```json
{
  "postulantId": 1,
  "episodeTypeId": 1,
  "previousTreatmentNumber": 2,
  "originalRequestDate": "2026-07-24",
  "initialProgramId": 1,
  "initialObservation": "Episodio creado con número de tratamientos previos informado manualmente."
}
```

## Compatibilidad

El campo reemplaza funcionalmente a `registers.number_tto`. El frontend puede sugerir un valor calculado según episodios registrados en el nuevo sistema, pero el usuario debe poder corregirlo manualmente antes de guardar.
