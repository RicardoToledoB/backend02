# Fix corrección administrativa: CREATE citación con eventTime objeto

Fecha: 2026-09-02

## Problema

`PUT /api/v1/demand/episodes/{id}/administrative-correction` respondía `400 Bad Request`
al crear una citación cuando `eventTime` venía serializado como objeto:

```json
"eventTime": {
  "hour": 8,
  "minute": 0,
  "second": 0,
  "nano": 0
}
```

El DTO usaba `LocalTime`, que por defecto acepta strings ISO (`"08:00:00"`), pero no siempre
acepta el objeto generado por algunos frontends.

## Ajuste

Se agregó `FlexibleLocalTimeDeserializer`, compatible con:

- `"08:00"`
- `"08:00:00"`
- `[8,0]` o `[8,0,0]`
- `{ "hour": 8, "minute": 0, "second": 0, "nano": 0 }`

Se aplicó a:

- `AdministrativeEventCorrectionDTO.eventTime`
- `CreateEventRequest.eventTime`
- `RegisterAttendanceRequest.eventTime`
- `CreateCitationRequest.citationTime`

Además, al crear una citación por corrección administrativa, si no viene `attendanceStatus`, se asigna
`AGENDADO` por defecto, manteniendo coherencia con el endpoint regular de citaciones.

## SQL

No requiere SQL.
