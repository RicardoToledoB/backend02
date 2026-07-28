# Fix 403 en referencias y cierre

Fecha: 2026-07-28

## Problema

Los endpoints:

- `POST /api/v1/demand/episodes/{id}/references`
- `POST /api/v1/demand/episodes/{id}/close`

respondían `403 Forbidden` sin cuerpo aun utilizando token Bearer válido de administrador.

## Ajuste aplicado

Se agregaron estos endpoints al `SecurityFilterChain` con `permitAll()` para evitar el falso `403` de la cadena web, manteniendo validación manual obligatoria del Bearer token en el controlador mediante `RequestTokenValidator`.

Esto mantiene los endpoints protegidos: sin token válido responden `401`.

También se agregó `/error` como público para que errores internos no vuelvan a disfrazarse como `403`.

## Cierre

`CloseEpisodeRequest.closureDate` ahora acepta:

- `YYYY-MM-DD`
- `YYYY-MM-DDTHH:mm:ss`

Ejemplo mínimo válido:

```json
{
  "closureDate": "2026-07-27"
}
```

Si no se informa causal de cierre, el backend usa una causal activa por defecto (`OTRO_CIERRE`; si no existe, `ABANDONO`).

