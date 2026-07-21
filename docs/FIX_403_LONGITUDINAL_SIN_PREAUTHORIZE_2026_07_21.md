# Fix 403 longitudinal episodios

Se elimina @PreAuthorize del DemandController para evitar doble validación en endpoints longitudinales.

La seguridad del módulo Demanda queda centralizada en SecurityConfig:

```java
.requestMatchers("/api/v1/demand/**").authenticated()
```

El token JWT sigue siendo obligatorio.

Endpoints afectados:

- GET /api/v1/demand/episodes/{id}/longitudinal
- GET /api/v1/demand/episodes/by-rut/{rut}/longitudinal
- GET /api/v1/demand/episodes
- GET /api/v1/demand/episodes/{id}
- GET /api/v1/demand/episodes/active/by-rut/{rut}

Si el endpoint responde 404 después de este cambio, la seguridad ya fue superada y el problema será de datos.
