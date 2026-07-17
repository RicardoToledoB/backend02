# Ajuste contacts / referente por postulante

Fecha: 2026-07-17

## Solicitud

Modificar el mantenedor/entidad `contacts` para:

- Persistir `cellphone`.
- Persistir `email`.
- Guardar correctamente la relación con `postulant_id`.
- Crear endpoint de consulta de referente por postulante.

## Cambios de backend

### Tabla `contacts`

Con `spring.jpa.hibernate.ddl-auto=update`, Hibernate agregará las columnas nuevas si no existen:

```sql
ALTER TABLE contacts ADD COLUMN cellphone VARCHAR(60) NULL;
ALTER TABLE contacts ADD COLUMN email VARCHAR(180) NULL;
```

La columna `postulant_id` ya existía en el modelo y queda correctamente mapeada contra `postulants(id)`.

### DTO

`ContactDTO` ahora soporta:

```json
{
  "id": 1,
  "name": "Referente familiar",
  "description": "Madre del usuario",
  "cellphone": "+56 9 1234 5678",
  "email": "referente@correo.cl",
  "postulantId": 10,
  "postulant": {
    "id": 10,
    "rut": "11.799.136-9"
  }
}
```

Para frontend se recomienda enviar `postulantId` como campo simple.

### Crear contacto/referente

```http
POST /api/v1/contacts
Authorization: Bearer TOKEN
Content-Type: application/json
```

```json
{
  "name": "María González",
  "description": "Referente del postulante",
  "cellphone": "+56 9 1234 5678",
  "email": "maria.gonzalez@redsalud.gob.cl",
  "postulantId": 10
}
```

También se mantiene compatibilidad con:

```json
{
  "name": "María González",
  "cellphone": "+56 9 1234 5678",
  "email": "maria.gonzalez@redsalud.gob.cl",
  "postulant": { "id": 10 }
}
```

### Consultar referente por postulante

```http
GET /api/v1/contacts/by-postulant/{postulantId}
```

Retorna el último contacto activo creado para el postulante.

Respuesta:

```json
{
  "id": 5,
  "name": "María González",
  "description": "Referente del postulante",
  "cellphone": "+56 9 1234 5678",
  "email": "maria.gonzalez@redsalud.gob.cl",
  "postulantId": 10,
  "postulant": {
    "id": 10,
    "rut": "11.799.136-9"
  }
}
```

Si no existe referente activo, retorna `404`.

### Endpoint adicional de apoyo

```http
GET /api/v1/contacts/by-postulant/{postulantId}/all
```

Retorna todos los contactos activos asociados al postulante, ordenados desde el más reciente.

### Seguridad

Se habilitó `/api/v1/contacts/**` para:

- `ROLE_ADMIN`
- `ROLE_ADMINISTRATIVO`
- `ROLE_SUPERVISOR`
- `ROLE_PROFESIONAL`
