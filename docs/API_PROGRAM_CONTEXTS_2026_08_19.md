# API contexto de programa por episodio — 2026-08-19

## Endpoint nuevo

`POST /api/v1/demand/episodes/program-contexts`

Permite consultar, para uno o varios episodios, la etapa asociada a un programa específico aunque ese programa ya no sea el responsable actual de la demanda.

## Request

```json
{
  "programId": 2,
  "episodeIds": [13, 12, 9, 11]
}
```

## Response

Devuelve una fila por episodio solicitado.

```json
[
  {
    "episodeId": 13,
    "programId": 2,
    "programName": "PAB THOMAS FENTON",
    "stageId": 11,
    "stageStateCode": "EN_TRAMITE",
    "stageResultCode": "AUN_SIN_RESULTADO",
    "receivedAt": "2026-08-14T10:30:00",
    "closureDate": null,
    "closed": false
  }
]
```

## Regla aplicada

- El backend busca la última etapa del `programId` indicado dentro de cada episodio, ordenando por `stageOrder DESC, id DESC`.
- No exige que la etapa sea `current=true`.
- Si el programa no tiene etapa en un episodio, retorna la fila con `stageId`, estados y fechas en `null`.
- No calcula si corresponde gestionar, solo lectura ni acción operativa. Esa decisión queda centralizada en el frontend.
