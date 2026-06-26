# Ajuste login: JWT y respuesta enriquecida

## Objetivo

El endpoint `POST /auth/login` ahora retorna el token JWT y, además, la información completa necesaria para inicializar el frontend sin tener que realizar llamadas adicionales inmediatas para usuario, roles y programas asociados.

Se mantiene compatibilidad hacia atrás porque el campo `token` sigue existiendo.

## Endpoint

```http
POST /auth/login
Content-Type: application/json
```

### Request

```json
{
  "email": "admin@demo.com",
  "password": "Admin123$"
}
```

### Response esperado

```json
{
  "authenticated": true,
  "result": "OK",
  "message": "Login correcto",
  "tokenType": "Bearer",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresInMs": 3600000,
  "expiresAt": "2026-06-26T16:30:00Z",
  "user": {
    "id": 1,
    "firstName": "Admin",
    "secondName": null,
    "firstLastName": null,
    "secondLastName": null,
    "fullName": "Admin",
    "email": "admin@demo.com",
    "username": "admin",
    "rut": null
  },
  "roles": [
    {
      "id": 1,
      "name": "ADMIN",
      "code": "ADMIN",
      "description": null,
      "active": true,
      "assignedByUserId": null
    }
  ],
  "programs": [
    {
      "id": 1,
      "name": "Programa ejemplo",
      "populationTypeId": 1,
      "populationTypeName": "Adulto",
      "modalityId": 1,
      "modalityName": "Ambulatorio",
      "planId": 1,
      "planName": "Plan general",
      "regionId": 12,
      "regionName": "Magallanes y de la Antártica Chilena",
      "cityId": 1,
      "cityName": "Punta Arenas",
      "address": "",
      "phone": "",
      "email": "",
      "description": "",
      "active": true,
      "isActive": true,
      "isSupervisor": false,
      "canReceiveReferences": true,
      "canManageDemands": true,
      "canViewDashboard": false,
      "roleInProgram": "PROFESIONAL"
    }
  ],
  "authorities": [
    "ROLE_ADMIN"
  ],
  "claims": {
    "userId": 1,
    "email": "admin@demo.com",
    "username": "admin",
    "rut": null,
    "fullName": "Admin",
    "roles": [
      {
        "id": 1,
        "name": "ADMIN",
        "code": "ADMIN"
      }
    ],
    "authorities": [
      "ROLE_ADMIN"
    ],
    "programIds": [
      1
    ],
    "programs": [
      {
        "id": 1,
        "name": "Programa ejemplo",
        "isSupervisor": false,
        "canReceiveReferences": true,
        "canManageDemands": true,
        "canViewDashboard": false,
        "roleInProgram": "PROFESIONAL"
      }
    ]
  }
}
```

## Información incluida dentro del JWT

El JWT ahora incluye claims operacionales:

- `userId`
- `email`
- `username`
- `rut`
- `fullName`
- `roles`
- `authorities`
- `programIds`
- `programs` con permisos por programa

No se incluye la contraseña ni hashes de contraseña.

## Endpoint complementario

Se agregó:

```http
GET /auth/me
Authorization: Bearer <TOKEN>
```

Retorna la información vigente del usuario autenticado, roles, programas, permisos y claims, pero sin generar un token nuevo.

## Archivos modificados

- `src/main/java/com/cosam/project01/security/controller/AuthController.java`
- `src/main/java/com/cosam/project01/security/dto/AuthResponse.java`
- `src/main/java/com/cosam/project01/security/dto/AuthUserDTO.java`
- `src/main/java/com/cosam/project01/security/dto/AuthRoleDTO.java`
- `src/main/java/com/cosam/project01/security/dto/AuthProgramDTO.java`
- `src/main/java/com/cosam/project01/security/JwtService.java`
- `src/main/java/com/cosam/project01/repository/UserRoleRepository.java`
- `src/main/java/com/cosam/project01/repository/UserProgramRepository.java`
- `src/main/java/com/cosam/project01/security/seeder/DataSeeder.java`

## Actualización 26-06-2026: refreshToken

El endpoint `POST /auth/login` ahora incluye `refreshToken` en la respuesta, manteniendo compatibilidad con el campo `token`.

Ejemplo:

```json
{
  "authenticated": true,
  "result": "OK",
  "message": "Login correcto",
  "tokenType": "Bearer",
  "token": "ACCESS_TOKEN_JWT",
  "refreshToken": "REFRESH_TOKEN",
  "expiresInMs": 3600000,
  "expiresAt": "2026-06-26T16:34:11Z",
  "user": {
    "id": 1,
    "email": "admin@demo.com",
    "username": "admin",
    "fullName": "Admin"
  },
  "roles": [],
  "authorities": [],
  "programs": []
}
```

Nuevo endpoint de renovación:

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
  "expiresAt": "2026-06-26T17:34:11Z"
}
```
