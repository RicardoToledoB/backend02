# Ajuste backend: purga administrativa de demandas de prueba

Fecha: 2026-07-30

## Endpoint nuevo

```http
DELETE /api/v1/demand/episodes/{episodeId}/purge
```

Operación restringida a usuarios con `ROLE_ADMIN` mediante validación manual del Bearer token.

## Objetivo

Eliminar completamente una demanda/episodio de prueba sin eliminar al demandante ni sus datos personales.

La operación elimina únicamente el agregado de demanda:

- `episode_alerts`
- `episode_audit_logs`
- `episode_documents`
- `episode_references`
- `episode_events`
- `episode_substances`
- `episode_stages`
- `episodes`

No elimina:

- `postulants`
- `contacts`
- datos previsionales
- usuarios
- profesionales
- programas
- sustancias maestras
- catálogos

## Limpieza de archivos físicos

Antes de eliminar las filas de documentos, el servicio identifica los archivos asociados al episodio desde `episode_documents.storage_path`.

Luego de la purga de base de datos, elimina los archivos físicos asociados. Por seguridad solo borra rutas que contengan:

```text
/documents/episodes/{episodeId}/
```

También intenta eliminar el directorio del episodio si queda vacío.

## Respuesta esperada

```json
{
  "episodeId": 1,
  "episodeCode": "DEM-000001",
  "postulantId": 1,
  "databasePurged": true,
  "deletedRows": {
    "episode_alerts": 0,
    "episode_audit_logs": 22,
    "episode_documents": 8,
    "episode_references": 1,
    "episode_events": 21,
    "episode_substances": 0,
    "episode_stages": 2,
    "episodes": 1
  },
  "deletedFiles": 8,
  "failedFiles": 0,
  "failedFilePaths": [],
  "skippedUnsafeFilePaths": []
}
```

## Prueba curl

```bash
TOKEN=$(curl -s -X POST http://localhost:8095/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@demo.com","password":"Admin123$"}' \
  | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')

curl -i -X DELETE http://localhost:8095/api/v1/demand/episodes/1/purge \
  -H "Authorization: Bearer $TOKEN"
```

## Validación posterior

```sql
SELECT COUNT(*) FROM episodes WHERE id = 1;
SELECT COUNT(*) FROM episode_stages WHERE episode_id = 1;
SELECT COUNT(*) FROM episode_events WHERE episode_id = 1;
SELECT COUNT(*) FROM episode_references WHERE episode_id = 1;
SELECT COUNT(*) FROM episode_documents WHERE episode_id = 1;
SELECT COUNT(*) FROM episode_audit_logs WHERE episode_id = 1;
SELECT COUNT(*) FROM episode_alerts WHERE episode_id = 1;
SELECT COUNT(*) FROM episode_substances WHERE episode_id = 1;
```
