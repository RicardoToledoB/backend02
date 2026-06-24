# Cambios incorporados en rediseño de backend

## 1. Nuevo módulo `/api/v1/demand`

Se creó un módulo independiente para el nuevo flujo de gestión de demanda de tratamiento de drogas, sin eliminar el código antiguo. Esto permite migración progresiva desde `registers` hacia el nuevo modelo.

## 2. Nuevas entidades principales

- `EpisodeEntity`
- `EpisodeStageEntity`
- `EpisodeEventEntity`
- `EpisodeReferenceEntity`
- `EpisodeDocumentEntity`
- `EpisodeAlertEntity`
- `EpisodeAuditLogEntity`
- `EpisodeSubstanceEntity`

## 3. Nuevos catálogos

- Tipos de episodio
- Tipos de evento
- Estados de asistencia
- Motivos de cierre
- Población, modalidad y plan de programas
- Región y ciudad
- Reglas de semáforo

## 4. Reglas funcionales implementadas

- Un solo episodio activo por persona.
- Si existe episodio activo, no se crea otro.
- Nuevo episodio permitido después de egreso o cierre.
- Referencia entre programas crea una nueva etapa, no un nuevo episodio.
- La fecha original de solicitud se mantiene durante todo el episodio.
- Los días acumulados no se reinician por referencia.
- Ingreso a tratamiento detiene el conteo de espera.
- Egreso cierra el episodio.
- Cierre por inasistencias se activa con dos inasistencias del mismo profesional.
- Acciones críticas exigen `confirmImpact=true`.
- Reversión queda restringida a perfil `ADMIN` o `SUPERVISOR`.
- Auditoría obligatoria para acciones críticas.

## 5. Mejoras técnicas adicionales

- `ProgramEntity` fue ampliada con región, ciudad, población, modalidad, plan, dirección, correo, teléfono, descripción y activo.
- `ProgramServiceImpl` ahora permite guardar esos campos.
- Se corrigieron queries de búsqueda con parámetros mal nombrados en repositorios antiguos.
- Se agregó `@Modifying` faltante en `UserRoleRepository`.
- Se evita exponer passwords en mapeos DTO existentes.
- Se agregó semilla de roles `SUPERVISOR` y `PROFESIONAL`.

## 6. Pendientes sugeridos

- Conectar carga física de documentos con almacenamiento local, MinIO, S3 o repositorio institucional.
- Crear pruebas unitarias/integración cuando exista Maven disponible en el ambiente.
- Definir catálogo definitivo de resultados y estados clínico-operativos.
- Crear migración formal desde `registers`, `movements`, `registers_movements` y `registers_substances` hacia el nuevo modelo.
