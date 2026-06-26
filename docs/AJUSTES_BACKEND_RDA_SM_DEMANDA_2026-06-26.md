# Ajustes backend RDA-SM Demanda — 26-06-2026

## 1. Permisos de Demanda
Se ajustó `DemandController` para que los endpoints de demanda requieran usuario autenticado (`isAuthenticated()`), evitando 403 por roles con `code = null` o por configuración incompleta de `users_programs` durante pruebas.

El JWT/login sigue retornando roles, authorities y programas. Además, `DataSeeder` normaliza `roles.code` cuando existe el rol por nombre pero el código viene nulo.

Endpoints revisados:

- `GET /api/v1/demand/catalogs`
- `GET /api/v1/demand/persons/rut/{rut}`
- `GET /api/v1/demand/episodes/active/by-rut/{rut}`
- `GET /api/v1/demand/dashboard/supervisor`

## 2. Refresh token
`POST /auth/login` ahora retorna `refreshToken` además del access token.

Nuevo endpoint:

```http
POST /auth/refresh
Content-Type: application/json
```

Body:

```json
{
  "refreshToken": "REFRESH_TOKEN"
}
```

Respuesta:

```json
{
  "tokenType": "Bearer",
  "token": "NUEVO_ACCESS_TOKEN_JWT",
  "refreshToken": "NUEVO_REFRESH_TOKEN",
  "expiresInMs": 3600000,
  "expiresAt": "2026-06-26T16:34:11Z"
}
```

Propiedades nuevas:

```properties
app.jwt.refresh-expiration-ms=${JWT_REFRESH_EXPIRATION_MS:86400000}
```

## 3. Gestión documental
Se implementaron endpoints completos para documentos:

```http
GET    /api/v1/demand/episodes/{episodeId}/documents
POST   /api/v1/demand/episodes/{episodeId}/documents
GET    /api/v1/demand/documents/{documentId}/download
PUT    /api/v1/demand/documents/{documentId}
DELETE /api/v1/demand/documents/{documentId}
PUT    /api/v1/demand/documents/{documentId}/replace
```

### Subida multipart

```http
POST /api/v1/demand/episodes/{episodeId}/documents
Content-Type: multipart/form-data
```

Campos:

- `file`: archivo PDF u otro documento.
- `documentTypeCode`: obligatorio.
- `stageId`: opcional.
- `eventId`: opcional.
- `referenceId`: opcional.

El backend guarda:

- `mime_type`
- `file_size`
- `original_filename`
- `stored_filename`
- `storage_path`
- `uploaded_by_user_id`
- `uploaded_at`

Propiedad de almacenamiento:

```properties
app.documents.storage-dir=${DOCUMENT_STORAGE_DIR:./storage/demand-documents}
```

En producción:

```properties
app.documents.storage-dir=${DOCUMENT_STORAGE_DIR:/var/www/gestiondemanda-api/documents}
```

### Descarga / visualización

```http
GET /api/v1/demand/documents/{documentId}/download?disposition=inline
GET /api/v1/demand/documents/{documentId}/download?disposition=attachment
```

- `inline`: permite visualizar PDF en navegador.
- `attachment`: fuerza descarga.

## 4. Reemplazo de archivo
Se definió el modelo oficial:

1. El documento anterior se marca con `deleted_at`.
2. Se sube un nuevo documento.
3. Se registra auditoría en `episode_audit_logs`.

Endpoint:

```http
PUT /api/v1/demand/documents/{documentId}/replace
Content-Type: multipart/form-data
```

Campos:

- `file`: obligatorio.
- `documentTypeCode`: opcional; si no se envía, conserva el tipo anterior.
- `stageId`: opcional.
- `eventId`: opcional.
- `referenceId`: opcional.

## 5. Catálogo de tipos de documento
Se creó tabla y catálogo `document_types`.

Tipos mínimos cargados por seeder:

- `CONSENTIMIENTO`
- `INTERCONSULTA`
- `INFORME_CLINICO`
- `INFORME_SOCIAL`
- `ORDEN_INGRESO`
- `DOCUMENTO_EGRESO`
- `DOCUMENTO_CIERRE`
- `REFERENCIA`
- `OTRO`

Endpoints:

```http
GET /api/v1/demand/document-types
GET /api/v1/demand/catalogs
```

`/catalogs` incluye ahora `documentTypes`.

