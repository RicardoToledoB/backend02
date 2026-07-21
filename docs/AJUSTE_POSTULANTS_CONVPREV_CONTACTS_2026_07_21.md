# Ajuste backend - Postulante con convenio previsional y referente único

Fecha: 2026-07-21

## 1. GET /api/v1/postulants/{id}

Se ajusta la respuesta de `GET /api/v1/postulants/{id}` para incluir el convenio previsional del postulante (`convPrev`) y, dentro de este, la institución previsional (`intPrev`).

Ejemplo esperado:

```json
{
  "id": 1,
  "rut": "11.799.136-9",
  "firstName": "Patricio",
  "convPrevId": 1,
  "convPrev": {
    "id": 1,
    "code": "FONASA_A",
    "name": "Fonasa A",
    "intPrevId": 1,
    "intPrevCode": "FONASA",
    "intPrevName": "Fonasa",
    "intPrev": {
      "id": 1,
      "code": "FONASA",
      "name": "Fonasa"
    }
  }
}
```

## 2. Cómo enviar convPrev al crear o editar postulante

El frontend puede usar el campo simple recomendado:

```json
{
  "rut": "11.799.136-9",
  "firstName": "Patricio",
  "firstLastName": "Jara",
  "convPrevId": 1
}
```

También se mantiene compatibilidad con objeto anidado:

```json
{
  "rut": "11.799.136-9",
  "firstName": "Patricio",
  "firstLastName": "Jara",
  "convPrev": { "id": 1 }
}
```

## 3. Referente único del postulante en /contacts

La asociación del referente al postulante en `POST /api/v1/contacts` y `PUT /api/v1/contacts/{id}` se puede realizar de dos formas:

### Forma recomendada para frontend

```json
{
  "name": "María Pérez",
  "cellphone": "912345678",
  "email": "maria@correo.cl",
  "description": "Madre",
  "postulantId": 1
}
```

### Forma compatible con payload anidado

```json
{
  "name": "María Pérez",
  "cellphone": "912345678",
  "email": "maria@correo.cl",
  "description": "Madre",
  "postulant": { "id": 1 }
}
```

El backend resuelve primero `postulantId`; si no viene informado, usa `postulant.id`.

## 4. Endpoints de consulta del referente

```http
GET /api/v1/contacts/by-postulant/{postulantId}
```

Retorna el referente activo asociado al postulante.

```http
GET /api/v1/contacts/by-postulant/{postulantId}/all
```

Endpoint de apoyo para diagnóstico si existiera más de un referente activo asociado al mismo postulante.

## 5. Cambio requerido en base de datos

Antes de levantar el JAR con esta versión, la tabla `postulants` debe tener la columna `conv_prev_id`.

Ver archivo:

```text
sql/2026_07_21_postulants_conv_prev_id.sql
```
