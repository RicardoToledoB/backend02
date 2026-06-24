# Configuración CORS, perfiles y puerto de producción

## Dominios definidos

- Backend API producción: `https://gestiondemanda-api.dssm.cl`
- Frontend producción: `https://gestiondemanda.dssm.cl`
- Frontend local Angular: `http://localhost:4200` o `http://127.0.0.1:4200`
- Frontend local en red: `http://192.168.*:*`

## Puerto interno del backend

El backend queda configurado por defecto en el puerto interno `8095`, para no ocupar puertos ya utilizados en el servidor:

- 8080 ocupado
- 8086 ocupado
- 8888 ocupado

Configuración común:

```properties
server.port=${SERVER_PORT:8095}
```

En producción se recomienda levantar el backend solo en localhost:

```properties
server.address=${SERVER_ADDRESS:127.0.0.1}
server.port=${SERVER_PORT:8095}
```

Luego Nginx o Apache debe publicar `https://gestiondemanda-api.dssm.cl` y redirigir internamente hacia:

```text
http://127.0.0.1:8095
```

## Perfiles disponibles

### 1. Desarrollo H2

Perfil por defecto:

```bash
./mvnw spring-boot:run
```

Equivale a:

```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

Usa H2 en memoria.

### 2. Desarrollo con MySQL local

```bash
SPRING_PROFILES_ACTIVE=local-mysql ./mvnw spring-boot:run
```

Variables opcionales:

```bash
export DB_URL='jdbc:mysql://localhost:3306/demanda_drogas?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true'
export DB_USERNAME='demanda_user'
export DB_PASSWORD='Demanda123$'
export SERVER_PORT=8095
```

### 3. Producción

```bash
SPRING_PROFILES_ACTIVE=prod java -jar backend.jar
```

Variables recomendadas:

```bash
export SPRING_PROFILES_ACTIVE=prod
export SERVER_ADDRESS=127.0.0.1
export SERVER_PORT=8095
export DB_URL='jdbc:mysql://localhost:3306/demanda_drogas?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true'
export DB_USERNAME='demanda_user'
export DB_PASSWORD='CAMBIAR_PASSWORD_REAL'
export JWT_SECRET='CAMBIAR_POR_SECRETO_LARGO_Y_ALEATORIO_256_BITS'
```

## CORS

El CORS queda centralizado en `CorsConfig.java` y lee esta propiedad:

```properties
app.cors.allowed-origin-patterns=${CORS_ALLOWED_ORIGIN_PATTERNS:http://localhost:4200,http://127.0.0.1:4200,http://192.168.*:*,https://gestiondemanda.dssm.cl,https://gestiondemanda-api.dssm.cl}
```

Para producción estricta se puede definir:

```bash
export CORS_ALLOWED_ORIGIN_PATTERNS='https://gestiondemanda.dssm.cl,https://gestiondemanda-api.dssm.cl'
```

El frontend debe consumir:

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://gestiondemanda-api.dssm.cl'
};
```

En desarrollo local:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8095'
};
```

O, si se prueba desde otro equipo de la red:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://IP_DEL_BACKEND:8095'
};
```

## Ejemplo Nginx para backend

```nginx
server {
    listen 80;
    server_name gestiondemanda-api.dssm.cl;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl http2;
    server_name gestiondemanda-api.dssm.cl;

    ssl_certificate     /etc/letsencrypt/live/gestiondemanda-api.dssm.cl/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/gestiondemanda-api.dssm.cl/privkey.pem;

    location / {
        proxy_pass http://127.0.0.1:8095;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## Ejemplo systemd

```ini
[Unit]
Description=Gestion Demanda API
After=network.target mysql.service

[Service]
User=ricardo
WorkingDirectory=/var/www/html/gestiondemanda-api
ExecStart=/usr/bin/java -jar /var/www/html/gestiondemanda-api/backend.jar
Restart=always
RestartSec=10
Environment=SPRING_PROFILES_ACTIVE=prod
Environment=SERVER_ADDRESS=127.0.0.1
Environment=SERVER_PORT=8095
Environment=DB_URL=jdbc:mysql://localhost:3306/demanda_drogas?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true
Environment=DB_USERNAME=demanda_user
Environment=DB_PASSWORD=CAMBIAR_PASSWORD_REAL
Environment=JWT_SECRET=CAMBIAR_POR_SECRETO_LARGO_Y_ALEATORIO_256_BITS
Environment=CORS_ALLOWED_ORIGIN_PATTERNS=https://gestiondemanda.dssm.cl,https://gestiondemanda-api.dssm.cl

[Install]
WantedBy=multi-user.target
```

## Pruebas rápidas

Login local:

```bash
curl -X POST http://localhost:8095/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@demo.com","password":"Admin123$"}'
```

Swagger local:

```text
http://localhost:8095/docs
```

Producción:

```text
https://gestiondemanda-api.dssm.cl/docs
```