## 6. Alertas
`/api/v1/demand/catalogs` incluye:

- `alertTypes`
- `priorityLevels`
- `alertStatuses`

Valores iniciales:

```text
alertTypes: ALERTA_ESPERA, ALERTA_INASISTENCIA, ALERTA_DOCUMENTO, ALERTA_REFERENCIA
priorityLevels: BAJA, MEDIA, ALTA, CRITICA
alertStatuses: ACTIVA, GESTIONADA, CERRADA, DESCARTADA
```

El estado por defecto de una alerta nueva es `ACTIVA`.

## 7. Sustancias
Se mantiene compatibilidad con `level`, pero se agregan campos explícitos:

- `primarySubstance`: boolean.
- `useOrder`: int.

Regla oficial:

- `useOrder = 1` o `level = "1"` representa sustancia principal.
- `useOrder = 2` o `level = "2"` representa sustancia secundaria 1.
- `useOrder = 3` o `level = "3"` representa sustancia secundaria 2.

## 8. Citaciones y asistencia
Se confirma como lógica oficial:

- Citación = registro en `episode_events` con `event_type = CITACION`.
- Asistencia/Inasistencia = registro en `episode_events` con `event_type = ASISTENCIA` y `attendance_status_id`.
- No se crean tablas separadas `episode_citations` ni `episode_attendance` en esta etapa.

## 9. Responsable de etapa
Se agregó `episode_stages.responsible_user_id` y se expone en `EpisodeStageDTO.responsibleUser`.

En creación de episodio puede enviarse:

```json
{
  "responsibleUserId": 1
}
```

Si no se envía, se asigna el usuario autenticado.

## 10. Correo / notificación
Se habilitaron endpoints formales:

```http
POST /api/v1/demand/episodes/{episodeId}/send-email
POST /api/v1/demand/documents/{documentId}/send-email
POST /api/v1/demand/notifications/email
```

El envío SMTP real queda pendiente de configuración institucional. Por ahora el endpoint responde:

```json
{
  "sent": false,
  "queued": false,
  "result": "EMAIL_SERVICE_NOT_CONFIGURED",
  "message": "Endpoint habilitado. El envío SMTP real queda pendiente de configuración institucional."
}
```

La solicitud queda registrada en auditoría como `SOLICITUD_ENVIO_CORREO`.

## 11. DTO confirmado para crear episodio

```http
POST /api/v1/demand/episodes
Content-Type: application/json
Authorization: Bearer ACCESS_TOKEN
```

Body:

```json
{
  "postulantId": 1,
  "episodeTypeId": 1,
  "episodeTypeCode": "PRIMERA_SOLICITUD",
  "originalRequestDate": "2026-06-26",
  "initialProgramId": 1,
  "responsibleUserId": 1,
  "contactTypeId": 1,
  "senderId": 1,
  "diverterId": 1,
  "contactId": 1,
  "initialObservation": "Observación inicial"
}
```

Obligatorios:

- `postulantId`
- `initialProgramId`

`createdByUserId` se toma desde el token.

## 12. Búsqueda por RUT

```http
GET /api/v1/demand/persons/rut/{rut}
```

- Busca en `postulants.rut`.
- Permiso: usuario autenticado.
- Responde `200` si encuentra persona.
- Responde `404` si no existe.
- El formato esperado es el mismo almacenado en `postulants.rut`. Se recomienda que frontend mantenga un formato único institucional.

## 13. Episodio activo por RUT

```http
GET /api/v1/demand/episodes/active/by-rut/{rut}
```

- Permiso: usuario autenticado.
- Responde `200` si hay episodio activo.
- Responde `404` si no hay episodio activo.
- Se mantiene la regla funcional: una persona puede tener muchos episodios históricos, pero solo un episodio activo a la vez.

## 14. Reglas funcionales oficiales

- Usar `Egreso`, no `Alta`.
- Ingreso a tratamiento detiene KPI de espera.
- Egreso cierra episodio.
- Cierre por inasistencias cierra episodio.
- No es perfil cierra episodio.
- No corresponde cierra episodio con causal.
- Referencia no cierra episodio; cierra etapa origen y crea etapa destino, manteniendo fecha original y días acumulados.

## 15. Nota de compilación
No fue posible compilar dentro del entorno de generación porque el wrapper intenta descargar Maven desde internet. Compilar en el equipo/servidor con:

```bash
./mvnw clean compile
```
