# Ajuste prioritized: tercera entrevista y suggestedAction por etapa actual

Fecha: 2026-08-03

## Endpoint modificado

`GET /api/v1/demand/episodes/prioritized`

## Cambios

1. Se agregan siempre en `PrioritizedEpisodeDTO` los campos:
   - `firstCitationThirdInterviewDate`
   - `secondCitationThirdInterviewDate`

   Cuando no exista registro, se informan como `null`.

2. Se agrega soporte de ordenamiento para:
   - `sort=firstCitationThirdInterviewDate,asc|desc`
   - `sort=secondCitationThirdInterviewDate,asc|desc`

3. `suggestedAction` se calcula utilizando únicamente eventos de la etapa vigente resuelta por `episode.currentStageId`.

4. La secuencia considera:
   - Sin C1-E1: `Programar primera citación a primera entrevista`.
   - C1-E1 con inasistencia y sin C2-E1: `Programar segunda citación a primera entrevista`.
   - Primera entrevista completada y sin C1-E2: `Programar primera citación a segunda entrevista`.
   - C1-E2 con inasistencia y sin C2-E2: `Programar segunda citación a segunda entrevista`.
   - Segunda entrevista completada y sin C1-E3: `Programar primera citación a tercera entrevista`.
   - C1-E3 con inasistencia y sin C2-E3: `Programar segunda citación a tercera entrevista`.
   - Tercera entrevista completada y sin retroalimentación: `Registrar retroalimentación`.

5. Se agregan al seeder los tipos de citación:
   - `PRIMERA_CITACION_TERCERA_ENTREVISTA`
   - `SEGUNDA_CITACION_TERCERA_ENTREVISTA`

## SQL

Aplicar una vez:

`sql/2026_08_03_citation_types_third_interview.sql`
